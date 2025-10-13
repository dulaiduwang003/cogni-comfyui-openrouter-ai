package com.cn.system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 创建兑换码DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class CreateRedemptionCodeDto {

	/**
	 * 发放的积分数量
	 */
	@NotNull
	@Min(1)
	private Long creditsAmount;

	/**
	 * 可选：自定义前缀（如 RC-）
	 */
	private String prefix;

	/**
	 * 可选：随机部分长度，默认 8
	 */
	@Min(4)
	private Integer length;

	/**
	 * 可选：过期时间
	 */
	private LocalDateTime expireTime;

	/**
	 * 可选：描述
	 */
	private String description;
}


