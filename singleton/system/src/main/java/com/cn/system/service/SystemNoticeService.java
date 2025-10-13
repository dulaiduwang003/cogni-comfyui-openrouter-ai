package com.cn.system.service;

import com.cn.system.dto.SystemNoticeSetDto;

/**
 * 系统公告设置服务
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface SystemNoticeService {

    /**
     * 设置公告
     */
    void set(SystemNoticeSetDto dto);

    /**
     * 清空公告
     */
    void clear();
}


