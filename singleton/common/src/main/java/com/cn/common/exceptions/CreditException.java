package com.cn.common.exceptions;


import lombok.Data;

/**
 * 积分业务异常处理
 * @author bdth github@dulaiduwang003
 * @version 1.0
 */
@SuppressWarnings("all")
@Data
public class CreditException extends RuntimeException {

    private String message;

    private Integer code;


    public CreditException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public CreditException(final String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }
} 