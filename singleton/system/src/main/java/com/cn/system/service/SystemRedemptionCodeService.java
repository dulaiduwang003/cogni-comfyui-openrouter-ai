package com.cn.system.service;

import com.cn.common.vo.PageVo;
import com.cn.system.dto.CreateRedemptionCodeDto;
import com.cn.system.dto.DeleteRedemptionCodeDto;
import com.cn.system.dto.UpdateRedemptionCodeCreditsDto;
import com.cn.system.dto.UpdateRedemptionCodeDto;
import com.cn.system.vo.SystemRedemptionCodeVo;

/**
 * 系统兑换码服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface SystemRedemptionCodeService {

	PageVo<SystemRedemptionCodeVo> page(Integer page, Integer size, String keyword, Integer status);

	Long create(CreateRedemptionCodeDto dto);

	void delete(DeleteRedemptionCodeDto dto);

	void updateCredits(UpdateRedemptionCodeCreditsDto dto);

	void update(UpdateRedemptionCodeDto dto);
}


