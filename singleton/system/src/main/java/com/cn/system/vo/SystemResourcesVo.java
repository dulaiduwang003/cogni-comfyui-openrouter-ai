package com.cn.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 系统资源统计 VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemResourcesVo {

    /**
     * CPU 使用率 (%)
     */
    private Double cpuUsage;

    /**
     * 内存使用率 (%)
     */
    private Double memoryUsage;

    /**
     * 总内存 (MB)
     */
    private Long memoryTotal;

    /**
     * 已用内存 (MB)
     */
    private Long memoryUsed;

    /**
     * 可用内存 (MB)
     */
    private Long memoryFree;
}

