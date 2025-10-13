package com.cn.system.utils;

import com.cn.system.vo.SystemResourcesVo;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;

/**
 * 系统资源监控工具类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
public class SystemMetricsUtil {

    private static final OperatingSystemMXBean OS_BEAN =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /**
     * 获取系统资源统计信息
     */
    public static SystemResourcesVo getSystemResources() {
        try {
            // CPU 使用率
            double cpuUsage = OS_BEAN.getCpuLoad() * 100;

            // 内存信息
            long totalMemory = OS_BEAN.getTotalMemorySize();
            long freeMemory = OS_BEAN.getFreeMemorySize();
            long usedMemory = totalMemory - freeMemory;

            // 内存使用率
            double memoryUsage = totalMemory > 0
                    ? (double) usedMemory / totalMemory * 100
                    : 0.0;

            // 转换为 MB
            long totalMemoryMb = totalMemory / 1024 / 1024;
            long usedMemoryMb = usedMemory / 1024 / 1024;
            long freeMemoryMb = freeMemory / 1024 / 1024;

            return SystemResourcesVo.builder()
                    .cpuUsage(Math.round(cpuUsage * 10) / 10.0)  // 保留1位小数
                    .memoryUsage(Math.round(memoryUsage * 10) / 10.0)
                    .memoryTotal(totalMemoryMb)
                    .memoryUsed(usedMemoryMb)
                    .memoryFree(freeMemoryMb)
                    .build();

        } catch (Exception e) {
            log.error("获取系统资源信息失败", e);
            return SystemResourcesVo.builder()
                    .cpuUsage(0.0)
                    .memoryUsage(0.0)
                    .memoryTotal(0L)
                    .memoryUsed(0L)
                    .memoryFree(0L)
                    .build();
        }
    }
}

