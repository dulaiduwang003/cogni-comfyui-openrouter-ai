package com.cn.comfyui.websocket.message;

/**
 * WebSocket消息类型枚举
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public enum MessageType {


    /**
     * 任务状态变化 - 状态改变时发送
     */
    TASK_STATUS_CHANGE,
    
    /**
     * 任务进度更新 - 进度变化时发送
     */
    TASK_PROGRESS_UPDATE,
    
    /**
     * 队列位置更新 - 排队位置变化时发送
     */
    QUEUE_POSITION_UPDATE,
    
    /**
     * 连接确认消息
     */
    CONNECTION_ACK,
    
    /**
     * 错误消息
     */
    ERROR
}
