package com.cn.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 更新兑换码DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class UpdateRedemptionCodeDto {

    @NotNull
    private Long id;

    /** 可选：调整积分数量（已使用的不允许修改） */
    @Min(0)
    private Long creditsAmount;

    /** 可选：调整状态（1 有效，0 已使用，-1 已禁用） */
    private Integer status;

    /** 可选：调整过期时间 */
    private LocalDateTime expireTime;

    /** 可选：调整描述 */
    private String description;
}


