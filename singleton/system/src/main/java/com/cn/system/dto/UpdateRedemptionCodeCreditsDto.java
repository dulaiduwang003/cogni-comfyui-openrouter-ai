package com.cn.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 更新兑换码积分DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class UpdateRedemptionCodeCreditsDto {

	@NotNull
	private Long id;

	@NotNull
	@Min(0)
	private Long creditsAmount;
}


