package com.cn.system.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 系统兑换码VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class SystemRedemptionCodeVo {

	private Long id;

	private String code;

	private Long creditsAmount;

	private Integer status;

	private Long usedByUserId;

	private LocalDateTime usedTime;

	private LocalDateTime expireTime;

	private String description;

	private LocalDateTime createTime;
}


