package com.cn.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cn.common.constant.CacheExpireConstant;
import com.cn.common.constant.SystemStatsConstant;
import com.cn.common.entity.User;
import com.cn.common.mapper.UserMapper;
import com.cn.common.mapper.WorkflowMapper;
import com.cn.common.mapper.WorkflowResultMapper;
import com.cn.common.utils.RedisUtils;
import com.cn.system.service.SystemOverviewService;
import com.cn.system.utils.SystemMetricsUtil;
import com.cn.system.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 系统概况统计服务实现
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemOverviewServiceImpl implements SystemOverviewService {

    private final RedisUtils redisUtils;
    private final UserMapper userMapper;
    private final WorkflowMapper workflowMapper;
    private final WorkflowResultMapper workflowResultMapper;

    @Override
    public SystemOverviewVo getSystemOverview() {
        String today = LocalDate.now().toString();

        return SystemOverviewVo.builder()
                .userStats(getUserStats(today))
                .aiStats(getAiStats(today))
                .workflowStats(getWorkflowStats(today))
                .systemResources(getSystemResources())
                .taskStats(getTaskStats())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 获取用户统计数据
     */
    private UserStatsVo getUserStats(String today) {
        // 1. 今日新增用户（Redis优先，数据库兜底）
        Long todayNewUsers = getFromRedisOrDb(
                SystemStatsConstant.USER_NEW_PREFIX + today,
                () -> userMapper.selectCount(
                        new LambdaQueryWrapper<User>()
                                .ge(User::getCreateTime, LocalDate.now().atStartOfDay())
                                .lt(User::getCreateTime, LocalDate.now().plusDays(1).atStartOfDay())
                ),
                CacheExpireConstant.EXPIRE_5_MINUTES
        );

        // 2. 用户总数（缓存5分钟）
        Long totalUsers = getFromRedisOrDb(
                SystemStatsConstant.USER_TOTAL_COUNT,
                () -> userMapper.selectCount(null),
                CacheExpireConstant.EXPIRE_5_MINUTES
        );

        // 3. 在线用户数（WebSocket连接数）
        Integer onlineUsers = (Integer) redisUtils.get(SystemStatsConstant.WEBSOCKET_ONLINE_USERS);

        return UserStatsVo.builder()
                .totalUsers(totalUsers != null ? totalUsers : 0L)
                .onlineUsers(onlineUsers != null ? onlineUsers : 0)
                .todayNewUsers(todayNewUsers != null ? todayNewUsers : 0L)
                .build();
    }

    /**
     * 获取 AI 对话统计数据
     */
    private AiStatsVo getAiStats(String today) {
        // 1. 今日 API 调用次数
        Long apiCalls = getLongFromRedis(SystemStatsConstant.AI_API_CALLS_PREFIX + today);

        // 2. 今日 Token 消耗
        Long tokensUsed = getLongFromRedis(SystemStatsConstant.AI_TOKENS_PREFIX + today);

        // 3. 今日模型统计（Hash）
        Map<Object, Object> modelStatsMap = redisUtils.hashGetAll(
                SystemStatsConstant.AI_MODELS_PREFIX + today
        );

        List<ModelCallStatsVo> activeModels = modelStatsMap.entrySet().stream()
                .map(entry -> ModelCallStatsVo.builder()
                        .modelName((String) entry.getKey())
                        .callCount(convertToLong(entry.getValue()))
                        .build())
                .sorted(Comparator.comparing(ModelCallStatsVo::getCallCount).reversed())
                .limit(5)  // 只取前5个热门模型
                .collect(Collectors.toList());

        return AiStatsVo.builder()
                .todayApiCalls(apiCalls != null ? apiCalls : 0L)
                .todayTokensUsed(tokensUsed != null ? tokensUsed : 0L)
                .todayConversations(0L)  // 可选字段，暂不统计
                .activeModels(activeModels)
                .build();
    }

    /**
     * 获取工作流统计数据
     */
    private WorkflowStatsVo getWorkflowStats(String today) {
        // 1. 工作流总数（缓存5分钟）
        Long totalWorkflows = getFromRedisOrDb(
                SystemStatsConstant.WORKFLOW_TOTAL_COUNT,
                () -> workflowMapper.selectCount(null),
                CacheExpireConstant.EXPIRE_5_MINUTES
        );

        // 2. 启用的工作流数（缓存5分钟）
        // 注意：Workflow表中没有status字段，所有工作流默认都是启用状态

        // 3. 今日任务提交数（从Redis获取，定时任务会更新）
        // 注意：任务状态存储在Redis中，只有成功的任务才会写入WorkflowResult表
        Long todayTasks = getLongFromRedis(SystemStatsConstant.TASK_SUBMITTED_PREFIX + today);

        // 4. 今日成功任务数（从Redis获取，定时任务会更新）
        Long todaySuccessTasks = getLongFromRedis(SystemStatsConstant.TASK_SUCCESS_PREFIX + today);

        // 5. 今日失败任务数（从Redis获取，定时任务会更新）
        Long todayFailedTasks = getLongFromRedis(SystemStatsConstant.TASK_FAILED_PREFIX + today);

        // 6. 今日取消任务数（从Redis获取，定时任务会更新）
        Long todayCancelledTasks = getLongFromRedis(SystemStatsConstant.TASK_CANCELLED_PREFIX + today);

        return WorkflowStatsVo.builder()
                .totalWorkflows(totalWorkflows != null ? totalWorkflows : 0L)
                .todayTasks(todayTasks != null ? todayTasks : 0L)
                .todaySuccessTasks(todaySuccessTasks != null ? todaySuccessTasks : 0L)
                .todayFailedTasks(todayFailedTasks != null ? todayFailedTasks : 0L)
                .todayCancelledTasks(todayCancelledTasks != null ? todayCancelledTasks : 0L)
                .build();
    }

    /**
     * 获取系统资源统计
     */
    private SystemResourcesVo getSystemResources() {
        return SystemMetricsUtil.getSystemResources();
    }

    /**
     * 获取任务队列统计
     */
    private TaskStatsVo getTaskStats() {
        // 1. 队列中的任务数
        Long queuedTasks = getLongFromRedis(SystemStatsConstant.TASK_QUEUE_SIZE);

        // 2. 正在构建的任务数
        Integer buildingTasks = (Integer) redisUtils.get(SystemStatsConstant.TASK_BUILDING_COUNT);

        return TaskStatsVo.builder()
                .queuedTasks(queuedTasks != null ? queuedTasks : 0L)
                .buildingTasks(buildingTasks != null ? buildingTasks : 0)
                .build();
    }

    /**
     * 从 Redis 获取数据，如果为空则从数据库查询并回写 Redis
     */
    private Long getFromRedisOrDb(String redisKey, Supplier<Long> dbQuery, int expireSeconds) {
        try {
            // 1. 尝试从 Redis 获取
            Long value = getLongFromRedis(redisKey);

            // 2. 如果为空，从数据库查询
            if (value == null) {
                value = dbQuery.get();

                // 3. 回写 Redis（即使为0也写入，避免缓存穿透）
                if (value != null) {
                    redisUtils.set(redisKey, value, expireSeconds);
                    log.debug("从数据库查询并缓存: key={}, value={}", redisKey, value);
                }
            }

            return value;
        } catch (Exception e) {
            log.error("获取统计数据失败: key={}", redisKey, e);
            // 异常时尝试从数据库查询
            try {
                return dbQuery.get();
            } catch (Exception dbEx) {
                log.error("数据库查询失败: key={}", redisKey, dbEx);
                return 0L;
            }
        }
    }

    /**
     * 从 Redis 获取 Long 值
     */
    private Long getLongFromRedis(String key) {
        Object value = redisUtils.get(key);
        return convertToLong(value);
    }

    /**
     * 转换为 Long 类型
     */
    private Long convertToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                log.warn("无法转换为Long: {}", value);
                return null;
            }
        }
        return null;
    }
}

