package com.cn.common.exceptions;

/**
 * 限流异常
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
public class RateLimitException extends RuntimeException {
    
    public RateLimitException(String message) {
        super(message);
    }
    
    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
} 