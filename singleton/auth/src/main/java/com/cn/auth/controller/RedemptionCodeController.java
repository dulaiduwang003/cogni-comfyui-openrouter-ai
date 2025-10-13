package com.cn.auth.controller;

import com.cn.auth.dto.RedeemCodeDto;
import com.cn.auth.exceptions.RedemptionCodeException;
import com.cn.auth.service.RedemptionCodeService;
import com.cn.common.msg.Result;
import com.cn.common.annotations.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 兑换码控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@RestController
@RequestMapping("/redemption-code")
@RequiredArgsConstructor
@Slf4j
public class RedemptionCodeController {

    private final RedemptionCodeService redemptionCodeService;

    /**
     * 兑换码兑换
     *
     * @param dto 兑换码请求DTO
     * @return 兑换结果
     */
    @PostMapping(value = "/redeem", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.167, limitType = RateLimit.LimitType.USER, message = "兑换过于频繁，请稍后再试")
    public Result redeemCode(@RequestBody @Validated RedeemCodeDto dto) {
        try {
            redemptionCodeService.redeemCode(dto);
            return Result.ok();
        } catch (RedemptionCodeException e) {
            log.warn("兑换码兑换失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        } 
    }
} 