package com.cn.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.auth.dto.RedeemCodeDto;
import com.cn.auth.exceptions.RedemptionCodeException;
import com.cn.auth.service.RedemptionCodeService;
import com.cn.common.entity.RedemptionCode;
import com.cn.common.enums.RedemptionCodeStatus;
import com.cn.common.mapper.RedemptionCodeMapper;
import com.cn.common.structure.UserInfoStructure;
import com.cn.common.utils.CreditUtils;
import com.cn.common.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 兑换码服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedemptionCodeServiceImpl implements RedemptionCodeService {

    private final RedemptionCodeMapper redemptionCodeMapper;
    private final CreditUtils creditUtils;
    private final RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void redeemCode(RedeemCodeDto dto) {
        UserInfoStructure userInfo = UserUtils.getCurrentUserInfo();
        Long userId = userInfo.getUserId();
        String code = dto.getCode().trim().toUpperCase();

        // 获取分布式锁，防止并发兑换同一兑换码
        String lockKey = "REDEEM_LOCK:" + code;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new RedemptionCodeException("系统繁忙，请稍后重试");
            }

            try {
                // 1. 查询兑换码
                RedemptionCode redemptionCode = redemptionCodeMapper.selectOne(
                    new QueryWrapper<RedemptionCode>().lambda()
                        .eq(RedemptionCode::getCode, code)
                );

                if (redemptionCode == null) {
                    throw new RedemptionCodeException("兑换码不存在");
                }

                // 2. 检查兑换码状态
                RedemptionCodeStatus status = RedemptionCodeStatus.fromCode(redemptionCode.getStatus());
                if (status == null || !status.isActive()) {
                    if (status != null && status.isUsed()) {
                        throw new RedemptionCodeException("兑换码已被使用");
                    } else if (status != null && status.isDisabled()) {
                        throw new RedemptionCodeException("兑换码已被禁用");
                    } else {
                        throw new RedemptionCodeException("兑换码状态异常");
                    }
                }

                // 3. 检查是否过期
                if (redemptionCode.getExpireTime() != null && 
                    LocalDateTime.now().isAfter(redemptionCode.getExpireTime())) {
                    throw new RedemptionCodeException("兑换码已过期");
                }

                // 4. 检查用户是否已兑换过该兑换码（防止重复兑换）
                if (redemptionCode.getUsedByUserId() != null && 
                    redemptionCode.getUsedByUserId().equals(userId)) {
                    throw new RedemptionCodeException("您已经兑换过该兑换码");
                }

                // 5. 发放积分
                String description = "兑换码兑换：" + code;
                if (redemptionCode.getDescription() != null && !redemptionCode.getDescription().isEmpty()) {
                    description += " - " + redemptionCode.getDescription();
                }

                boolean rechargeSuccess = creditUtils.rechargeCredits(
                    userId, 
                    redemptionCode.getCreditsAmount(), 
                    description
                );

                if (!rechargeSuccess) {
                    throw new RedemptionCodeException("积分发放失败，请重试");
                }

                // 6. 更新兑换码状态
                redemptionCode.setStatus(RedemptionCodeStatus.USED.getCode())
                    .setUsedByUserId(userId)
                    .setUsedTime(LocalDateTime.now());
                redemptionCodeMapper.updateById(redemptionCode);

                // 7. 兑换成功，记录日志
                log.info("用户 {} 成功兑换兑换码 {}, 获得积分: {}", userId, code, redemptionCode.getCreditsAmount());

            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RedemptionCodeException("兑换被中断，请重试");
        }
    }
} 