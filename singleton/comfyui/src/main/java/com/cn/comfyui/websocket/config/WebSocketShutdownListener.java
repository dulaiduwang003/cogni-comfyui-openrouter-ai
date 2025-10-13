package com.cn.comfyui.websocket.config;

import com.cn.comfyui.websocket.handler.TaskProgressWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * WebSocket关闭监听器
 * 在应用关闭时主动清理所有WebSocket连接，避免Tomcat关闭时的异常
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketShutdownListener implements ApplicationListener<ContextClosedEvent> {
    
    private final TaskProgressWebSocketHandler taskProgressWebSocketHandler;
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("应用正在关闭，开始清理WebSocket连接...");
        try {
            // 主动关闭所有WebSocket连接
            taskProgressWebSocketHandler.closeAllConnections();
            log.info("WebSocket连接清理完成");
        } catch (Exception e) {
            log.warn("WebSocket连接清理过程中发生异常: {}", e.getMessage());
        }
    }
} 