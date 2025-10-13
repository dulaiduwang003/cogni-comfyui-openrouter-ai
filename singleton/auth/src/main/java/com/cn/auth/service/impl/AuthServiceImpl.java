package com.cn.auth.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cn.auth.dto.GetVerificationCodeDto;
import com.cn.auth.dto.PasswordLoginDto;
import com.cn.auth.exceptions.AuthException;
import com.cn.auth.service.AuthService;
import com.cn.common.entity.User;
import com.cn.common.exceptions.EmailException;
import com.cn.common.mapper.UserMapper;
import com.cn.common.structure.UserInfoStructure;
import com.cn.common.utils.EmailUtil;
import com.cn.common.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import com.cn.auth.dto.EmailCodeLoginDto;
import org.apache.commons.lang3.StringUtils;
import com.cn.auth.dto.RegisterDto;
import com.cn.auth.dto.ForgotPasswordDto;

/**
 * 认证服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    private final EmailUtil emailUtil;

    private final com.cn.common.utils.RedisUtils redisUtils;

    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            StpUtil.logout();
        }
    }

    @Override
    public void getVerificationCode(final GetVerificationCodeDto dto) {
        final String code = RandomStringUtils.random(8, true, true).toUpperCase();
        try {
            emailUtil.send(dto.getEmail(), code);
        } catch (EmailException e) {
            throw new EmailException(e.getMessage());
        }
    }

    @Override
    public String passwordLogin(final PasswordLoginDto dto) {
        final User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getEmail, dto.getEmail())
                .or()
                .eq(User::getPassword, SaSecureUtil.md5(dto.getPassword())));
        if (user == null) {
            throw new AuthException("登陆账号或密码错误");
        }

        StpUtil.login(user.getId());

        UserUtils.updateUserInfo(new UserInfoStructure()
                .setAvatar(user.getAvatar())
                .setNickname(user.getNickname())
                .setRole(user.getRole())
                .setUserId(user.getId()));
        return StpUtil.getTokenValue();
    }

    @Override
    public void register(final RegisterDto dto) {
        final String code = emailUtil.getCode(dto.getEmail());
        if (StringUtils.isBlank(code) || !code.equals(dto.getCode())) {
            throw new AuthException("验证码错误");
        }
        final User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getEmail, dto.getEmail())
        );
        if (user != null) {
            throw new AuthException("该邮箱已被注册");
        }
        userMapper.insert(new User()
                .setEmail(dto.getEmail())
                .setPassword(SaSecureUtil.md5(dto.getPassword()))
                .setNickname("用户" + RandomStringUtils.random(8, true, true))
        );
        
        // 统计：今日新增用户 +1
        recordNewUser();
        
        emailUtil.clearCode(dto.getEmail());
    }

    @Override
    public String emailCodeLogin(final EmailCodeLoginDto dto) {
        final String code = emailUtil.getCode(dto.getEmail());
        //检验验证码
        if (StringUtils.isBlank(code) || !code.equals(dto.getCode())) {
            throw new AuthException("验证码错误");
        }
        //删除验证码
        emailUtil.clearCode(dto.getEmail());
        //查询用户
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getEmail, dto.getEmail())
        );
        //用户不存在，提示先注册
        if (user == null) {
            throw new AuthException("该邮箱未注册，请先注册");
        }
        //执行登录
        StpUtil.login(user.getId());
        UserUtils.updateUserInfo(new UserInfoStructure()
                .setAvatar(user.getAvatar())
                .setNickname(user.getNickname())
                .setRole(user.getRole())
                .setUserId(user.getId()));

        return StpUtil.getTokenValue();
    }

    @Override
    public void forgotPassword(final ForgotPasswordDto dto) {
        // 检查验证码
        final String code = emailUtil.getCode(dto.getEmail());
        if (StringUtils.isBlank(code) || !code.equals(dto.getCode())) {
            throw new AuthException("验证码错误");
        }
        // 检查用户是否存在
        final User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getEmail, dto.getEmail())
        );
        if (user == null) {
            throw new AuthException("该邮箱未注册");
        }
        // 更新密码
        user.setPassword(SaSecureUtil.md5(dto.getPassword()));
        userMapper.updateById(user);
        // 清除验证码
        emailUtil.clearCode(dto.getEmail());
    }

    /**
     * 记录新增用户统计
     */
    private void recordNewUser() {
        try {
            String today = java.time.LocalDate.now().toString();
            String key = com.cn.common.constant.SystemStatsConstant.USER_NEW_PREFIX + today;
            redisUtils.increment(key, 1L);
            redisUtils.expire(key, com.cn.common.constant.CacheExpireConstant.EXPIRE_48_HOURS);
            log.debug("记录新增用户统计: date={}", today);
        } catch (Exception e) {
            log.error("记录新增用户统计失败", e);
        }
    }

}
