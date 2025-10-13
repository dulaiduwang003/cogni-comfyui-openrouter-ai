package com.cn.auth.controller;


import com.cn.auth.dto.EmailCodeLoginDto;
import com.cn.auth.dto.GetVerificationCodeDto;
import com.cn.auth.dto.PasswordLoginDto;
import com.cn.auth.dto.RegisterDto;
import com.cn.auth.dto.ForgotPasswordDto;
import com.cn.auth.exceptions.AuthException;
import com.cn.auth.service.AuthService;
import com.cn.common.msg.Result;
import com.cn.common.annotations.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    /**
     * Login result.
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/password-login", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.2, limitType = RateLimit.LimitType.IP, message = "登录过于频繁，请稍后再试")
    public Result passwordLogin(@RequestBody @Validated final PasswordLoginDto dto) {
        try {
            return Result.data(authService.passwordLogin(dto));
        } catch (AuthException e) {
            return Result.error(e.getMessage());
        }
    }



    /**
     * Login result.
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/verification-code", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.017, limitType = RateLimit.LimitType.IP, message = "验证码请求过于频繁，请1分钟后再试")
    public Result getVerificationCode(@RequestBody @Validated final GetVerificationCodeDto dto) {
        try {
            authService.getVerificationCode(dto);
            return Result.ok();
        } catch (AuthException e) {
            return Result.error(e.getMessage());
        }
    }



    /**
     * Logout result.
     *
     * @return the result
     */
    @PostMapping(value = "/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result logout() {
        authService.logout();
        return Result.ok();
    }


    /**
     * Register result.
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.05, limitType = RateLimit.LimitType.IP, message = "注册过于频繁，请稍后再试")
    public Result register(@RequestBody @Validated final RegisterDto dto) {
        try {
            authService.register(dto);
            return Result.ok();
        } catch (AuthException e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * Email login result.
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/email-login", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.2, limitType = RateLimit.LimitType.IP, message = "登录过于频繁，请稍后再试")
    public Result emailLogin(@RequestBody @Validated final EmailCodeLoginDto dto) {
        try {
            return Result.data(authService.emailCodeLogin(dto));
        } catch (AuthException e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * Forgot password result.
     *
     * @param dto the dto
     * @return the result
     */
    @PostMapping(value = "/forgot-password", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.033, limitType = RateLimit.LimitType.IP, message = "重置密码请求过于频繁，请稍后再试")
    public Result forgotPassword(@RequestBody @Validated final ForgotPasswordDto dto) {
        try {
            authService.forgotPassword(dto);
            return Result.ok();
        } catch (AuthException e) {
            return Result.error(e.getMessage());
        }
    }
}
