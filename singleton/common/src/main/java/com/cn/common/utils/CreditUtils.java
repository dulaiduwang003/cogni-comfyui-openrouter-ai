package com.cn.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cn.common.entity.CreditTransaction;
import com.cn.common.entity.UserCredits;
import com.cn.common.enums.TransactionType;
import com.cn.common.exceptions.CreditException;
import com.cn.common.mapper.CreditTransactionMapper;
import com.cn.common.mapper.UserCreditsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreditUtils {

    private final UserCreditsMapper userCreditsMapper;
    private final CreditTransactionMapper creditTransactionMapper;
    private final RedissonClient redissonClient;

    private static final String CREDIT_LOCK_PREFIX = "CREDIT_LOCK:";

    /**
     * 检查用户积分是否足够
     *
     * @param userId 用户ID
     * @param amount 需要的积分数量
     * @return 是否足够
     */
    public boolean hasEnoughCredits(Long userId, Long amount) {
        UserCredits userCredits = getUserCredits(userId);
        return userCredits.getAvailableCredits() >= amount;
    }

    /**
     * 冻结积分
     *
     * @param userId 用户ID
     * @param amount 冻结积分数量
     * @param description 描述
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeCredits(Long userId, Long amount, String description) {
        RLock lock = redissonClient.getLock(CREDIT_LOCK_PREFIX + userId);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                return false;
            }

            try {
                UserCredits userCredits = getUserCredits(userId);

                if (userCredits.getAvailableCredits() < amount) {
                    log.warn("用户 {} 积分不足，需要 {} 积分，可用 {} 积分", 
                            userId, amount, userCredits.getAvailableCredits());
                    return false;
                }

                // 更新积分
                UpdateWrapper<UserCredits> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda()
                        .eq(UserCredits::getUserId, userId)
                        .setSql("available_credits = available_credits - " + amount)
                        .setSql("frozen_credits = frozen_credits + " + amount);

                boolean updateResult = userCreditsMapper.update(null, updateWrapper) > 0;

                if (updateResult) {
                    // 记录流水
                    recordTransaction(userId, TransactionType.FREEZE, amount, description);
                }

                return updateResult;
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("冻结积分被中断，用户ID: {}", userId, e);
            return false;
        }
    }

    /**
     * 消费积分（从冻结积分中扣除）
     *
     * @param userId 用户ID
     * @param amount 消费积分数量
     * @param description 描述
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean consumeCredits(Long userId, Long amount, String description) {
        RLock lock = redissonClient.getLock(CREDIT_LOCK_PREFIX + userId);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                return false;
            }

            try {
                // 更新积分：从冻结积分和总积分中扣除
                UpdateWrapper<UserCredits> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda()
                        .eq(UserCredits::getUserId, userId)
                        .setSql("total_credits = total_credits - " + amount)
                        .setSql("frozen_credits = frozen_credits - " + amount);

                boolean updateResult = userCreditsMapper.update(null, updateWrapper) > 0;

                if (updateResult) {
                    // 记录流水
                    recordTransaction(userId, TransactionType.CONSUME, amount, description);
                }

                return updateResult;
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("消费积分被中断，用户ID: {}", userId, e);
            return false;
        }
    }

    /**
     * 退还积分（将冻结积分退还到可用积分）
     *
     * @param userId 用户ID
     * @param amount 退还积分数量
     * @param description 描述
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean refundCredits(Long userId, Long amount, String description) {
        RLock lock = redissonClient.getLock(CREDIT_LOCK_PREFIX + userId);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                log.warn("获取积分锁失败，用户ID: {}", userId);
                return false;
            }

            try {
                // 更新积分：将冻结积分退还到可用积分
                UpdateWrapper<UserCredits> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda()
                        .eq(UserCredits::getUserId, userId)
                        .setSql("available_credits = available_credits + " + amount)
                        .setSql("frozen_credits = frozen_credits - " + amount);

                boolean updateResult = userCreditsMapper.update(null, updateWrapper) > 0;

                if (updateResult) {
                    // 记录流水
                    recordTransaction(userId, TransactionType.REFUND, amount, description);
                }

                return updateResult;
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("退还积分被中断，用户ID: {}", userId, e);
            return false;
        }
    }

    /**
     * 充值积分
     *
     * @param userId      用户ID
     * @param amount      充值数量
     * @param description 描述
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean rechargeCredits(Long userId, Long amount, String description) {
        RLock lock = redissonClient.getLock(CREDIT_LOCK_PREFIX + userId);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {

                return false;
            }

            try {
                // 更新积分：增加总积分和可用积分
                UpdateWrapper<UserCredits> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda()
                        .eq(UserCredits::getUserId, userId)
                        .setSql("total_credits = total_credits + " + amount)
                        .setSql("available_credits = available_credits + " + amount);

                boolean updateResult = userCreditsMapper.update(null, updateWrapper) > 0;

                if (updateResult) {
                    // 记录流水
                    recordTransaction(userId, TransactionType.RECHARGE, amount, description);
                }

                return updateResult;
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("充值积分被中断，用户ID: {}", userId, e);
            return false;
        }
    }

    /**
     * 获取用户积分信息
     *
     * @param userId 用户ID
     * @return 用户积分
     */
    public UserCredits getUserCredits(Long userId) {
        UserCredits userCredits = userCreditsMapper.selectOne(
                new QueryWrapper<UserCredits>().lambda().eq(UserCredits::getUserId, userId));
        
        if (userCredits == null) {
            // 用户首次使用，创建积分账户，默认100积分
            userCredits = new UserCredits()
                    .setUserId(userId)
                    .setTotalCredits(100L)
                    .setAvailableCredits(100L)
                    .setFrozenCredits(0L);
            userCreditsMapper.insert(userCredits);
            
            // 记录初始积分流水
            recordTransaction(userId, TransactionType.RECHARGE, 100L, "新用户注册赠送积分");
        }
        
        return userCredits;
    }

    /**
     * 记录积分流水
     */
    private void recordTransaction(Long userId, TransactionType transactionType, Long amount, String description) {
        CreditTransaction transaction = new CreditTransaction()
                .setUserId(userId)
                .setTransactionType(transactionType.getCode())
                .setAmount(amount)
                .setDescription(description);
        
        creditTransactionMapper.insert(transaction);
    }

    /**
     * 检查并冻结积分（组合操作）
     *
     * @param userId 用户ID
     * @param amount 积分数量
     * @param description 描述
     * @return 是否成功
     */
    public boolean checkAndFreezeCredits(Long userId, Long amount, String description) {
        if (!hasEnoughCredits(userId, amount)) {
            return false;
        }
        return freezeCredits(userId, amount, description);
    }

    /**
     * 检查并冻结积分（失败时抛异常）
     * 
     * @param userId 用户ID
     * @param amount 积分数量
     * @param description 描述
     * @throws RuntimeException 当积分不足或冻结失败时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void checkAndFreezeCreditsWithException(Long userId, Long amount, String description) {
        if (amount == null || amount <= 0) {
            return; // 无需扣除积分
        }
        
        RLock lock = redissonClient.getLock(CREDIT_LOCK_PREFIX + userId);
        try {
            if (!lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                throw new CreditException("系统繁忙，请稍后重试");
            }

            try {
                UserCredits userCredits = getUserCredits(userId);

                if (userCredits.getAvailableCredits() < amount) {
                    throw new CreditException("积分不足，需要 " + amount + " 积分，当前可用 " + userCredits.getAvailableCredits() + " 积分");
                }

                // 更新积分
                UpdateWrapper<UserCredits> updateWrapper = new UpdateWrapper<>();
                updateWrapper.lambda()
                        .eq(UserCredits::getUserId, userId)
                        .setSql("available_credits = available_credits - " + amount)
                        .setSql("frozen_credits = frozen_credits + " + amount);

                boolean updateResult = userCreditsMapper.update(null, updateWrapper) > 0;

                if (!updateResult) {
                    throw new CreditException("积分冻结失败，请重试");
                }
                
                // 记录流水
                recordTransaction(userId, TransactionType.FREEZE, amount, description);
                
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CreditException("积分检查被中断，请重试");
        }
    }
} 