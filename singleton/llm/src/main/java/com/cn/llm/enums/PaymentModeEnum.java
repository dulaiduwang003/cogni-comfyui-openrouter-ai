package com.cn.llm.enums;

/**
 * 付费模式枚举
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public enum PaymentModeEnum {
    PAID("PAID"),
    FREE("FREE");

    private final String value;

    PaymentModeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据字符串值获取枚举
     */
    public static PaymentModeEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PaymentModeEnum mode : PaymentModeEnum.values()) {
            if (mode.getValue().equals(value)) {
                return mode;
            }
        }
        return null;
    }
} 