package com.cn.system.controller;

import com.cn.common.annotations.RateLimit;
import com.cn.common.msg.Result;
import com.cn.system.dto.SystemNoticeSetDto;
import com.cn.system.service.SystemNoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统公告设置接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@RestController
@RequestMapping("/system/notice")
@Validated
@RequiredArgsConstructor
public class SystemNoticeController {

    private final SystemNoticeService systemNoticeService;

    @PostMapping(value = "/set", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.2, limitType = RateLimit.LimitType.USER, message = "公告设置过于频繁，请稍后再试")
    public Result set(@RequestBody @Valid final SystemNoticeSetDto dto) {
        systemNoticeService.set(dto);
        return Result.ok();
    }

    @PostMapping(value = "/clear", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 0.2, limitType = RateLimit.LimitType.USER, message = "公告清空过于频繁，请稍后再试")
    public Result clear() {
        systemNoticeService.clear();
        return Result.ok();
    }
}


