// WebSocket消息类型枚举
export enum WebSocketMessageTypeEnum {
    TASK_STATUS_CHANGE = "TASK_STATUS_CHANGE", // 任务状态变化
    TASK_PROGRESS_UPDATE = "TASK_PROGRESS_UPDATE", // 任务进度更新
    QUEUE_POSITION_UPDATE = "QUEUE_POSITION_UPDATE", // 队列位置更新
    CONNECTION_ACK = "CONNECTION_ACK", // 连接确认消息
    ERROR = "ERROR" // 错误消息
} 