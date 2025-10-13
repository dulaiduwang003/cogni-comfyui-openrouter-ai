package com.cn.common.enums;

import lombok.Getter;

/**
 * 兑换码状态枚举
 * 
 * @author CogniX
 */
@Getter
public enum RedemptionCodeStatus {
    
    /**
     * 有效状态 - 可以正常兑换
     */
    ACTIVE(1),
    
    /**
     * 已使用状态 - 已被用户兑换，不可再次使用
     */
    USED(0),
    
    /**
     * 已禁用状态 - 管理员禁用，不可使用
     */
    DISABLED(-1);
    
    private final Integer code;
    
    RedemptionCodeStatus(Integer code) {
        this.code = code;
    }
    
    /**
     * 根据状态码获取枚举
     * 
     * @param code 状态码
     * @return 对应的枚举，如果找不到则返回null
     */
    public static RedemptionCodeStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        
        for (RedemptionCodeStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 检查状态是否有效（可以兑换）
     * 
     * @return 是否有效
     */
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    /**
     * 检查状态是否已使用
     * 
     * @return 是否已使用
     */
    public boolean isUsed() {
        return this == USED;
    }
    
    /**
     * 检查状态是否已禁用
     * 
     * @return 是否已禁用
     */
    public boolean isDisabled() {
        return this == DISABLED;
    }
} 