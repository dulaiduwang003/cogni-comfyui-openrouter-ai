package com.cn.common.constant;

/**
 * 系统统计 Redis Key 常量
 * Key命名规范：STATS:{MODULE}:{METRIC}:{DATE}
 *
 * @author Conni-X-Pro
 */
public interface SystemStatsConstant {

    // ==================== 用户统计 ====================

    /**
     * 今日新增用户数
     * Key格式: STATS:USER:NEW:yyyy-MM-dd，过期48小时
     */
    String USER_NEW_PREFIX = "STATS:USER:NEW:";

    /**
     * 用户总数缓存，过期5分钟
     */
    String USER_TOTAL_COUNT = "STATS:USER:TOTAL_COUNT";

    // ==================== WebSocket 统计 ====================

    /**
     * WebSocket 在线用户数，过期5分钟
     */
    String WEBSOCKET_ONLINE_USERS = "STATS:WS:ONLINE_USERS";

    /**
     * WebSocket 总连接数，过期5分钟
     */
    String WEBSOCKET_TOTAL_CONNECTIONS = "STATS:WS:TOTAL_CONNECTIONS";

    // ==================== AI 对话统计 ====================

    /**
     * 今日 API 调用次数
     * Key格式: STATS:AI:API_CALLS:yyyy-MM-dd，过期48小时
     */
    String AI_API_CALLS_PREFIX = "STATS:AI:API_CALLS:";

    /**
     * 今日 Token 消耗
     * Key格式: STATS:AI:TOKENS:yyyy-MM-dd，过期48小时
     */
    String AI_TOKENS_PREFIX = "STATS:AI:TOKENS:";

    /**
     * 今日模型调用统计（Hash存储）
     * Key格式: STATS:AI:MODELS:yyyy-MM-dd，过期48小时
     */
    String AI_MODELS_PREFIX = "STATS:AI:MODELS:";

    // ==================== ComfyUI 任务统计 ====================

    /**
     * 今日任务提交数
     * Key格式: STATS:TASK:SUBMITTED:yyyy-MM-dd，过期48小时
     */
    String TASK_SUBMITTED_PREFIX = "STATS:TASK:SUBMITTED:";

    /**
     * 今日任务成功数
     * Key格式: STATS:TASK:SUCCESS:yyyy-MM-dd，过期48小时
     */
    String TASK_SUCCESS_PREFIX = "STATS:TASK:SUCCESS:";

    /**
     * 今日任务失败数
     * Key格式: STATS:TASK:FAILED:yyyy-MM-dd，过期48小时
     */
    String TASK_FAILED_PREFIX = "STATS:TASK:FAILED:";

    /**
     * 今日任务取消数
     * Key格式: STATS:TASK:CANCELLED:yyyy-MM-dd，过期48小时
     */
    String TASK_CANCELLED_PREFIX = "STATS:TASK:CANCELLED:";

    /**
     * 当前队列任务数，过期5分钟
     */
    String TASK_QUEUE_SIZE = "STATS:TASK:QUEUE_SIZE";

    /**
     * 当前构建中任务数，过期5分钟
     */
    String TASK_BUILDING_COUNT = "STATS:TASK:BUILDING_COUNT";

    // ==================== 工作流统计 ====================

    /**
     * 工作流总数缓存，过期5分钟
     */
    String WORKFLOW_TOTAL_COUNT = "STATS:WORKFLOW:TOTAL_COUNT";

    /**
     * 启用的工作流数缓存，过期5分钟
     */
    String WORKFLOW_ENABLED_COUNT = "STATS:WORKFLOW:ENABLED_COUNT";
}

