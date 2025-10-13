package com.cn.comfyui.excepitons;

import lombok.Data;

/**
 * ComfyUI异常处理
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@SuppressWarnings("all")
@Data
public class ComfyuiException extends RuntimeException {

    private String message;

    private Integer code;

    public ComfyuiException(final String message, final Integer code) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public ComfyuiException(final String message) {
        super(message);
        this.message = message;
        this.code = 500;
    }
}
