package com.cn.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务队列统计 VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatsVo {

    /**
     * 队列中的任务数
     */
    private Long queuedTasks;

    /**
     * 正在构建的任务数
     */
    private Integer buildingTasks;
}

