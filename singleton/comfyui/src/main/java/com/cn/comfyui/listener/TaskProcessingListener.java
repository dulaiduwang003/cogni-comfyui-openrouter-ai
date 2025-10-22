package com.cn.comfyui.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.comfyui.constant.ComfyuiConstant;
import com.cn.common.enums.ComfyuiWorksTypeEnum;
import com.cn.comfyui.excepitons.ComfyuiException;
import com.cn.comfyui.model.TaskNodeContainer;
import com.cn.comfyui.model.WorkflowResultModel;
import com.cn.comfyui.structure.TaskInfoStructure;
import com.cn.comfyui.structure.TaskStructure;
import com.cn.comfyui.websocket.service.TaskProgressPushService;
import com.cn.common.configuration.ComfyuiConfiguration;
import com.cn.common.entity.Workflow;
import com.cn.common.entity.WorkflowOutput;
import com.cn.common.entity.WorkflowResult;
import com.cn.common.enums.FilePathEnum;
import com.cn.common.enums.TaskStatusEnum;
import com.cn.common.mapper.WorkflowMapper;
import com.cn.common.mapper.WorkflowOutputMapper;
import com.cn.common.mapper.WorkflowResultMapper;
import com.cn.common.utils.RedisUtils;
import com.cn.common.utils.StringUtils;
import com.cn.common.utils.UploadUtil;
import com.cn.common.utils.CreditUtils;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.alibaba.fastjson2.JSON.parseObject;
import static com.cn.comfyui.constant.ComfyuiConstant.COMFYUI_QUEUE_INDEX;
import static com.cn.comfyui.constant.ComfyuiConstant.COMFYUI_TASK_LIST;
import static com.cn.comfyui.utils.ComfyuiUtils.getBodyError;
import static com.cn.comfyui.utils.ComfyuiUtils.getFileExtensionFromUrl;

