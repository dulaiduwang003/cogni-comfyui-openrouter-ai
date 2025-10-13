package com.cn.auth.exceptions;

/**
 * 兑换码相关异常
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public class RedemptionCodeException extends RuntimeException {

    public RedemptionCodeException(String message) {
        super(message);
    }

    public RedemptionCodeException(String message, Throwable cause) {
        super(message, cause);
    }
} 