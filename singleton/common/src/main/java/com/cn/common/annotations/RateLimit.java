package com.cn.common.annotations;

import java.lang.annotation.*;

/**
 * 接口限流注解
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * 每秒允许的请求数
     */
    double permitsPerSecond() default 10.0;
    
    /**
     * 限流维度 
     * IP: 按IP地址限流
     * USER: 按用户ID限流  
     * GLOBAL: 全局限流
     */
    LimitType limitType() default LimitType.IP;
    
    /**
     * 限流提示信息
     */
    String message() default "请求过于频繁，请稍后再试";
    
    /**
     * 限流维度枚举
     */
    enum LimitType {
        /**
         * 按IP限流
         */
        IP,
        
        /**
         * 按用户ID限流
         */
        USER,
        
        /**
         * 全局限流（按接口方法）
         */
        GLOBAL
    }
} 