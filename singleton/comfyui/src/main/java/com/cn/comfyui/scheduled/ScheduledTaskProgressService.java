package com.cn.comfyui.scheduled;

import com.cn.common.utils.RedisUtils;
import com.cn.comfyui.structure.TaskInfoStructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cn.comfyui.constant.ComfyuiConstant.*;

/**
 * 优化的任务清理服务
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledTaskProgressService {

    private final RedisUtils redisUtils;
    
    // 活跃用户缓存，避免频繁keys()扫描
    private final Set<String> activeUserKeys = ConcurrentHashMap.newKeySet();
    
    // 清理统计
    private final AtomicInteger totalCleanedTasks = new AtomicInteger(0);
    private final AtomicInteger totalCleanedQueueIndex = new AtomicInteger(0);

    /**
     * 主清理任务 - 5分钟执行一次，提高清理频率
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void cleanupOldTasks() {
        log.info("🧹 开始执行任务清理...");
        long startTime = System.currentTimeMillis();
        
        asyncCleanupTasks();
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("⏱️ 任务清理触发完成，耗时: {}ms", duration);
    }

    /**
     * 每小时更新活跃用户列表
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void updateActiveUsers() {
        log.info("更新活跃用户列表...");
        Set<String> currentActiveUsers = redisUtils.keys(COMFYUI_TASK_LIST + "*");
        
        // 更新缓存
        activeUserKeys.clear();
        activeUserKeys.addAll(currentActiveUsers);
        
        log.info("活跃用户数量: {}", activeUserKeys.size());
    }

    /**
     * 异步执行清理任务，避免阻塞主线程
     */
    @Async
    public void asyncCleanupTasks() {
        try {
            execution();
        } catch (Exception e) {
            log.error("清理任务执行失败", e);
        }
    }

    private void execution() {
        long startTime = System.currentTimeMillis();
        int cleanedTasks = 0;
        int cleanedQueueIndex = 0;
        
        // 如果活跃用户缓存为空，重新获取
        Set<String> usersToProcess = activeUserKeys.isEmpty() ? 
            redisUtils.keys(COMFYUI_TASK_LIST + "*") : activeUserKeys;
            
        log.info("📊 准备清理 {} 个用户的过期任务", usersToProcess.size());

        for (String userIdKey : usersToProcess) {
            try {
                int userCleanedTasks = cleanupUserTasks(userIdKey);
                cleanedTasks += userCleanedTasks;
                
                if (userCleanedTasks > 0) {
                    log.debug("用户 {} 清理了 {} 个过期任务", 
                        userIdKey.replace(COMFYUI_TASK_LIST, ""), userCleanedTasks);
                }
            } catch (Exception e) {
                log.warn("清理用户 {} 的任务时出错: {}", userIdKey, e.getMessage());
            }
        }
        
        // 清理孤儿队列索引
        try {
            cleanedQueueIndex = cleanupOrphanQueueIndex();
        } catch (Exception e) {
            log.warn("清理孤儿队列索引时出错: {}", e.getMessage());
        }

        // 更新统计
        totalCleanedTasks.addAndGet(cleanedTasks);
        totalCleanedQueueIndex.addAndGet(cleanedQueueIndex);
        
        long duration = System.currentTimeMillis() - startTime;
        
        if (cleanedTasks > 0 || cleanedQueueIndex > 0) {
            log.info("清理完成 - 任务: {}, 队列索引: {}, 耗时: {}ms", 
                cleanedTasks, cleanedQueueIndex, duration);
        }
    }

    /**
     * 清理单个用户的过期任务
     */
    private int cleanupUserTasks(String userIdKey) {
        String userId = userIdKey.replace(COMFYUI_TASK_LIST, "");
        String timeIndexKey = COMFYUI_TASK_TIME_INDEX + userId;
        
        Map<Object, Object> tasks = redisUtils.hashGetAll(userIdKey);
        if (tasks.isEmpty()) {
            // 如果用户没有任务了，从活跃用户列表中移除
            activeUserKeys.remove(userIdKey);
            return 0;
        }
        
        int cleanedCount = 0;
        LocalDateTime cutoffTime = LocalDateTime.now().minus(Duration.ofHours(24));
        
        for (Map.Entry<Object, Object> entry : tasks.entrySet()) {
            try {
                TaskInfoStructure task = (TaskInfoStructure) entry.getValue();
                LocalDateTime createTime = task.getCreateTime();
                
                if (createTime != null && cutoffTime.isAfter(createTime)) {
                    String taskId = task.getTaskId();
                    
                    // 删除用户任务
                    redisUtils.hashDelete(userIdKey, taskId);
                    
                    // 从时间索引ZSet中删除
                    redisUtils.zsetRemove(timeIndexKey, taskId);
                    
                    // 从队列索引中删除（如果存在）
                    redisUtils.hashDelete(COMFYUI_QUEUE_INDEX, taskId);
                    
                    cleanedCount++;
                    
                    log.debug("清理过期任务: {} (创建时间: {})", taskId, createTime);
                }
            } catch (Exception e) {
                log.warn("处理任务 {} 时出错: {}", entry.getKey(), e.getMessage());
            }
        }
        
        return cleanedCount;
    }

    /**
     * 清理孤儿队列索引 - 清理在COMFYUI_QUEUE_INDEX中但在用户任务中已不存在的taskId
     */
    private int cleanupOrphanQueueIndex() {
        Map<Object, Object> queueIndex = redisUtils.hashGetAll(COMFYUI_QUEUE_INDEX);
        if (queueIndex.isEmpty()) {
            return 0;
        }
        
        int cleanedCount = 0;
        Set<String> allActiveTaskIds = getAllActiveTaskIds();
        
        for (Object taskIdObj : queueIndex.keySet()) {
            String taskId = taskIdObj.toString();
            
            if (!allActiveTaskIds.contains(taskId)) {
                // 这是一个孤儿索引，删除它
                redisUtils.hashDelete(COMFYUI_QUEUE_INDEX, taskId);
                cleanedCount++;
                
                log.debug("清理孤儿队列索引: {}", taskId);
            }
        }
        
        return cleanedCount;
    }

    /**
     * 获取所有活跃任务ID
     */
    private Set<String> getAllActiveTaskIds() {
        Set<String> activeTaskIds = ConcurrentHashMap.newKeySet();
        
        Set<String> usersToProcess = activeUserKeys.isEmpty() ? 
            redisUtils.keys(COMFYUI_TASK_LIST + "*") : activeUserKeys;
            
        for (String userIdKey : usersToProcess) {
            try {
                Map<Object, Object> tasks = redisUtils.hashGetAll(userIdKey);
                for (Object taskIdObj : tasks.keySet()) {
                    activeTaskIds.add(taskIdObj.toString());
                }
            } catch (Exception e) {
                log.warn("获取用户 {} 的任务ID时出错: {}", userIdKey, e.getMessage());
            }
        }
        
        return activeTaskIds;
    }

    /**
     * 每天凌晨输出清理统计报告
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyCleanupReport() {
        int totalTasks = totalCleanedTasks.getAndSet(0);
        int totalQueue = totalCleanedQueueIndex.getAndSet(0);
        
        if (totalTasks > 0 || totalQueue > 0) {
            log.info("昨日清理统计 - 过期任务: {}, 孤儿队列索引: {}", totalTasks, totalQueue);
        }
    }
}
