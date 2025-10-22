package com.cn.comfyui.websocket.message;

import com.alibaba.fastjson2.JSON;
import com.cn.comfyui.model.WorkflowResultModel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务进度消息实体
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Builder
public class TaskProgressMessage {
    
    /**
     * 消息类型
     */
    private MessageType type;
    
    /**
     * 消息数据 - JSON字符串格式

     */
    private String data;
    
    /**
     * 消息时间戳
     */
    private Long timestamp;
    
    /**
     * 额外信息/错误信息
     */
    private String message;
    
    /**
     * 创建状态变化消息
     */
    public static TaskProgressMessage statusChange(String taskId, String status, Long progress, WorkflowResultModel workflowResultModel, LocalDateTime createTime, String workflowName) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("taskId", taskId);
        dataMap.put("status", status);
        dataMap.put("progress", progress);
        dataMap.put("workflowResultModel", workflowResultModel);
        dataMap.put("workflowName",workflowName);
        dataMap.put("createTime", createTime);
        
        return TaskProgressMessage.builder()
                .type(MessageType.TASK_STATUS_CHANGE)
                .data(JSON.toJSONString(dataMap))
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 创建进度更新消息
     */
    public static TaskProgressMessage progressUpdate(String taskId, Long progress) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("taskId", taskId);
        dataMap.put("progress", progress);
        
        return TaskProgressMessage.builder()
                .type(MessageType.TASK_PROGRESS_UPDATE)
                .data(JSON.toJSONString(dataMap))
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 创建队列位置更新消息
     */
    public static TaskProgressMessage queueUpdate(String taskId, Long location) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("taskId", taskId);
        dataMap.put("location", location);
        
        return TaskProgressMessage.builder()
                .type(MessageType.QUEUE_POSITION_UPDATE)
                .data(JSON.toJSONString(dataMap))
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 创建连接确认消息
     */
    public static TaskProgressMessage connectionAck(String message) {
        return TaskProgressMessage.builder()
                .type(MessageType.CONNECTION_ACK)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 创建错误消息
     */
    public static TaskProgressMessage error(String message) {
        return TaskProgressMessage.builder()
                .type(MessageType.ERROR)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 创建通用消息
     * 
     * @param type 消息类型
     * @param dataMap 数据Map
     * @return TaskProgressMessage
     */
    public static TaskProgressMessage create(MessageType type, Map<String, Object> dataMap) {
        return TaskProgressMessage.builder()
                .type(type)
                .data(dataMap != null ? JSON.toJSONString(dataMap) : null)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    /**
     * 创建通用消息（带额外信息）
     * 
     * @param type 消息类型
     * @param dataMap 数据Map
     * @param message 额外信息
     * @return TaskProgressMessage
     */
    public static TaskProgressMessage create(MessageType type, Map<String, Object> dataMap, String message) {
        return TaskProgressMessage.builder()
                .type(type)
                .data(dataMap != null ? JSON.toJSONString(dataMap) : null)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
