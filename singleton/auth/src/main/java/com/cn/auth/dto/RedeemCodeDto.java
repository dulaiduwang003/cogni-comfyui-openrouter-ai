package com.cn.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 兑换码DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class RedeemCodeDto {

    @NotBlank(message = "兑换码不能为空")
    private String code;
} 