package com.cn.auth.service;

import com.cn.auth.dto.UpdateAvatarDto;
import com.cn.auth.dto.UpdateNicknameDto;

import com.cn.auth.vo.UserVo;
import com.cn.auth.vo.UserCreditsVo;
import com.cn.auth.vo.CreditTransactionVo;

import com.cn.common.vo.PageVo;

/**
 * 用户服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface UserService {

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息视图对象
     */
    UserVo getUserInfo();
    
    /**
     * 更新用户头像
     *
     * @param dto 包含新头像URL的DTO
     */
    void updateAvatar(UpdateAvatarDto dto);
    
    /**
     * 更新用户昵称
     *
     * @param dto 包含新昵称的DTO
     */
    void updateNickname(UpdateNicknameDto dto);

    /**
     * 获取当前用户积分信息
     *
     * @return 用户积分信息
     */
    UserCreditsVo getUserCredits();

    /**
     * 获取当前用户积分交易记录
     *
     * @param page 页码
     * @param size 每页大小
     * @param transactionType 交易类型筛选，可选参数
     * @return 积分交易记录分页数据
     */
    PageVo<CreditTransactionVo> getCreditTransactions(Integer page, Integer size, String transactionType);


}
