package com.cn.system.service;

import com.cn.system.vo.SystemOverviewVo;

/**
 * 系统概况统计服务
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface SystemOverviewService {

    /**
     * 获取系统概况统计数据
     */
    SystemOverviewVo getSystemOverview();
}

