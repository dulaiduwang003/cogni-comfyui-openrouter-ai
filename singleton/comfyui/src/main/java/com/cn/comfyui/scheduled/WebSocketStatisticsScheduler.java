package com.cn.comfyui.scheduled;

import com.cn.common.constant.CacheExpireConstant;
import com.cn.common.constant.SystemStatsConstant;
import com.cn.common.utils.RedisUtils;
import com.cn.comfyui.constant.ComfyuiConstant;
import com.cn.comfyui.websocket.handler.TaskProgressWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * WebSocket 和队列统计定时任务

 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketStatisticsScheduler {

    private final RedisUtils redisUtils;
    private final TaskProgressWebSocketHandler webSocketHandler;

    /**
     * 每30秒更新一次 WebSocket 在线统计
     * <p>
     * 统计当前 WebSocket 连接的用户数和总连接数
     * </p>
     */
    @Scheduled(fixedRate = 30000)
    public void updateWebSocketStats() {
        try {
            // 获取在线用户数
            int onlineUsers = webSocketHandler.getConnectedUserCount();

            // 获取总连接数
            int totalConnections = webSocketHandler.getTotalConnectionCount();

            // 写入 Redis
            redisUtils.set(
                    SystemStatsConstant.WEBSOCKET_ONLINE_USERS,
                    onlineUsers,
                    CacheExpireConstant.EXPIRE_5_MINUTES
            );

            redisUtils.set(
                    SystemStatsConstant.WEBSOCKET_TOTAL_CONNECTIONS,
                    totalConnections,
                    CacheExpireConstant.EXPIRE_5_MINUTES
            );

            log.debug("更新WebSocket统计: onlineUsers={}, totalConnections={}", onlineUsers, totalConnections);

        } catch (Exception e) {
            log.error("更新WebSocket统计失败", e);
        }
    }

    /**
     * 每分钟更新一次队列统计
     * <p>
     * 统计当前队列中的任务数量
     * </p>
     */
    @Scheduled(cron = "0 * * * * ?")
    public void updateQueueStats() {
        try {
            // 获取队列任务数
            Long queueSize = redisUtils.keySize(ComfyuiConstant.COMFYUI_QUEUE);

            // 写入 Redis
            redisUtils.set(
                    SystemStatsConstant.TASK_QUEUE_SIZE,
                    queueSize != null ? queueSize : 0L,
                    CacheExpireConstant.EXPIRE_5_MINUTES
            );

            log.debug("更新队列统计: queueSize={}", queueSize);

        } catch (Exception e) {
            log.error("更新队列统计失败", e);
        }
    }
}

