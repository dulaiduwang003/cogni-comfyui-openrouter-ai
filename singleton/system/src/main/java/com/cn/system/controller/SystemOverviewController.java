package com.cn.system.controller;

import com.cn.common.msg.Result;
import com.cn.system.service.SystemOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统概况统计接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@RestController
@RequestMapping("/system/overview")
@RequiredArgsConstructor
public class SystemOverviewController {

    private final SystemOverviewService systemOverviewService;

    /**
     * 获取系统概况统计数据
     */
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getSystemOverview() {
        return Result.data(systemOverviewService.getSystemOverview());
    }
}

