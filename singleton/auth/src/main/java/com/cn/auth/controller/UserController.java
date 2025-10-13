package com.cn.auth.controller;


import com.cn.auth.dto.UpdateAvatarDto;
import com.cn.auth.dto.UpdateNicknameDto;
import com.cn.auth.service.UserService;
import com.cn.common.msg.Result;
import com.cn.common.annotations.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping(value = "/get/user-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getCurrentUserInfo() {
         return Result.data(userService.getUserInfo());
    }

    /**
     * 更新用户头像
     *
     * @param dto 包含新头像URL的DTO
     * @return 更新结果
     */
    @PostMapping(value = "/update-avatar", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.1, limitType = RateLimit.LimitType.USER, message = "头像更新过于频繁，请稍后再试")
    public Result updateAvatar(@RequestBody @Validated UpdateAvatarDto dto) {
         userService.updateAvatar(dto);
            return Result.ok();
    }

    /**
     * 更新用户昵称
     *
     * @param dto 包含新昵称的DTO
     * @return 更新结果
     */
    @PostMapping(value = "/update-nickname", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.1, limitType = RateLimit.LimitType.USER, message = "昵称更新过于频繁，请稍后再试")
    public Result updateNickname(@RequestBody @Validated UpdateNicknameDto dto) {
         userService.updateNickname(dto);
            return Result.ok();
    }

    /**
     * 获取当前用户积分信息
     *
     * @return 用户积分信息
     */
    @GetMapping(value = "/get/credits", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getUserCredits() {
          return Result.data(userService.getUserCredits());
    }

    /**
     * 获取当前用户积分交易记录
     *
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @param transactionType 交易类型筛选，可选参数
     * @return 积分交易记录分页数据
     */
    @GetMapping(value = "/get/credit-transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getCreditTransactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String transactionType) {
         return Result.data(userService.getCreditTransactions(page, size, transactionType));
    }


}
