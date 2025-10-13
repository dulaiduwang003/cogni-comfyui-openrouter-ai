package com.cn.comfyui.websocket.service;

import com.cn.comfyui.structure.TaskInfoStructure;
import com.cn.comfyui.websocket.handler.TaskProgressWebSocketHandler;
import com.cn.comfyui.websocket.message.TaskProgressMessage;
import com.cn.common.enums.TaskStatusEnum;
import com.cn.common.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.cn.comfyui.constant.ComfyuiConstant.COMFYUI_QUEUE_INDEX;
import static com.cn.comfyui.constant.ComfyuiConstant.COMFYUI_TASK_LIST;

/**
 * 任务进度推送服务
 * 负责在任务状态变更时进行实时推送
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TaskProgressPushService {
    
    private final TaskProgressWebSocketHandler webSocketHandler;
    private final RedisUtils redisUtils;
    private final RedissonClient redissonClient;
    
    /**
     * 推送任务状态变更
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskResult 任务结果结构
     * @param changeType 变更类型
     */
    public void pushTaskProgress(Long userId, String taskId, TaskInfoStructure taskResult, String changeType) {
        try {
            TaskProgressMessage message = null;
            
            switch (changeType) {
                case "STATUS_CHANGE":
                    // 状态变化：推送完整信息
                    message = TaskProgressMessage.statusChange(
                        taskId, 
                        taskResult.getStatus(), 
                        taskResult.getProgress(),
                        taskResult.getWorkflowResultModel(),
                        taskResult.getCreateTime(),
                        taskResult.getWorkflowName()
                    );
                    log.info("推送任务状态变化 - 用户: {}, 任务: {}, 状态: {} -> {}", 
                        userId, taskId, "变更前", taskResult.getStatus());
                    break;
                    
                case "PROGRESS_UPDATE":
                    // 进度更新：只推送运行中的任务
                    if (isRunningTask(taskResult.getStatus())) {
                        message = TaskProgressMessage.progressUpdate(taskId, taskResult.getProgress());
                        log.debug("推送任务进度更新 - 用户: {}, 任务: {}, 进度: {}%", 
                            userId, taskId, taskResult.getProgress());
                    }
                    break;
                    
                case "QUEUE_POSITION":
                    // 队列位置变化：只推送等待中的任务
                    if (TaskStatusEnum.WAIT.getDec().equals(taskResult.getStatus())) {
                        message = TaskProgressMessage.queueUpdate(taskId, taskResult.getLocation());
                        log.debug("推送队列位置更新 - 用户: {}, 任务: {}, 位置: {}", 
                            userId, taskId, taskResult.getLocation());
                    }
                    break;
                    
                default:
                    log.warn("未知的变更类型: {}", changeType);
                    return;
            }
            
            // 发送消息
            if (message != null) {
                webSocketHandler.pushToUser(userId, message);
            }
            
        } catch (Exception e) {
            log.error("推送任务进度失败 - 用户: {}, 任务: {}, 错误: {}", userId, taskId, e.getMessage(), e);
        }
    }
    
    /**
     * 推送任务状态变更（简化版本）
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskResult 任务结果结构
     */
    public void pushTaskStatusChange(Long userId, String taskId, TaskInfoStructure taskResult) {
        pushTaskProgress(userId, taskId, taskResult, "STATUS_CHANGE");
    }
    
    /**
     * 推送任务进度更新
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskResult 任务结果结构
     */
    public void pushTaskProgressUpdate(Long userId, String taskId, TaskInfoStructure taskResult) {
        pushTaskProgress(userId, taskId, taskResult, "PROGRESS_UPDATE");
    }
    
    /**
     * 推送队列位置更新
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @param taskResult 任务结果结构
     */
    public void pushQueuePositionUpdate(Long userId, String taskId, TaskInfoStructure taskResult) {
        pushTaskProgress(userId, taskId, taskResult, "QUEUE_POSITION");
    }
    
    /**
     * 判断是否为运行中的任务
     */
    private boolean isRunningTask(String status) {
        return TaskStatusEnum.WAIT.getDec().equals(status) || 
               TaskStatusEnum.BUILD.getDec().equals(status);
    }
    
    /**
     * 推送队列位置批量更新
     * 当队列中有任务完成时，推送所有等待任务的新位置给相应用户
     */
    public void pushQueuePositionBatchUpdate() {
        try {
            // 获取所有队列中的任务及其位置
            Map<Object, Object> queueTasks = redisUtils.hashGetAll(COMFYUI_QUEUE_INDEX);
            if (queueTasks == null || queueTasks.isEmpty()) {
                log.debug("队列中没有等待任务，跳过队列位置批量更新");
                return;
            }
            
            log.info("开始推送队列位置批量更新，队列中任务数: {}", queueTasks.size());
            
            // 获取所有用户的任务列表
            RKeys rKeys = redissonClient.getKeys();
            Iterable<String> taskListKeys = rKeys.getKeysByPattern(COMFYUI_TASK_LIST + "*");
            
            for (String taskListKey : taskListKeys) {
                try {
                    // 提取用户ID
                    String userId = taskListKey.replace(COMFYUI_TASK_LIST, "");
                    Long userIdLong = Long.parseLong(userId);
                    
                    // 获取该用户的所有任务
                    Map<Object, Object> userTasks = redisUtils.hashGetAll(taskListKey);
                    if (userTasks == null || userTasks.isEmpty()) {
                        continue;
                    }
                    
                    // 检查该用户是否有等待中的任务需要更新队列位置
                    for (Map.Entry<Object, Object> entry : userTasks.entrySet()) {
                        String taskId = entry.getKey().toString();
                        Object taskObj = entry.getValue();
                        
                        if (taskObj instanceof TaskInfoStructure) {
                            TaskInfoStructure task = (TaskInfoStructure) taskObj;
                            
                            // 只处理等待状态的任务
                            if (TaskStatusEnum.WAIT.getDec().equals(task.getStatus())) {
                                // 获取最新的队列位置
                                Object newLocation = queueTasks.get(taskId);
                                if (newLocation != null) {
                                    Long location = Long.parseLong(newLocation.toString());
                                    
                                    // 推送队列位置更新
                                    TaskProgressMessage message = TaskProgressMessage.queueUpdate(taskId, location);
                                    webSocketHandler.pushToUser(userIdLong, message);
                                    
                                    log.debug("推送队列位置更新 - 用户: {}, 任务: {}, 新位置: {}", 
                                        userIdLong, taskId, location);
                                }
                            }
                        }
                    }
                    
                } catch (NumberFormatException e) {
                    log.warn("解析用户ID失败: {}", taskListKey);
                } catch (Exception e) {
                    log.warn("处理用户任务列表失败: {}, 错误: {}", taskListKey, e.getMessage());
                }
            }
            
            log.info("队列位置批量更新推送完成");
            
        } catch (Exception e) {
            log.error("推送队列位置批量更新失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取WebSocket连接统计信息
     */
    public String getConnectionStats() {
        return String.format("连接用户数: %d, 总连接数: %d", 
            webSocketHandler.getConnectedUserCount(), 
            webSocketHandler.getTotalConnectionCount());
    }
}