/**
 * 绘图任务监听器，负责处理绘图任务队列和任务状态更新
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
@Configuration
public class TaskProcessingListener {

    private final ComfyuiConfiguration confyuiConfiguration;

    private final UploadUtil uploadUtil;

    private final RedisUtils redisUtils;

    private final WorkflowResultMapper worksMapper;

    private final WorkflowOutputMapper workflowsOutputMapper;

    private final WorkflowMapper workflowsMapper;

    private final RedissonClient redissonClient;

    private final TaskProgressPushService taskProgressPushService;

    private final CreditUtils creditUtils;

    private final AtomicInteger anomalyCounters = new AtomicInteger(0);

    private final WebClient webClient;

    private static final Random random = new Random();

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private Thread listenerThread;

    // 添加超时检查线程
    private Thread timeoutCheckThread;

    // 存储活跃任务的线程，用于取消
    private final Map<String, Thread> activeTaskThreads = new ConcurrentHashMap<>();

    // 存储信号量与任务的关联，确保任务完成时释放对应的信号量
    private final Map<String, Semaphore> taskSemaphores = new ConcurrentHashMap<>();

    // 用于轮询选择服务器
    private final AtomicInteger nextServerIndex = new AtomicInteger(0);

    /**
     * 关闭监听器方法
     * 设置运行状态为false并中断监听线程
     * 在容器销毁前调用
     */
    @PreDestroy
    public void shutdown() {
        log.info("正在关闭绘图任务监听器...");
        isRunning.set(false);

        // 取消所有正在执行的任务监控
        log.info("正在取消所有任务监控...");
        for (Map.Entry<String, Thread> entry : activeTaskThreads.entrySet()) {
            entry.getValue().interrupt();
            // 释放所有剩余的信号量
            releaseTaskSemaphore(entry.getKey());
        }
        activeTaskThreads.clear();

        // 中断并等待监听线程
        if (listenerThread != null) {
            listenerThread.interrupt();
            try {
                // 等待线程安全退出
                listenerThread.join(5000);
            } catch (InterruptedException e) {
                log.warn("等待监听线程关闭时被中断", e);
                Thread.currentThread().interrupt();
            }
        }

        // 中断超时检查线程
        if (timeoutCheckThread != null) {
            timeoutCheckThread.interrupt();
            try {
                timeoutCheckThread.join(5000);
            } catch (InterruptedException e) {
                log.warn("等待超时检查线程关闭时被中断", e);
                Thread.currentThread().interrupt();
            }
        }

        // 将所有BUILD WAIT状态的任务改为CANCELED状态
        try {
            log.info("正在处理未完成的绘图任务...");

            // 清空剩余的队列，防止重启后重新处理
            try {
                // 清空队列中所有任务
                while (redisUtils.keySize(ComfyuiConstant.COMFYUI_QUEUE) > 0) {
                    try {
                        redisUtils.listPop(ComfyuiConstant.COMFYUI_QUEUE, 100, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        // 忽略退出循环
                        if (e.getMessage() != null &&
                                (e.getMessage().contains("interrupted") ||
                                        e.getMessage().contains("STOPPED") ||
                                        e.getMessage().contains("LettuceConnectionFactory"))) {
                            log.info("Redis连接已关闭或被中断，停止清空队列");
                            break;
                        }
                        // 其他异常打印警告并退出循环
                        log.warn("清空队列时出错: {}", e.getMessage());
                        break;
                    }
                }
                log.info("已清空队列中的所有任务");

                // 清空队列索引
                try {
                    redisUtils.getRedisTemplate().delete(COMFYUI_QUEUE_INDEX);
                    log.info("已清空队列索引");
                } catch (Exception e) {
                    if (e.getMessage() != null &&
                            (e.getMessage().contains("STOPPED") ||
                                    e.getMessage().contains("LettuceConnectionFactory") ||
                                    e.getMessage().contains("interrupted"))) {
                        log.info("Redis连接已关闭或被中断，无法清空队列索引");
                    } else {
                        log.warn("清空队列索引时出错: {}", e.getMessage());
                    }
                }
            } catch (Exception e) {
                if (e.getMessage() != null &&
                        (e.getMessage().contains("STOPPED") ||
                                e.getMessage().contains("LettuceConnectionFactory") ||
                                e.getMessage().contains("interrupted"))) {
                    log.info("Redis连接已关闭或被中断，无法清空队列");
                } else {
                    log.warn("清空队列时出错: {}", e.getMessage());
                }
            }

            //
            try {
                RKeys rKeys = redissonClient.getKeys();
                Iterable<String> keysByPattern = rKeys.getKeysByPattern(COMFYUI_TASK_LIST + "*");

                for (String taskListKey : keysByPattern) {
                    try {
                        // 使用RedisUtils获取该用户的所有任务
                        Map<Object, Object> taskMap = redisUtils.hashGetAll(taskListKey);
                        if (taskMap == null || taskMap.isEmpty()) {
                            continue;
                        }

                        for (Map.Entry<Object, Object> entry : taskMap.entrySet()) {
                            String taskId = entry.getKey().toString();
                            Object taskObj = entry.getValue();

                            // 类型
                            if (taskObj instanceof TaskInfoStructure) {
                                TaskInfoStructure task = (TaskInfoStructure) taskObj;

                                try {
                                    // 根据任务状态进行不同处理
                                    if (task != null) {
                                        // 如果任务状态为BUILD，也改为CANCELED而不是FAILED
                                        if (TaskStatusEnum.BUILD.getDec().equals(task.getStatus())) {
                                            log.info("将BUILD状态任务标记为取消：{}", taskId);
                                            task.setStatus(TaskStatusEnum.CANCELED.getDec());
                                            redisUtils.hashPut(taskListKey, taskId, task);
                                            
                                            // 退还冻结的积分
                                            if (task.getCreditsDeducted() != null && task.getCreditsDeducted() > 0) {
                                                try {
                                                    Long userId = Long.valueOf(taskListKey.replace(COMFYUI_TASK_LIST, ""));
                                                    creditUtils.refundCredits(userId, task.getCreditsDeducted(), 
                                                        "应用关闭任务取消积分退还 - " + task.getWorkflowName());
                                                } catch (Exception creditEx) {
                                                    log.warn("退还任务{}积分时出错: {}", taskId, creditEx.getMessage());
                                                }
                                            }
                                        }
                                        // 如果任务状态为WAIT，则更改为CANCELED
                                        else if (TaskStatusEnum.WAIT.getDec().equals(task.getStatus())) {
                                            log.info("将WAIT状态任务标记为取消：{}", taskId);
                                            task.setStatus(TaskStatusEnum.CANCELED.getDec());
                                            redisUtils.hashPut(taskListKey, taskId, task);
                                            
                                            // 退还冻结的积分
                                            if (task.getCreditsDeducted() != null && task.getCreditsDeducted() > 0) {
                                                try {
                                                    Long userId = Long.valueOf(taskListKey.replace(COMFYUI_TASK_LIST, ""));
                                                    creditUtils.refundCredits(userId, task.getCreditsDeducted(), 
                                                        "应用关闭任务取消积分退还 - " + task.getWorkflowName());
                                                } catch (Exception creditEx) {
                                                    log.warn("退还任务{}积分时出错: {}", taskId, creditEx.getMessage());
                                                }
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    // 捕获可能的Redis连接关闭异常
                                    if (e.getMessage() != null &&
                                            (e.getMessage().contains("STOPPED")
                                                    || e.getMessage().contains("LettuceConnectionFactory"))) {
                                        log.info("Redis连接已关闭，无法更新任务状态: {}", taskId);
                                        break;
                                    }
                                    log.warn("更新任务{}状态时出错: {}", taskId, e.getMessage());
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 捕获可能的Redis连接关闭异常
                        if (e.getMessage() != null &&
                                (e.getMessage().contains("STOPPED")
                                        || e.getMessage().contains("LettuceConnectionFactory"))) {
                            log.info("Redis连接已关闭，停止处理未完成任务");
                            break; // 退出循环，不再处理其他任务列表
                        }
                        log.error("处理任务列表{}时出错: {}", taskListKey, e.getMessage());
                    }
                }
            } catch (Exception e) {
                // 捕获可能的Redis/Redisson连接关闭异常
                if (e.getMessage() != null &&
                        (e.getMessage().contains("STOPPED") || e.getMessage().contains("LettuceConnectionFactory"))) {
                    log.info("Redis/Redisson连接已关闭，无法处理未完成任务");
                } else {
                    log.error("获取任务列表时出错: {}", e.getMessage());
                }
            }

            log.info("未完成的绘图任务处理完毕");
        } catch (Exception e) {
            // 捕获顶层异常
            if (e.getMessage() != null &&
                    (e.getMessage().contains("STOPPED") || e.getMessage().contains("LettuceConnectionFactory"))) {
                log.info("Redis连接已关闭，无法处理未完成任务");
            } else {
                log.error("处理未完成任务时发生错误: {}", e.getMessage());
            }
        }

        log.info("绘图任务监听器已关闭");
    }

    /**
     * 应用关闭事件监听器
     * 当Spring容器关闭时调用shutdown方法
     */
    @EventListener(ContextClosedEvent.class)
    public void onApplicationShutdown() {
        shutdown();
    }

    /**
     * 应用就绪事件监听器
     * 启动任务监听线程，开始处理绘图任务队列
     * 使用信号量控制并发任务数量
     */
    @EventListener(ApplicationReadyEvent.class)
    public void listening() {
        try {
            // 获取服务器列表
            List<ComfyuiConfiguration.ServerConfig> servers = confyuiConfiguration.getServer();
            if (servers == null || servers.isEmpty()) {
                log.error("ComfyUI服务器地址列表为空，监听器无法启动。");
                return;
            }
            // 获取服务器列表
            List<String> serverName = servers.stream().map(ComfyuiConfiguration.ServerConfig::getName).toList();
            if (serverName.isEmpty()) {
                log.error("ComfyUI服务器URL列表为空，监听器无法启动。");
                return;
            }
            log.info("可用的ComfyUI服务器列表: {}", serverName);

            // 创建信号量，控制并发任务数
            // 并发数直接等于服务器数量
            int concurrentTasks = serverName.size();

            log.info("实际并发处理任务数: {}", concurrentTasks);
            Semaphore semaphore = new Semaphore(concurrentTasks);

            // 使用虚拟线程替代普通线程
            listenerThread = Thread.ofVirtual().name("comfyui-listener-thread").start(() -> {
                log.info("绘图任务监听器已启动");
                while (isRunning.get()) {
                    try {
                        // 尝试获取信号量，如果没有可用的信号量，则阻塞
                        if (semaphore.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                            try {
                                // 获取任务
                                TaskStructure task = null;

                                // 使用与取消任务相同的锁，确保获取任务和取消任务操作互斥
                                RLock globalLock = redissonClient.getLock(ComfyuiConstant.COMFYUI_GLOBAL_SUBMIT_LOCK);
                                try {
                                    // 尝试获取锁
                                    if (globalLock.tryLock(2, 2, TimeUnit.SECONDS)) {
                                        try {
                                            task = (TaskStructure) redisUtils
                                                    .listPop(ComfyuiConstant.COMFYUI_QUEUE, 1, TimeUnit.SECONDS);
                                        } catch (Exception e) {
                                            // 检查是否是由于应用关闭导致的Redis命令中断异常
                                            if (e.getCause() instanceof InterruptedException ||
                                                    (e.getMessage() != null
                                                            && e.getMessage().contains("interrupted"))) {
                                                if (!isRunning.get()) {
                                                    log.info("Redis命令被中断，应用正在关闭");
                                                    semaphore.release(); // 释放信号量
                                                    Thread.currentThread().interrupt();
                                                    break;
                                                }
                                            } else {
                                                throw e; // 重新抛出其他类型的异常
                                            }
                                        } finally {
                                            // 检查线程是否已被中断，如果是，则不尝试解锁
                                            if (!Thread.currentThread().isInterrupted()
                                                    && globalLock.isHeldByCurrentThread()) {
                                                try {
                                                    globalLock.unlock();
                                                } catch (Exception e) {
                                                    if (isRunning.get()) {
                                                        log.error("释放任务锁时发生异常: {}", e.getMessage());
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } catch (InterruptedException e) {
                                    log.info("获取任务锁时被中断");
                                    semaphore.release(); // 释放信号量
                                    Thread.currentThread().interrupt();
                                    break;
                                } catch (Exception e) {
                                    // 检查是否是由于应用关闭导致的Redis相关异常
                                    if (e.getMessage() != null &&
                                            (e.getMessage().contains("interrupted") ||
                                                    e.getMessage().contains("STOPPED") ||
                                                    e.getMessage().contains("LettuceConnectionFactory"))) {
                                        if (!isRunning.get()) {
                                            log.info("获取任务锁时Redis连接中断，应用正在关闭");
                                            semaphore.release(); // 释放信号量
                                            break;
                                        }
                                    }
                                    log.error("获取任务锁时发生异常: {}", e.getMessage(), e);
                                    semaphore.release(); // 释放信号量
                                }

                                // 如果成功获取到任务，则执行
                                if (task != null) {
                                    // 关联任务与信号量
                                    taskSemaphores.put(task.getTaskId(), semaphore);
                                    // 选择一个服务器执行任务
                                    ComfyuiConfiguration.ServerConfig selectedServerConfig = selectNextServer();
                                    log.info("正在处理任务:{}，服务器: {}", task.getTaskId(), selectedServerConfig.getName());
                                    execute(task, selectedServerConfig);
                                } else {
                                    // 如果没有获取到任务，释放信号量
                                    semaphore.release();
                                    // 短暂休眠避免空转
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        break;
                                    }
                                }
                            } catch (Exception e) {
                                // 确保任何异常情况下都释放信号量
                                semaphore.release();
                                if (isRunning.get()) {
                                    log.error("任务处理异常: {}", e.getMessage(), e);
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        log.info("任务监听线程被中断");
                        break;
                    } catch (Exception e) {
                        if (isRunning.get()) {
                            log.error("任务处理异常: {}", e.getMessage(), e);
                        }
                    }
                }
                log.info("绘图任务监听线程已退出");
            });

            // 启动任务超时检查线程
            startTimeoutCheckThread();

        } catch (Exception e) {
            log.error("任务监听器初始化异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 启动任务超时检查线程
     * 定期检查所有BUILD状态的任务，将超时任务标记为FAILED
     */
    private void startTimeoutCheckThread() {
        // 虚拟线程
        timeoutCheckThread = Thread.ofVirtual().name("comfyui-timeout-check-thread").start(() -> {
            log.info("任务超时检查线程已启动");
            while (isRunning.get()) {
                try {
                    // 每分钟检查一次
                    Thread.sleep(60000);

                    if (!isRunning.get()) {
                        break;
                    }

                    // 获取配置的超时时间（分钟）
                    int timeoutMinutes = Math.toIntExact(confyuiConfiguration.getTask().getTimeoutCheckInterval());
                    if (timeoutMinutes <= 0) {
                        // 如果配置为0或负数，则不进行超时检查
                        continue;
                    }

                    try {
                        // 使用Redisson的keys命令获取所有符合模式的键名
                        RKeys rKeys = redissonClient.getKeys();
                        Iterable<String> keysByPattern = rKeys.getKeysByPattern(COMFYUI_TASK_LIST + "*");

                        for (String taskListKey : keysByPattern) {
                            try {
                                // 使用RedisUtils获取该用户的所有任务
                                Map<Object, Object> taskMap = redisUtils.hashGetAll(taskListKey);
                                if (taskMap == null || taskMap.isEmpty()) {
                                    continue;
                                }

                                for (Map.Entry<Object, Object> entry : taskMap.entrySet()) {
                                    String taskId = entry.getKey().toString();
                                    Object taskObj = entry.getValue();

                                    // 检查是否是DrawingTaskResultStructure类型
                                    if (taskObj instanceof TaskInfoStructure) {
                                        TaskInfoStructure task = (TaskInfoStructure) taskObj;

                                        // 只检查BUILD状态的任务
                                        if (task != null && TaskStatusEnum.BUILD.getDec().equals(task.getStatus())) {
                                            // 计算任务已运行时间
                                            LocalDateTime now = LocalDateTime.now();
                                            LocalDateTime createTime = task.getCreateTime();

                                            if (createTime != null) {
                                                // 计算时间差（分钟）
                                                long runningMinutes = Duration.between(createTime, now).toMinutes();

                                                // 如果超过配置的超时时间，则标记为失败
                                                if (runningMinutes >= timeoutMinutes) {
                                                    log.info("任务 {} 已超时 {} 分钟，自动标记为失败", taskId, runningMinutes);
                                                    task.setStatus(TaskStatusEnum.FAILED.getDec());
                                                    // 使用RedisUtils更新任务状态
                                                    redisUtils.hashPut(taskListKey, taskId, task);
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // 处理Redis连接关闭异常
                                if (e.getMessage() != null &&
                                        (e.getMessage().contains("STOPPED")
                                                || e.getMessage().contains("LettuceConnectionFactory"))) {
                                    log.debug("Redis连接已关闭，停止超时检查");
                                    return; // 直接退出整个线程
                                }

                                if (isRunning.get()) {
                                    log.error("检查任务列表 {} 超时状态时出错: {}", taskListKey, e.getMessage());
                                }
                            }
                        }
                    } catch (Exception e) {
                        // 处理Redis/Redisson连接关闭异常
                        if (e.getMessage() != null &&
                                (e.getMessage().contains("STOPPED")
                                        || e.getMessage().contains("LettuceConnectionFactory"))) {
                            log.debug("Redis/Redisson连接已关闭，停止超时检查");
                            return; // 直接退出整个线程
                        }

                        if (isRunning.get()) {
                            log.error("获取任务列表时出错: {}", e.getMessage());
                        }
                    }
                } catch (InterruptedException e) {
                    log.info("任务超时检查线程被中断");
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // 处理顶层异常中的Redis连接关闭情况
                    if (e.getMessage() != null &&
                            (e.getMessage().contains("STOPPED")
                                    || e.getMessage().contains("LettuceConnectionFactory"))) {
                        log.debug("Redis连接已关闭，停止超时检查");
                        break;
                    }

                    if (isRunning.get()) {
                        log.error("任务超时检查异常: {}", e.getMessage());
                    }

                    // 发生异常后短暂休眠，避免出现死循环
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            log.info("任务超时检查线程已退出");
        });
    }

    /**
     * 执行绘图任务
     * 将任务提交到绘图服务器并处理响应结果
     * 注意：此方法不再管理信号量的释放，信号量会在任务完全结束后释放
     *
     * @param structure 绘图任务结构体，包含任务ID、用户ID和任务JSON数据
     */
    private void execute(final TaskStructure structure, final ComfyuiConfiguration.ServerConfig selectedServerConfig) {
        if (!isRunning.get()) {
            log.info("应用正在关闭，不再处理新任务");
            // 释放与任务关联的信号量
            releaseTaskSemaphore(structure.getTaskId());
            return;
        }

        try {
            handleComfyuiQueue(structure.getTaskId());

            final Workflow workflow = workflowsMapper.selectOne(new QueryWrapper<Workflow>()
                    .lambda()
                    .eq(Workflow::getId, structure.getWorkflowId())
                    .select(Workflow::getId, Workflow::getJson));
            final JSONObject workflowsJson = JSON.parseObject(workflow.getJson());


            List<TaskNodeContainer> taskNodeContainer = structure.getTaskNodeContainer();
            // 上传资源
            taskNodeContainer.parallelStream()
                    .filter(TaskNodeContainer::getIsUpload) // 只处理需要上传的节点
                    .forEach(node -> {
                        final String fileName = uploadFile(node.getNodeValue(), selectedServerConfig.getUrl());
                        node.setNodeValue(fileName);
                    });
            // 更新节点值

            updateNodeValues(workflowsJson, taskNodeContainer);
            // 构建请求体
            JSONObject body = new JSONObject();
            body.put("client_id", structure.getTaskId());
            body.put("prompt", workflowsJson);
            System.out.println(workflowsJson);

            final String block = webClient.post()
                    .uri(selectedServerConfig.getUrl() + "/prompt")
                    .body(BodyInserters.fromValue(body))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new ComfyuiException(getBodyError(errorBody))))))
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(confyuiConfiguration.getTask().getMaxRetryTime()))
                    .retryWhen(Retry.backoff(confyuiConfiguration.getTask().getMaxRetries(), Duration.ofSeconds(1)))
                    .block();
            JSONObject result = parseObject(block);
            // log.info("任务id:{}, 提交至服务器: {}, 成功:{}", structure.getTaskId(), selectedServer,
            // result);
            log.info("任务id:{}, 提交至服务器: {}", structure.getTaskId(), selectedServerConfig.getName());
            if (result != null) {
                String key;
                key = COMFYUI_TASK_LIST + structure.getUserId();
                final TaskInfoStructure o = (TaskInfoStructure) redisUtils.hashGet(key,
                        structure.getTaskId());
                // 构建中
                TaskInfoStructure updatedTask = o.setProgress(0L)
                        .setStatus(TaskStatusEnum.BUILD.getDec());
                redisUtils.hashPut(key, structure.getTaskId(), updatedTask);

                // 推送任务开始构建状态
                taskProgressPushService.pushTaskStatusChange(structure.getUserId(), structure.getTaskId(), updatedTask);

                inspectionTasks(new TaskExecution()
                        .setWorkflowId(structure.getWorkflowId())
                        .setTaskId(structure.getTaskId())
                        .setUserId(structure.getUserId())
                        .setPromptId(result.getString("prompt_id"))
                        .setSelectedServerConfig(selectedServerConfig));
            } else {
                taskErrorStatus(structure.getUserId(), structure.getTaskId());
                // 任务提交失败，释放信号量
                releaseTaskSemaphore(structure.getTaskId());
            }
        } catch (Exception e) {
            if (isRunning.get()) {
                log.error("执行任务异常, 任务ID: {}, 服务器: {}, 错误: {}", structure.getTaskId(), selectedServerConfig.getName(),
                        e.getMessage(),
                        e);
                taskErrorStatus(structure.getUserId(), structure.getTaskId());
            } else {
                log.info("应用正在关闭，任务执行被中断: {}", structure.getTaskId());
            }
            // 任务执行异常，释放信号量
            releaseTaskSemaphore(structure.getTaskId());
        }
    }

    /**
     * 监控任务执行状态
     * 定期检查任务是否完成
     * 任务完成后会释放与任务关联的信号量
     *
     * @param taskExecution 任务执行对象，包含任务ID、用户ID和提示ID
     */
    private void inspectionTasks(final TaskExecution taskExecution) {
        // 创建原子计数器，用于跟踪重试次数
        AtomicInteger retryCount = new AtomicInteger(0);
        // 最大重试次数，每3秒一次，约5小时
        final int maxRetries = 6000;

        // 使用虚拟线程
        Thread monitorThread = Thread.ofVirtual().name("task-monitor-" + taskExecution.getTaskId()).start(() -> {
            try {
                while (isRunning.get() && retryCount.get() <= maxRetries) {
                    try {
                        // 更新随机进度
                        try {
                            handleRandomVariables(taskExecution.getUserId(), taskExecution.getTaskId());
                        } catch (Exception e) {
                            // 处理可能的Redis中断异常
                            if (e.getMessage() != null &&
                                    (e.getMessage().contains("interrupted") ||
                                            e.getMessage().contains("STOPPED") ||
                                            e.getMessage().contains("LettuceConnectionFactory"))) {
                                if (!isRunning.get()) {
                                    log.info("应用正在关闭，停止更新任务进度: {}", taskExecution.getTaskId());
                                    releaseTaskSemaphore(taskExecution.getTaskId());
                                    return;
                                }
                            } else {
                                log.warn("更新任务进度时出错: {}", e.getMessage());
                            }
                        }

                        // 检查是否应该停止（应用关闭或超过最大重试次数）
                        if (!isRunning.get()) {
                            log.info("应用正在关闭，停止监控任务: {}", taskExecution.getTaskId());
                            releaseTaskSemaphore(taskExecution.getTaskId());
                            return;
                        }

                        if (retryCount.incrementAndGet() > maxRetries) {
                            log.warn("任务 {} 在服务器 {} 监控超过最大重试次数 {}", taskExecution.getTaskId(),
                                    taskExecution.getSelectedServerConfig().getName(), maxRetries);
                            taskErrorStatus(taskExecution.getUserId(), taskExecution.getTaskId());
                            releaseTaskSemaphore(taskExecution.getTaskId());
                            return;
                        }

                        // 检查任务状态
                        final String block = webClient.get()
                                .uri(taskExecution.getSelectedServerConfig().getUrl() + "/history/"
                                        + taskExecution.getPromptId())
                                .retrieve()
                                .bodyToMono(String.class)
                                .timeout(Duration.ofSeconds(confyuiConfiguration.getTask().getMaxRetryTime()))
                                .retryWhen(Retry.backoff(confyuiConfiguration.getTask().getMaxRetries(),
                                        Duration.ofSeconds(1)))
                                .block();

                        if (block == null) {
                            log.warn("任务 {} 获取状态返回为空", taskExecution.getTaskId());
                        } else {
                            JSONObject jsonObject = parseObject(block);
                            if (jsonObject.containsKey(taskExecution.getPromptId())) {
                                log.info("任务id:{}, 在服务器: {} 构建成功", taskExecution.getTaskId(),
                                        taskExecution.getSelectedServerConfig().getName());
                                JSONObject resultJson = jsonObject.getJSONObject(taskExecution.getPromptId());
                                // 保存作品
                                saveWorks(taskExecution.getTaskId(), taskExecution.getWorkflowId(),
                                        taskExecution.getUserId(), resultJson,
                                        taskExecution.getSelectedServerConfig().getUrl());

                                // 任务完成，释放信号量
                                releaseTaskSemaphore(taskExecution.getTaskId());

                                // 任务完成，退出循环
                                return;
                            }
                        }

                        // 休眠3秒，相当于原来的scheduleWithFixedDelay
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.info("任务监控线程被中断: {}", taskExecution.getTaskId());
                        break;
                    } catch (Exception e) {
                        if (isRunning.get()) {
                            log.error("任务状态检查异常, 任务ID: {}, 服务器: {}, 错误: {}", taskExecution.getTaskId(),
                                    taskExecution.getSelectedServerConfig().getName(), e.getMessage(), e);
                            taskErrorStatus(taskExecution.getUserId(), taskExecution.getTaskId());
                            releaseTaskSemaphore(taskExecution.getTaskId());
                            return;
                        }
                    }
                }
            } finally {
                // 任务结束，从活跃任务列表中移除
                activeTaskThreads.remove(taskExecution.getTaskId());
            }
        });

        // 将任务添加到活跃任务列表
        activeTaskThreads.put(taskExecution.getTaskId(), monitorThread);
    }

    /**
     * 保存绘图作品
     * 从结果JSON中提取图片和模型数据，并保存到数据库
     * 添加事务管理，确保多个数据库操作的原子性
     *
     * @param taskId     任务ID
     * @param userId     用户ID
     * @param resultJson 绘图结果JSON对象
     */
    @Transactional(rollbackFor = Exception.class)
    private void saveWorks(final String taskId, final Long workflowId, final Long userId,
            final JSONObject resultJson, final String selectedServerUrl) {
        if (!isRunning.get()) {
            log.info("应用正在关闭，不保存作品: {}", taskId);
            return;
        }

        String fileUrlPrefix = selectedServerUrl + "/view?filename=";
        JSONObject outputs = resultJson.getJSONObject("outputs");
        System.out.println(outputs);
        // 获取输出节点
        WorkflowOutput workflowOutput = workflowsOutputMapper
                .selectOne(new QueryWrapper<WorkflowOutput>()
                        .lambda()
                        .eq(WorkflowOutput::getWorkflowId, workflowId));
        // 解析输出结果
        String nodeKey = workflowOutput.getNodeKey();
        // 创建作品
        final WorkflowResult works = new WorkflowResult()
                .setTaskId(taskId)
                .setUserId(userId);

        // 定义处理器Map
        Map<String, Function<Object[], JSONObject>> processors = Map.of(
                ComfyuiWorksTypeEnum.IMAGE.getDec(), (args) -> processImage(outputs, nodeKey),
                ComfyuiWorksTypeEnum.AUDIO.getDec(), (args) -> processAudio(outputs, nodeKey),
                ComfyuiWorksTypeEnum.MODEL.getDec(), (args) -> processModel(outputs, nodeKey),
                ComfyuiWorksTypeEnum.VIDEO.getDec(), (args) -> processVideo(outputs, nodeKey));

        String type = workflowOutput.getType();
        String fullFileUrl;
        if (processors.containsKey(type)) {
            JSONObject jsonObject = processors.get(type).apply(new Object[] { outputs, nodeKey });
            // 构建完整的 URL
            fullFileUrl = fileUrlPrefix + jsonObject.getString("filename") + "&type=" + jsonObject.getString("type");
            if (StringUtils.notEmpty(jsonObject.getString("subfolder"))) {
                fullFileUrl += "&subfolder=" + jsonObject.getString("subfolder");
            }
            works.setType(type); // 直接使用对应的类型
        } else {
            throw new IllegalArgumentException("Unsupported work type: " + type);
        }
        // 上传阿里OSS
        final String uploadUrl = uploadUtil.uploadUrl(fullFileUrl, FilePathEnum.COMFYUI.getDec());
        // 设置redis构建消息
        final String key = COMFYUI_TASK_LIST + userId;
        TaskInfoStructure o = (TaskInfoStructure) redisUtils.hashGet(key, taskId);

        works.setUrl(uploadUrl)
            .setWorkflowName(o.getWorkflowName())
            .setWorkflowId(workflowId);

        // 保存表单参数（包含元数据），用于前端展示和重新制作
        if (o.getForm() != null) {
            works.setFormParams(JSON.parseObject(JSON.toJSONString(o.getForm())));
        }

        worksMapper.insert(works);

        // 任务成功完成，消费冻结的积分
        if (o.getCreditsDeducted() != null && o.getCreditsDeducted() > 0) {
            boolean consumed = creditUtils.consumeCredits(
                userId, 
                o.getCreditsDeducted(), 
                "任务完成积分消费 - " + o.getWorkflowName()
            );
            if (!consumed) {
                log.warn("任务 {} 完成但积分消费失败，用户ID: {}, 积分: {}", 
                    taskId, userId, o.getCreditsDeducted());
            }
        }

        TaskInfoStructure completedTask = o
                // 构建成功
                .setStatus(TaskStatusEnum.SUCCEED.getDec())
                .setProgress(100L)
                .setWorkflowResultModel(new WorkflowResultModel().setWorkflowResultId(works.getId()).setUrl(works.getUrl()).setType(works.getType()));
        redisUtils.hashPut(key, taskId, completedTask);

        // 统计：今日成功任务数 +1
        recordTaskSuccess();

        // 推送任务完成状态
        taskProgressPushService.pushTaskStatusChange(userId, taskId, completedTask);
    }

    private JSONObject processModel(final JSONObject nodeOutput, final String nodeKey) {

        JSONObject json = nodeOutput.getJSONObject(nodeKey);
        JSONArray images = json.getJSONArray("3d");
        if (images != null && !images.isEmpty()) {

            return images.getJSONObject(0);
        }
        throw new ComfyuiException("解析模型失败");
    }

    private JSONObject processImage(final JSONObject nodeOutput, final String nodeKey) {
        System.out.println(nodeOutput);
        JSONObject json = nodeOutput.getJSONObject(nodeKey);
        JSONArray images = json.getJSONArray("images");
        if (images != null && !images.isEmpty()) {

            return images.getJSONObject(0);
        }
        throw new ComfyuiException("解析图片结果失败");
    }

    private JSONObject processAudio(final JSONObject nodeOutput, final String nodeKey) {
        JSONObject json = nodeOutput.getJSONObject(nodeKey);
        JSONArray images = json.getJSONArray("audio");
        if (images != null && !images.isEmpty()) {

            return images.getJSONObject(0);
        }
        throw new ComfyuiException("解析音频结果失败");
    }

    private JSONObject processVideo(final JSONObject nodeOutput, final String nodeKey) {
        JSONObject json = nodeOutput.getJSONObject(nodeKey);
        JSONArray images = json.getJSONArray("gifs");
        if (images != null && !images.isEmpty()) {

            return images.getJSONObject(0);
        }
        throw new ComfyuiException("解析视频结果失败");
    }

    /**
     * 设置任务错误状态
     * 将任务状态更新为失败状态
     *
     * @param userId 用户ID
     * @param taskId 任务ID
     */
    private void taskErrorStatus(final Long userId, final String taskId) {
        if (!isRunning.get()) {
            return;
        }

        String key = COMFYUI_TASK_LIST + userId;
        TaskInfoStructure o = (TaskInfoStructure) redisUtils.hashGet(key, taskId);
        
        // 任务失败，退还冻结的积分
        if (o.getCreditsDeducted() != null && o.getCreditsDeducted() > 0) {
            boolean refunded = creditUtils.refundCredits(
                userId, 
                o.getCreditsDeducted(), 
                "任务失败积分退还 - " + o.getWorkflowName()
            );
            if (!refunded) {
                log.warn("任务 {} 失败但积分退还失败，用户ID: {}, 积分: {}", 
                    taskId, userId, o.getCreditsDeducted());
            }
        }
        
        TaskInfoStructure failedTask = o.setStatus(TaskStatusEnum.FAILED.getDec());
        redisUtils.hashPut(key, taskId, failedTask);

        // 统计：今日失败任务数 +1
        recordTaskFailure();

        // 推送任务失败状态
        taskProgressPushService.pushTaskStatusChange(userId, taskId, failedTask);

        log.error("任务id:{}构建失败", taskId);

    }

    /**
     * 处理任务进度随机增加
     * 模拟任务进度增长，使用随机值增加任务进度
     *
     * @param userId 用户ID
     * @param taskId 任务ID
     */
    private void handleRandomVariables(final Long userId, final String taskId) {
        if (!isRunning.get()) {
            return;
        }

        final String key = COMFYUI_TASK_LIST + userId;
        TaskInfoStructure value = (TaskInfoStructure) redisUtils.hashGet(key, taskId);
        if (value == null) {
            return;
        }
        int progress = Math.toIntExact(value.getProgress());
        int maxIncrease = Math.min(4, 99 - progress);
        if (maxIncrease <= 0) {
            return;
        }
        int increase = random.nextInt(maxIncrease) + 1;
        progress += increase;
        TaskInfoStructure updatedTask = value.setProgress((long) progress);
        redisUtils.hashPut(key, taskId, updatedTask);

        // 推送进度更新
        taskProgressPushService.pushTaskProgressUpdate(userId, taskId, updatedTask);
    }

    /**
     * 处理绘图队列
     * 从队列索引中移除当前任务，并减少其他任务的索引值
     *
     * @param taskId 要处理的任务ID
     */
    private void handleComfyuiQueue(final String taskId) {
        if (!isRunning.get()) {
            return;
        }

        // 遍历并减少每个值
        redisUtils.hashDelete(COMFYUI_QUEUE_INDEX, taskId);
        Map<Object, Object> tasks = redisUtils.hashGetAll(COMFYUI_QUEUE_INDEX);
        for (Map.Entry<Object, Object> entry : tasks.entrySet()) {
            Object id = entry.getKey();
            Integer currentValue = (Integer) entry.getValue();
            if (currentValue != null) {
                redisUtils.hashPut(COMFYUI_QUEUE_INDEX, id.toString(), currentValue - 1);
            }
        }
        
        // 推送队列位置批量更新给所有等待中的用户
        try {
            taskProgressPushService.pushQueuePositionBatchUpdate();
        } catch (Exception e) {
            log.error("推送队列位置批量更新失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 释放与任务关联的信号量
     *
     * @param taskId 任务ID
     */
    private void releaseTaskSemaphore(String taskId) {
        Semaphore semaphore = taskSemaphores.remove(taskId);
        if (semaphore != null) {
            try {
                semaphore.release();
                log.debug("任务 {} 已释放信号量", taskId);
            } catch (Exception e) {
                log.warn("释放任务 {} 信号量时出错: {}", taskId, e.getMessage());
            }
        }
    }

    /**
     * 更新工作流JSON中的节点值
     *
     * @param workflowsJson 工作流JSON对象
     * @param containers    容器列表
     */
    private void updateNodeValues(JSONObject workflowsJson, List<TaskNodeContainer> containers) {
        for (TaskNodeContainer container : containers) {
            JSONObject node = workflowsJson.getJSONObject(container.getNodeKey());
            if (node != null) {
                JSONObject inputs = node.getJSONObject("inputs");
                if (inputs != null) {
                    if (StringUtils.isNotBlank(container.getNodeValue())) {
                        inputs.put(container.getInputs(), container.getNodeValue());
                    }
                }
            }
        }
    }

    // 优化后的 uploadFile 方法，采用流式传输以提高性能和内存效率
    public String uploadFile(final String uri, final String selectedServer) {
        try {
            final URL url = new URL(uri); // 可能抛出 MalformedURLException

            if (!isFileTypeSupported(uri)) {
                throw new ComfyuiException("不支持该类型文件上传: " + uri);
            }
            String fileExtension = getFileExtensionFromUrl(uri);
            String mimeType = getMimeTypeFromUrl(uri);
            String randomFilename = UUID.randomUUID().toString() + "." + fileExtension;

            // InputStream的提供者，将在Flux被订阅时调用，从而实现懒加载和重试时重新打开流
            Callable<InputStream> inputStreamSupplier = () -> {
                try {
                    // 当Flux订阅时，实际打开网络连接并获取输入流
                    return url.openStream(); // 可能抛出 IOException
                } catch (IOException e) {
                    // 将检查型异常转换为运行时异常，以便在响应式流中正确传播错误
                    log.error("(comfy-ui)流式上传：打开源文件流失败: {}, URL: {}", e.getMessage(), uri);
                    throw new RuntimeException("无法打开资源流: " + uri, e);
                }
            };

            // 使用DataBufferUtils从InputStream创建Flux<DataBuffer>
            // DataBufferUtils会自动处理InputStream的关闭
            Flux<DataBuffer> dataBufferFlux = DataBufferUtils.readInputStream(
                    inputStreamSupplier,
                    new DefaultDataBufferFactory(), // 默认的DataBuffer工厂
                    4096 // 缓冲区大小，可以根据需要调整
            );

            final MultipartBodyBuilder builder = new MultipartBodyBuilder();
            // 以流的方式添加文件部分，使用 asyncPart 处理 Publisher<DataBuffer>
            builder.asyncPart("image", dataBufferFlux, DataBuffer.class)
                    .contentType(MediaType.parseMediaType(mimeType))
                    .filename(randomFilename); // 设置文件名

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 使用WebClient进行POST请求
            final String block = webClient.post()
                    .uri(selectedServer + "/upload/image")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(BodyInserters.fromMultipartData(builder.build())) // 从MultipartBodyBuilder构建请求体
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (clientResponse -> clientResponse.bodyToMono(String.class)
                                    .timeout(Duration.ofSeconds(confyuiConfiguration.getTask().getMaxRetryTime()))
                                    .retryWhen(Retry.backoff(confyuiConfiguration.getTask().getMaxRetries(),
                                            Duration.ofSeconds(1)))
                                    .flatMap(errorBody -> Mono.error(new ComfyuiException(getBodyError(errorBody))))))
                    .bodyToMono(String.class)
                    .block(); // 阻塞等待结果

            JSONObject json = JSONObject.parseObject(block);
            if (json == null) {
                log.error("(comfy-ui)通过URL上传文件时 comfyui无响应");
                throw new ComfyuiException("服务器繁忙,请稍后再试");
            }
            log.info(json.getString("name"));
            return json.getString("name");

        } catch (Exception e) {
            log.error("(comfy-ui)通过URL上传文件时发生未知错误: {}, URL: {}", e.getMessage(), uri, e);
            throw new ComfyuiException("解析资源时出现错误");
        }
    }

    private String getMimeTypeFromUrl(final String url) {
        String extension = getFileExtensionFromUrl(url);
        return confyuiConfiguration.getSupportedFileTypes().getOrDefault(extension, "application/octet-stream");
    }

    private boolean isFileTypeSupported(final String url) {
        String extension = getFileExtensionFromUrl(url);
        return confyuiConfiguration.getSupportedFileTypes().containsKey(extension);
    }

    /**
     * 任务执行内部类
     * 存储任务执行所需的关键参数
     */
    @Data
    @Accessors(chain = true)
    public static class TaskExecution {
        private Long workflowId; // 工作流ID
        private String taskId; // 任务ID
        private String promptId; // 工作流分配ID
        private Long userId; // 用户ID
        private ComfyuiConfiguration.ServerConfig selectedServerConfig; // 新增字段，用于存储选定的服务器地址
    }

    /**
     * 轮询选择下一个可用的服务器
     *
     * @return 服务器地址
     */
    private ComfyuiConfiguration.ServerConfig selectNextServer() {
        List<ComfyuiConfiguration.ServerConfig> servers = confyuiConfiguration.getServer();
        if (servers == null || servers.isEmpty()) {
            log.error("ComfyUI服务器列表为空，无法选择服务器。");
            // 在这种情况下，可以抛出异常或者返回一个默认的、可能无效的地址
            // 以避免空指针，但这通常表示配置错误。
            throw new ComfyuiException("ComfyUI服务器配置不正确，列表为空。");
        }
        // 简单的轮询策略
        // 使用Math.abs确保索引非负，即使AtomicInteger意外变为负数（理论上不应发生）
        int index = Math.abs(nextServerIndex.getAndIncrement() % servers.size());
        return servers.get(index);
    }

    /**
     * 记录任务成功统计
     */
    private void recordTaskSuccess() {
        try {
            String today = java.time.LocalDate.now().toString();
            String key = com.cn.common.constant.SystemStatsConstant.TASK_SUCCESS_PREFIX + today;
            redisUtils.increment(key, 1L);
            redisUtils.expire(key, com.cn.common.constant.CacheExpireConstant.EXPIRE_48_HOURS);
            log.debug("记录任务成功统计: date={}", today);
        } catch (Exception e) {
            log.error("记录任务成功统计失败", e);
        }
    }

    /**
     * 记录任务失败统计
     */
    private void recordTaskFailure() {
        try {
            String today = java.time.LocalDate.now().toString();
            String key = com.cn.common.constant.SystemStatsConstant.TASK_FAILED_PREFIX + today;
            redisUtils.increment(key, 1L);
            redisUtils.expire(key, com.cn.common.constant.CacheExpireConstant.EXPIRE_48_HOURS);
            log.debug("记录任务失败统计: date={}", today);
        } catch (Exception e) {
            log.error("记录任务失败统计失败", e);
        }
    }

}
