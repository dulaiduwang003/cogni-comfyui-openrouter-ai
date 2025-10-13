package com.cn.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作流统计 VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStatsVo {

    /**
     * 工作流总数
     */
    private Long totalWorkflows;

    /**
     * 今日任务提交总数
     */
    private Long todayTasks;

    /**
     * 今日成功任务数
     */
    private Long todaySuccessTasks;

    /**
     * 今日失败任务数
     */
    private Long todayFailedTasks;

    /**
     * 今日取消任务数
     */
    private Long todayCancelledTasks;
}

