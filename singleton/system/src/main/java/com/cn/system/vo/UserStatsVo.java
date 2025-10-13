package com.cn.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户统计 VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsVo {

    /**
     * 用户总数
     */
    private Long totalUsers;

    /**
     * 当前在线用户数（WebSocket 连接数）
     */
    private Integer onlineUsers;

    /**
     * 今日新增用户数
     */
    private Long todayNewUsers;
}

