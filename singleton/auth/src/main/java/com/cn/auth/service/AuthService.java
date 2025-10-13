package com.cn.auth.service;


import com.cn.auth.dto.EmailCodeLoginDto;
import com.cn.auth.dto.PasswordLoginDto;
import com.cn.auth.dto.GetVerificationCodeDto;
import com.cn.auth.dto.RegisterDto;
import com.cn.auth.dto.ForgotPasswordDto;

/**
 * 认证服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface AuthService {



    String passwordLogin(final PasswordLoginDto dto);

    String emailCodeLogin(final EmailCodeLoginDto dto);

    void getVerificationCode(final GetVerificationCodeDto dto);

    void register(final RegisterDto dto);

    void logout();

    void forgotPassword(final ForgotPasswordDto dto);
    

}
