package com.cn.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.cn.common.annotations.RateLimit;
import com.cn.common.exceptions.RateLimitException;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流AOP切面
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Aspect
@Component
@Slf4j
@SuppressWarnings("all")
public class RateLimitAspect {
    
    /**
     * 存储不同维度的限流器
     */
    private final ConcurrentHashMap<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();
    
    @Around("@annotation(com.cn.common.annotations.RateLimit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        
        // 构建限流key
        String limitKey = buildLimitKey(rateLimit, method);
        
        // 获取或创建限流器
        RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(limitKey, 
            key -> RateLimiter.create(rateLimit.permitsPerSecond()));
        
        // 尝试获取令牌
        boolean acquired = rateLimiter.tryAcquire();
        
        if (!acquired) {
            log.warn("接口限流触发，限流key: {}, 限制: {}请求/秒", limitKey, rateLimit.permitsPerSecond());
            throw new RateLimitException(rateLimit.message());
        }
        
        // 继续执行原方法
        return joinPoint.proceed();
    }
    
    /**
     * 构建限流key
     */
    private String buildLimitKey(RateLimit rateLimit, Method method) {
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();
        
        switch (rateLimit.limitType()) {
            case IP:
                return "rate_limit:ip:" + getClientIP() + ":" + methodName;
            case USER:
                return "rate_limit:user:" + getCurrentUserId() + ":" + methodName;
            case GLOBAL:
                return "rate_limit:global:" + methodName;
            default:
                return "rate_limit:global:" + methodName;
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIP() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String xForwardedFor = request.getHeader("X-Forwarded-For");
                String xRealIP = request.getHeader("X-Real-IP");
                
                if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
                    return xForwardedFor.split(",")[0].trim();
                }
                if (xRealIP != null && !xRealIP.isEmpty() && !"unknown".equalsIgnoreCase(xRealIP)) {
                    return xRealIP;
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.warn("获取客户端IP失败: {}", e.getMessage());
        }
        return "unknown";
    }
    
    /**
     * 获取当前用户ID
     */
    private String getCurrentUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsString();
            }
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
        }
        return "anonymous";
    }
} 