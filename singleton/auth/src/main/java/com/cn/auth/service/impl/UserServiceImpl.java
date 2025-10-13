package com.cn.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.auth.dto.UpdateAvatarDto;
import com.cn.auth.dto.UpdateNicknameDto;
import com.cn.auth.service.UserService;
import com.cn.auth.vo.CreditTransactionVo;
import com.cn.auth.vo.UserCreditsVo;
import com.cn.auth.vo.UserVo;
import com.cn.common.entity.CreditTransaction;
import com.cn.common.entity.User;
import com.cn.common.entity.UserCredits;
import com.cn.common.mapper.CreditTransactionMapper;
import com.cn.common.mapper.RedemptionCodeMapper;
import com.cn.common.mapper.UserMapper;
import com.cn.common.structure.UserInfoStructure;
import com.cn.common.utils.CreditUtils;
import com.cn.common.utils.UserUtils;
import com.cn.common.vo.PageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final CreditUtils creditUtils;
    private final CreditTransactionMapper creditTransactionMapper;
    private final RedemptionCodeMapper redemptionCodeMapper;
    private final RedissonClient redissonClient;

    @Override
    public UserVo getUserInfo() {
        UserInfoStructure userInfo = UserUtils.getCurrentUserInfo();

        return new UserVo()
                .setRole(userInfo.getRole())
                .setNickname(userInfo.getNickname())
                .setAvatar(userInfo.getAvatar());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(UpdateAvatarDto dto) {
        updateUserInfo(
                // 更新数据库中的用户信息
                user -> user.setAvatar(dto.getAvatar()),
                // 更新缓存中的用户信息
                (userInfo, dbUser) -> userInfo.setAvatar(dto.getAvatar()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNickname(UpdateNicknameDto dto) {
        updateUserInfo(
                // 更新数据库中的用户信息
                user -> user.setNickname(dto.getNickname()),
                // 更新缓存中的用户信息
                (userInfo, dbUser) -> userInfo.setNickname(dto.getNickname()));
    }

    /**
     * 通用的用户信息更新方法
     *
     * @param dbUserUpdater    数据库用户信息更新器
     * @param cacheUserUpdater 缓存用户信息更新器
     */
    @Transactional(rollbackFor = Exception.class)
    protected void updateUserInfo(Consumer<User> dbUserUpdater, BiConsumer<UserInfoStructure, User> cacheUserUpdater) {
        // 获取当前登录用户ID
        Long userId = UserUtils.getCurrentLoginId();

        // 获取当前用户信息
        UserInfoStructure currentUserInfo = UserUtils.getCurrentUserInfo();

        // 创建用户对象并设置ID
        User user = new User().setId(userId);

        // 使用更新器更新用户信息
        dbUserUpdater.accept(user);

        // 更新数据库
        userMapper.updateById(user);

        // 创建新的用户信息结构体，保留原有信息
        UserInfoStructure updatedUserInfo = new UserInfoStructure()
                .setUserId(currentUserInfo.getUserId())
                .setNickname(currentUserInfo.getNickname())
                .setRole(currentUserInfo.getRole())
                .setAvatar(currentUserInfo.getAvatar());

        // 使用更新器更新缓存用户信息
        cacheUserUpdater.accept(updatedUserInfo, user);

        // 更新缓存
        UserUtils.updateUserInfo(updatedUserInfo);
    }

    @Override
    public UserCreditsVo getUserCredits() {
        Long userId = UserUtils.getCurrentLoginId();
        UserCredits userCredits = creditUtils.getUserCredits(userId);
        
        return new UserCreditsVo()
                .setTotalCredits(userCredits.getTotalCredits())
                .setAvailableCredits(userCredits.getAvailableCredits())
                .setFrozenCredits(userCredits.getFrozenCredits());
    }

    @Override
    public PageVo<CreditTransactionVo> getCreditTransactions(Integer page, Integer size, String transactionType) {
        Long userId = UserUtils.getCurrentLoginId();
        
        // 创建分页对象
        Page<CreditTransaction> pageObj = new Page<>(page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<CreditTransaction> queryWrapper = new QueryWrapper<CreditTransaction>()
                .lambda()
                .eq(CreditTransaction::getUserId, userId)
                .orderByDesc(CreditTransaction::getCreateTime);


        // 如果指定了交易类型，添加过滤条件
        if (transactionType != null && !transactionType.trim().isEmpty()) {
            queryWrapper.eq(CreditTransaction::getTransactionType, transactionType);
        }
        
        // 查询用户的积分交易记录，按创建时间倒序
        IPage<CreditTransaction> transactionPage = creditTransactionMapper.selectPage(pageObj, queryWrapper);
        
        // 转换为VO
        List<CreditTransactionVo> transactionVos = transactionPage.getRecords().stream()
                .map(transaction -> new CreditTransactionVo()
                        .setId(transaction.getId())
                        .setTransactionType(transaction.getTransactionType())
                        .setAmount(transaction.getAmount())
                        .setDescription(transaction.getDescription())
                        .setCreateTime(transaction.getCreateTime()))
                .collect(Collectors.toList());
        
        return new PageVo<CreditTransactionVo>()
                .setTotal(transactionPage.getTotal())
                .setItems(transactionVos);
    }



}
