package com.cn.notice.controller;

import com.cn.common.annotations.RateLimit;
import com.cn.common.msg.Result;
import com.cn.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 通知控制器
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
@Validated
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    @RateLimit(permitsPerSecond = 5.0, limitType = RateLimit.LimitType.GLOBAL)
    public Result get() {
        return Result.data(noticeService.getNotice());
    }
}


