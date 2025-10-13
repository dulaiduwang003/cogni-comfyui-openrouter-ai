package com.cn.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.cn.common.annotations.RateLimit;
import com.cn.common.enums.RoleEnum;
import com.cn.common.msg.Result;
import com.cn.common.vo.PageVo;
import com.cn.system.dto.*;
import com.cn.system.service.SystemUserService;
import com.cn.system.vo.SystemUserVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/system/user")
@Validated
@RequiredArgsConstructor
public class SystemUserController {

    private final SystemUserService systemUserService;

    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 5.0, limitType = RateLimit.LimitType.GLOBAL)
    public Result page(@RequestParam(defaultValue = "1") @Min(1) Integer page,
                       @RequestParam(defaultValue = "10") @Min(1) Integer size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String role) {
        PageVo<SystemUserVo> data = systemUserService.page(page, size, keyword, role);
        return Result.data(data);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result create(@RequestBody @Valid CreateUserDto dto) {
        Long id = systemUserService.create(dto);
        return Result.data(id);
    }

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result update(@RequestBody @Valid UpdateUserDto dto) {
        systemUserService.update(dto);
        return Result.ok();
    }

    @PostMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result delete(@RequestBody @Valid DeleteUserDto dto) {
        systemUserService.delete(dto);
        return Result.ok();
    }
} 