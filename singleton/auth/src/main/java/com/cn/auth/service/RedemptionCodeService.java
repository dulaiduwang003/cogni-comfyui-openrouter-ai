package com.cn.auth.service;

import com.cn.auth.dto.RedeemCodeDto;

/**
 * 兑换码服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface RedemptionCodeService {

    /**
     * 兑换兑换码
     *
     * @param dto 兑换码DTO
     */
    void redeemCode(RedeemCodeDto dto);
} 