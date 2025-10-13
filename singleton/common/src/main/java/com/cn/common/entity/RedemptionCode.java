package com.cn.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName(value = "redemption_codes")
public class RedemptionCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 兑换码
     */
    private String code;

    /**
     * 积分数量
     */
    private Long creditsAmount;

    /**
     * 状态：1-有效 0-已使用 -1-已禁用
     */
    private Integer status;

    /**
     * 使用者用户ID
     */
    private Long usedByUserId;

    /**
     * 使用时间
     */
    private LocalDateTime usedTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
} 