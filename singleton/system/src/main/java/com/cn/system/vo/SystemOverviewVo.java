package com.cn.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 系统概况统计 VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemOverviewVo {

    /**
     * 用户统计
     */
    private UserStatsVo userStats;

    /**
     * AI 对话统计
     */
    private AiStatsVo aiStats;

    /**
     * 工作流统计
     */
    private WorkflowStatsVo workflowStats;

    /**
     * 系统资源统计
     */
    private SystemResourcesVo systemResources;

    /**
     * 任务队列统计
     */
    private TaskStatsVo taskStats;

    /**
     * 统计时间戳
     */
    private LocalDateTime timestamp;
}

