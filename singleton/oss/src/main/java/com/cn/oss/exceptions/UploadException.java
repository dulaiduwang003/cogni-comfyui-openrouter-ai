package com.cn.oss.exceptions;

import lombok.Data;

/**
 * 上传异常处理
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@SuppressWarnings("all")
@Data
public class UploadException extends RuntimeException {

    private String message;

    private Integer code;

    public UploadException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public UploadException(final String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }
}
