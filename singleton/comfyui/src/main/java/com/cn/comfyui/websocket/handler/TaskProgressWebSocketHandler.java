package com.cn.comfyui.websocket.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.fastjson.JSON;
import com.cn.comfyui.websocket.message.TaskProgressMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务进度WebSocket处理器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class TaskProgressWebSocketHandler implements WebSocketHandler {
    
    /**
     * 用户连接管理 userId -> Set<WebSocketSession>
     * 支持同一用户多个连接（多标签页场景）
     */
    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        try {
            // 从连接参数中获取token
            String token = getTokenFromSession(session);
            if (token == null) {
                log.warn("WebSocket连接缺少token参数");
                session.sendMessage(new TextMessage(JSON.toJSONString(
                    TaskProgressMessage.error("连接失败：缺少认证token"))));
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            
            // 验证token并获取用户ID
            Long userId = validateTokenAndGetUserId(token);
            if (userId == null) {
                log.warn("WebSocket连接token无效: {}", token);
                session.sendMessage(new TextMessage(JSON.toJSONString(
                    TaskProgressMessage.error("连接失败：token无效或已过期"))));
                session.close(CloseStatus.NOT_ACCEPTABLE);
                return;
            }
            
            // 将用户ID存储到session属性中
            session.getAttributes().put("userId", userId);
            
            // 添加到用户连接管理
            userSessions.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(session);
            
            log.info("用户 {} WebSocket连接建立成功，当前连接数: {}", userId, userSessions.get(userId).size());
            
            // 发送连接确认消息
            session.sendMessage(new TextMessage(JSON.toJSONString(
                TaskProgressMessage.connectionAck("连接建立成功，开始接收任务进度推送"))));
                
        } catch (Exception e) {
            log.error("WebSocket连接建立失败: {}", e.getMessage(), e);
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
    
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        // 目前不处理客户端消息，如需要可以扩展
        log.debug("收到WebSocket消息: {}", message.getPayload());
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        try {
            // 检查是否是应用关闭期间的正常异常
            if (isShutdownRelatedError(exception)) {
                log.debug("应用关闭期间的WebSocket传输错误: {}", exception.getMessage());
            } else {
                log.error("WebSocket传输错误: {}", exception.getMessage(), exception);
            }
            cleanupSession(session);
        } catch (Exception e) {
            // 捕获清理过程中的异常，避免应用关闭时的报错
            log.warn("WebSocket传输错误处理过程中发生异常: {}", e.getMessage());
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        try {
            cleanupSession(session);
            log.info("WebSocket连接关闭，状态: {}", closeStatus);
        } catch (Exception e) {
            // 捕获连接关闭清理过程中的异常，避免应用关闭时的报错
            log.warn("WebSocket连接关闭处理过程中发生异常: {}", e.getMessage());
        }
    }
    
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
    
    /**
     * 推送任务进度给指定用户
     * 
     * @param userId 用户ID
     * @param message 消息内容
     */
    public void pushToUser(Long userId, TaskProgressMessage message) {
        try {
            Set<WebSocketSession> sessions = userSessions.get(userId);
            if (sessions == null || sessions.isEmpty()) {
                log.debug("用户 {} 没有活跃的WebSocket连接", userId);
                return;
            }
            
            String messageJson = JSON.toJSONString(message);
            
            // 移除无效连接并发送消息
            sessions.removeIf(session -> {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(messageJson));
                        return false; // 保留有效连接
                    } else {
                        log.debug("移除已关闭的WebSocket连接");
                        return true; // 移除无效连接
                    }
                } catch (IOException e) {
                    log.warn("发送WebSocket消息失败: {}", e.getMessage());
                    return true; // 移除出错的连接
                } catch (Exception e) {
                    log.warn("WebSocket连接处理异常: {}", e.getMessage());
                    return true; // 移除出错的连接
                }
            });
            
            // 如果所有连接都无效，清理用户记录
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
            
            log.debug("推送消息给用户 {}: {}", userId, message.getType());
            
        } catch (Exception e) {
            // 捕获推送过程中的所有异常，避免影响主业务流程
            log.warn("推送WebSocket消息过程中发生异常: {}", e.getMessage());
        }
    }
    
    /**
     * 获取当前连接的用户数量
     */
    public int getConnectedUserCount() {
        return userSessions.size();
    }
    
    /**
     * 获取总连接数
     */
    public int getTotalConnectionCount() {
        return userSessions.values().stream().mapToInt(Set::size).sum();
    }
    
    /**
     * 从session中获取token
     */
    private String getTokenFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                        return keyValue[1];
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 验证token并获取用户ID
     */
    private Long validateTokenAndGetUserId(String token) {
        try {
            // 使用Sa-Token验证token
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId != null) {
                return Long.parseLong(loginId.toString());
            }
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
        }
        return null;
    }
    
    /**
     * 清理session
     */
    private void cleanupSession(WebSocketSession session) {
        try {
            Long userId = (Long) session.getAttributes().get("userId");
            if (userId != null) {
                Set<WebSocketSession> sessions = userSessions.get(userId);
                if (sessions != null) {
                    sessions.remove(session);
                    if (sessions.isEmpty()) {
                        userSessions.remove(userId);
                    }
                    log.debug("清理用户 {} 的WebSocket连接", userId);
                }
            }
        } catch (Exception e) {
            // 捕获清理过程中的所有异常，确保应用关闭时不会因为WebSocket清理导致错误
            log.warn("WebSocket连接清理过程中发生异常: {}", e.getMessage());
        }
    }
    
    /**
     * 检查是否是应用关闭相关的异常
     * 这些异常在应用关闭期间是正常的，应该降低日志级别
     */
    private boolean isShutdownRelatedError(Throwable exception) {
        if (exception == null) {
            return false;
        }
        
        // 检查当前异常
        if (isShutdownException(exception)) {
            return true;
        }
        
        // 检查异常链中的cause
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (isShutdownException(cause)) {
                return true;
            }
            cause = cause.getCause();
        }
        
        return false;
    }
    
    /**
     * 检查单个异常是否是应用关闭相关的异常
     */
    private boolean isShutdownException(Throwable exception) {
        String message = exception.getMessage();
        String exceptionType = exception.getClass().getSimpleName();
        
        // 检查异常类型
        if ("ClosedChannelException".equals(exceptionType)) {
            return true;
        }
        
        // 检查异常消息
        if (message != null) {
            return message.contains("ClosedChannelException") ||
                   message.contains("Connection reset") ||
                   message.contains("Broken pipe") ||
                   message.contains("Connection aborted") ||
                   message.contains("web application is stopping");
        }
        
        return false;
    }
    
    /**
     * 关闭所有WebSocket连接
     * 在应用关闭时调用，避免Tomcat关闭时的异常
     */
    public void closeAllConnections() {
        log.info("开始关闭所有WebSocket连接，当前用户数: {}", userSessions.size());
        
        // 遍历所有用户的连接
        userSessions.forEach((userId, sessions) -> {
            log.debug("关闭用户 {} 的 {} 个WebSocket连接", userId, sessions.size());
            
            // 关闭该用户的所有连接
            sessions.forEach(session -> {
                try {
                    if (session.isOpen()) {
                        session.close();
                        log.debug("成功关闭WebSocket连接");
                    }
                } catch (Exception e) {
                    // 静默处理关闭异常，避免影响其他连接的关闭
                    log.debug("关闭WebSocket连接时发生异常: {}", e.getMessage());
                }
            });
        });
        
        // 清空所有用户会话记录
        userSessions.clear();
        log.info("所有WebSocket连接已关闭");
    }
}
