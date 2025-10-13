package com.cn.comfyui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.comfyui.constant.ComfyuiConstant;
import com.cn.comfyui.dto.CancelTaskDto;
import com.cn.comfyui.dto.RemakeTaskDto;
import com.cn.comfyui.dto.SubmitTaskDto;
import com.cn.comfyui.excepitons.ComfyuiException;
import com.cn.comfyui.model.TaskNodeContainer;
import com.cn.comfyui.service.WorkflowService;
import com.cn.comfyui.structure.TaskInfoStructure;
import com.cn.comfyui.structure.TaskStructure;
import com.cn.comfyui.vo.TaskProgressVo;
import com.cn.comfyui.vo.WorkflowCategoryVo;
import com.cn.comfyui.vo.WorkflowInterfaceVo;
import com.cn.comfyui.vo.WorkflowsVo;
import com.cn.comfyui.websocket.service.TaskProgressPushService;
import com.cn.common.configuration.ComfyuiConfiguration;
import com.cn.common.entity.Workflow;
import com.cn.common.entity.WorkflowCategory;
import com.cn.common.entity.WorkflowForm;
import com.cn.common.enums.RequiredEnum;
import com.cn.common.enums.TaskStatusEnum;
import com.cn.common.exceptions.CreditException;
import com.cn.common.mapper.WorkflowCategoryMapper;
import com.cn.common.mapper.WorkflowFormMapper;
import com.cn.common.mapper.WorkflowMapper;
import com.cn.common.mapper.WorkflowResultMapper;
import com.cn.common.utils.RedisUtils;
import com.cn.common.utils.UploadUtil;
import com.cn.common.utils.UserUtils;
import com.cn.common.utils.CreditUtils;
import com.cn.common.vo.PageVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cn.comfyui.constant.ComfyuiConstant.*;

/**
 * 工作流服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("all")
public class WorkflowServiceImpl implements WorkflowService {

    private final ComfyuiConfiguration comfyuiConfiguration;

    private final UploadUtil uploadUtil;

    private final RedisUtils redisUtils;

    private final RedissonClient redissonClient;

    private final WorkflowMapper workflowsMapper;

    private final WorkflowResultMapper worksMapper;

    private final WorkflowFormMapper workflowsFormMapper;

    private final WorkflowCategoryMapper workflowsFilterMapper;

    private final TaskProgressPushService taskProgressPushService;

    private final CreditUtils creditUtils;

    private static final String COMFYUI_SUBMIT_TASK_LOCK = "COMFYUI_SUBMIT_TASK_LOCK:";

    @Override
    public TaskProgressVo getTaskProgress(final String taskId) {
        TaskInfoStructure value = (TaskInfoStructure) redisUtils
                .hashGet(COMFYUI_TASK_LIST + UserUtils.getCurrentLoginId(), taskId);
        if (value == null) {
            throw new ComfyuiException("绘图任务不存在");
        }
        TaskProgressVo vo = new TaskProgressVo()
                .setProgress(value.getProgress())
                .setWorkflowName(value.getWorkflowName())
                .setCreateTime(value.getCreateTime())
                .setTaskId(taskId)
                .setStatus(value.getStatus())
                .setWorkflowResultModel(value.getWorkflowResultModel())
                .setCreditsDeducted(value.getCreditsDeducted());

        if (value.getStatus().equals(TaskStatusEnum.WAIT.getDec())) {
            final Object location = redisUtils.hashGet(COMFYUI_QUEUE_INDEX, taskId);
            vo.setLocation(location != null ? Long.parseLong(location.toString()) : 0);
        }
        return vo;
    }

    @Override
    public List<TaskProgressVo> getTaskProgressList() {
        // 保持原有方法不变，用于兼容性
        Map<Object, Object> objectObjectMap = redisUtils.hashGetAll(COMFYUI_TASK_LIST + UserUtils.getCurrentLoginId());
        return objectObjectMap.keySet().stream().map(c -> {
            TaskInfoStructure o = (TaskInfoStructure) objectObjectMap.get(c);
            final TaskProgressVo vo = new TaskProgressVo()
                    .setTaskId(c.toString())
                    .setLocation(o.getLocation())
                    .setWorkflowName(o.getWorkflowName())
                    .setStatus(o.getStatus())
                    .setProgress(o.getProgress())
                    .setWorkflowResultModel(o.getWorkflowResultModel())
                    .setCreateTime(o.getCreateTime())
                    .setCreditsDeducted(o.getCreditsDeducted());
            if (o.getStatus().equals(TaskStatusEnum.WAIT.getDec())) {
                Object location = redisUtils.hashGet(COMFYUI_QUEUE_INDEX, o.getTaskId());
                vo.setLocation(location != null ? Long.parseLong(location.toString()) : 0);
            }
            return vo;
        }).sorted(Comparator.comparing(TaskProgressVo::getCreateTime).reversed()).toList();
    }

    @Override
    public PageVo<TaskProgressVo> getTaskProgressPage(final Long page, final String status) {
        final Long pageSize = 10L;
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        final String taskTimeIndexKey = COMFYUI_TASK_TIME_INDEX + currentLoginId;
        final String taskListKey = COMFYUI_TASK_LIST + currentLoginId;

        // 如果没有指定状态筛选，使用原有的分页逻辑
        if (status == null || status.trim().isEmpty()) {
            return getTaskProgressPageWithoutFilter(page, pageSize, taskTimeIndexKey, taskListKey);
        }

        // 验证状态参数是否有效
        if (!isValidTaskStatus(status)) {
            throw new ComfyuiException("无效的任务状态: " + status);
        }

        // 有状态筛选的情况，需要获取所有任务然后筛选
        Set<Object> allTaskIds = redisUtils.zsetReverseRange(taskTimeIndexKey, 0, -1);
        
        if (allTaskIds.isEmpty()) {
            return new PageVo<TaskProgressVo>().setTotal(0L).setItems(new ArrayList<>());
        }

        // 批量获取所有任务详情
        Object[] taskIdArray = allTaskIds.toArray();
        Map<Object, Object> taskMap = redisUtils.hashMultiGet(taskListKey, Arrays.asList(taskIdArray));
        
        // 筛选指定状态的任务
        List<TaskProgressVo> filteredTasks = new ArrayList<>();
        for (Object taskId : allTaskIds) {
            TaskInfoStructure task = (TaskInfoStructure) taskMap.get(taskId);
            if (task != null && status.equals(task.getStatus())) {
                final TaskProgressVo vo = new TaskProgressVo()
                        .setTaskId(taskId.toString())
                        .setLocation(task.getLocation())
                        .setStatus(task.getStatus())
                        .setWorkflowName(task.getWorkflowName())
                        .setProgress(task.getProgress())
                        .setWorkflowResultModel(task.getWorkflowResultModel())
                        .setCreateTime(task.getCreateTime())
                        .setCreditsDeducted(task.getCreditsDeducted());
                
                // 如果是等待状态，获取队列位置
                if (task.getStatus().equals(TaskStatusEnum.WAIT.getDec())) {
                    Object location = redisUtils.hashGet(COMFYUI_QUEUE_INDEX, task.getTaskId());
                    vo.setLocation(location != null ? Long.parseLong(location.toString()) : 0);
                }
                filteredTasks.add(vo);
            }
        }

        // 手动分页
        final long total = filteredTasks.size();
        final long start = (page - 1) * pageSize;
        final long end = Math.min(start + pageSize, total);
        
        List<TaskProgressVo> pagedTasks = new ArrayList<>();
        if (start < total) {
            pagedTasks = filteredTasks.subList((int) start, (int) end);
        }
        
        return new PageVo<TaskProgressVo>()
                .setTotal(total)
                .setItems(pagedTasks);
    }

    /**
     * 无状态筛选的分页查询（原有逻辑）
     */
    private PageVo<TaskProgressVo> getTaskProgressPageWithoutFilter(Long page, Long pageSize, String taskTimeIndexKey, String taskListKey) {
        // 计算分页参数
        final long start = (page - 1) * pageSize;
        final long end = start + pageSize - 1;
        
        // 从有序集合按时间倒序获取taskId列表（分页）
        Set<Object> taskIds = redisUtils.zsetReverseRange(taskTimeIndexKey, start, end);

        // 获取总数
        Long total = redisUtils.zsetCount(taskTimeIndexKey);
        if (total == null) {
            total = 0L;
        }

        // 批量获取任务详情
        List<TaskProgressVo> tasks = new ArrayList<>();
        if (!taskIds.isEmpty()) {
            // 将taskIds转换为数组用于批量查询
            Object[] taskIdArray = taskIds.toArray();
            Map<Object, Object> taskMap = redisUtils.hashMultiGet(taskListKey, Arrays.asList(taskIdArray));
            
            // 转换为VO列表，保持ZSet的顺序
            for (Object taskId : taskIds) {
                TaskInfoStructure task = (TaskInfoStructure) taskMap.get(taskId);
                if (task != null) {
                    final TaskProgressVo vo = new TaskProgressVo()
                            .setTaskId(taskId.toString())
                            .setLocation(task.getLocation())
                            .setStatus(task.getStatus())
                            .setWorkflowName(task.getWorkflowName())
                            .setProgress(task.getProgress())
                            .setWorkflowResultModel(task.getWorkflowResultModel())
                            .setCreateTime(task.getCreateTime())
                            .setCreditsDeducted(task.getCreditsDeducted());

                    
                    // 如果是等待状态，获取队列位置
                    if (task.getStatus().equals(TaskStatusEnum.WAIT.getDec())) {
                        Object location = redisUtils.hashGet(COMFYUI_QUEUE_INDEX, task.getTaskId());
                        vo.setLocation(location != null ? Long.parseLong(location.toString()) : 0);
                    }
                    tasks.add(vo);
                }
            }
        }
        
        return new PageVo<TaskProgressVo>()
                .setTotal(total)
                .setItems(tasks);
    }

    /**
     * 验证任务状态是否有效
     */
    private boolean isValidTaskStatus(String status) {
        return TaskStatusEnum.WAIT.getDec().equals(status) ||
               TaskStatusEnum.BUILD.getDec().equals(status) ||
               TaskStatusEnum.SUCCEED.getDec().equals(status) ||
               TaskStatusEnum.CANCELED.getDec().equals(status) ||
               TaskStatusEnum.FAILED.getDec().equals(status);
    }

    @Override
    public String submitTask(final SubmitTaskDto dto) {
        return processTaskSubmission(dto.getWorkflowId(), dto.getNodeContainer(), null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String remakeTask(final RemakeTaskDto dto) {
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        final String key = COMFYUI_TASK_LIST + currentLoginId;
        final String taskId = dto.getTaskId();

        // 获取原始任务信息
        TaskInfoStructure originalTask = (TaskInfoStructure) redisUtils.hashGet(key, taskId);
        if (originalTask == null) {
            throw new ComfyuiException("任务不存在");
        }

        // 检查任务状态，只有取消、成功或失败状态的任务可以重新制作
        if (!TaskStatusEnum.CANCELED.getDec().equals(originalTask.getStatus()) &&
                !TaskStatusEnum.SUCCEED.getDec().equals(originalTask.getStatus()) &&
                !TaskStatusEnum.FAILED.getDec().equals(originalTask.getStatus())) {
            throw new ComfyuiException("只有已取消、已完成或失败的任务可以重新制作");
        }

        // 保存任务表单数据
        TaskInfoStructure.Form form = originalTask.getForm();
        if (form == null) {
            throw new ComfyuiException("任务表单数据不存在，无法重新制作");
        }


        // 从Redis删除原任务
        redisUtils.hashDelete(key, taskId);
        
        // 从时间索引ZSet中删除原任务（重新制作时需要删除，因为会创建新任务）
        redisUtils.zsetRemove(COMFYUI_TASK_TIME_INDEX + currentLoginId, taskId);

        // 转换表单数据为Container格式
        List<TaskNodeContainer> containers = form.getTaskNodeContainer().stream()
                .map(c -> new TaskNodeContainer()
                        .setInputs(c.getInputs())
                        .setIsUpload(c.getIsUpload())
                        .setNodeKey(c.getNodeKey())
                        .setNodeValue(c.getNodeValue()))
                .collect(Collectors.toList());

        // 重新制作任务时，使用原任务的积分信息
        Long originalCredits = originalTask.getCreditsDeducted();
        try {
            creditUtils.checkAndFreezeCreditsWithException(
                currentLoginId, 
                originalCredits, 
                "任务重新制作积分冻结 - " + originalTask.getWorkflowName()
            );
        } catch (CreditException e) {
            throw new ComfyuiException(e.getMessage());
        }

        return processTaskSubmission(form.getWorkflowId(), containers, taskId, originalCredits);
    }

    /**
     * 处理任务提交的通用逻辑
     *
     * @param workflowId    工作流ID
     * @param containers     容器列表
     * @param existingTaskId 现有任务ID（用于重新制作），如果是新任务则为null
     * @return 任务ID
     */
    private String processTaskSubmission(final Long workflowId, List<TaskNodeContainer> containers,
            String existingTaskId) {
        return processTaskSubmission(workflowId, containers, existingTaskId, null);
    }

    /**
     * 处理任务提交的通用逻辑（重载方法，支持传入积分信息）
     *
     * @param workflowId    工作流ID
     * @param containers     容器列表
     * @param existingTaskId 现有任务ID（用于重新制作），如果是新任务则为null
     * @param presetCredits  预设的积分信息（用于重新制作时使用原任务积分）
     * @return 任务ID
     */
    private String processTaskSubmission(final Long workflowId, List<TaskNodeContainer> containers,
            String existingTaskId, Long presetCredits) {
        RLock globalLock = redissonClient.getLock(ComfyuiConstant.COMFYUI_GLOBAL_SUBMIT_LOCK);

        try {
            if (!globalLock.tryLock(5, 5, TimeUnit.SECONDS)) {
                throw new ComfyuiException("系统繁忙，请稍后再试");
            }

            try {
                if (redisUtils.keySize(ComfyuiConstant.COMFYUI_QUEUE) > comfyuiConfiguration.getSubmitTaskMax()) {
                    throw new ComfyuiException("使用人数过多，请稍后再试试");
                }

                final Long currentLoginId = UserUtils.getCurrentLoginId();
                RLock userLock = redissonClient.getLock(COMFYUI_SUBMIT_TASK_LOCK + currentLoginId);

                try {
                    if (!userLock.tryLock(10, 10, TimeUnit.SECONDS)) {
                        throw new ComfyuiException("您点的太快啦，请等等再试");
                    }

                    try {
                        Workflow workflows = getWorkflow(workflowId);
                        
                        // 确定需要的积分数量并检查冻结
                        Long creditsRequired;
                        if (presetCredits != null) {
                            // 重新制作任务时使用预设积分
                            creditsRequired = presetCredits;
                        } else {
                            // 新任务时从工作流获取积分要求并冻结
                            creditsRequired = workflows.getCreditsDeducted();
                            if (creditsRequired == null) {
                                creditsRequired = 0L;
                            }
                            
                            // 检查和冻结积分
                            try {
                                creditUtils.checkAndFreezeCreditsWithException(
                                    currentLoginId, 
                                    creditsRequired, 
                                    "任务提交积分冻结 - " + workflows.getName()
                                );
                            } catch (CreditException e) {
                                throw new ComfyuiException(e.getMessage());
                            }
                        }
                        
                        final String taskId = existingTaskId != null ? existingTaskId : UUID.randomUUID().toString();
                        redisUtils.listPush(ComfyuiConstant.COMFYUI_QUEUE,
                                new TaskStructure()
                                        .setTaskId(taskId)
                                        .setWorkflowId(workflowId)
                                        .setUserId(currentLoginId)
                                        .setTaskNodeContainer(containers));

                        Long queueSize = redisUtils.keySize(ComfyuiConstant.COMFYUI_QUEUE);
                        redisUtils.hashPut(COMFYUI_QUEUE_INDEX, taskId, queueSize);

                        // 保存表单 用于重新制作
                        List<TaskNodeContainer> taskContainers = containers.stream()
                                .map(c -> new TaskNodeContainer()
                                        .setIsUpload(c.getIsUpload())
                                        .setInputs(c.getInputs())
                                        .setNodeKey(c.getNodeKey())
                                        .setNodeValue(c.getNodeValue()))
                                .toList();

                        TaskInfoStructure.Form form = new TaskInfoStructure.Form()
                                .setTaskNodeContainer(taskContainers)
                                .setWorkflowId(workflowId);

                        LocalDateTime createTime = LocalDateTime.now();
                        TaskInfoStructure newTask = new TaskInfoStructure()
                                .setTaskId(taskId)
                                .setWorkflowName(workflows.getName())
                                .setForm(form)
                                .setLocation(queueSize)
                                .setCreateTime(createTime)
                                .setStatus(TaskStatusEnum.WAIT.getDec())
                                .setCreditsDeducted(creditsRequired);

                        // 保存任务详情到Hash
                        redisUtils.hashPut(COMFYUI_TASK_LIST + currentLoginId, taskId, newTask);
                        
                        // 同时添加到时间索引ZSet（使用时间戳作为分数）
                        long timestamp = createTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                        redisUtils.zsetAdd(COMFYUI_TASK_TIME_INDEX + currentLoginId, taskId, timestamp);

                        // 推送任务提交状态
                        taskProgressPushService.pushTaskStatusChange(currentLoginId, taskId, newTask);

                        // 统计：今日任务提交数 +1
                        recordTaskSubmitted();

                        return taskId;
                    } finally {
                        userLock.unlock();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new ComfyuiException("系统繁忙，请稍后再试");
                }
            } finally {
                globalLock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ComfyuiException("系统繁忙，请稍后再试");
        }
    }

    private Workflow getWorkflow(final Long workflowId){
        Workflow workflow = workflowsMapper.selectOne(new QueryWrapper<Workflow>()
                .lambda().eq(Workflow::getId, workflowId));
        if (workflow == null) {
            throw new ComfyuiException("该工作流已下架或不存在");
        }
        return workflow;
    }

    @Override
    public WorkflowInterfaceVo getWorkflowInterface(final Long workflowId) {
        Workflow workflow = getWorkflow(workflowId);
        final List<WorkflowInterfaceVo.WorkflowsFormContainer> list = workflowsFormMapper
                .selectList(new QueryWrapper<WorkflowForm>()
                        .lambda()
                        .eq(WorkflowForm::getWorkflowId, workflowId))
                .stream()
                .map(c -> new WorkflowInterfaceVo.WorkflowsFormContainer()
                        .setNodeKey(c.getNodeKey())
                        .setInputs(c.getInputs())
                        .setOptions(c.getOptions())
                        .setSize(c.getSize())
                        .setTemplate(c.getTemplate())
                        .setRequired(c.getRequired().equals(RequiredEnum.TRUE.getDec()))
                        .setType(c.getType())
                        .setTips(c.getTips()))
                .toList();
        return new WorkflowInterfaceVo().setWorkflowId(workflowId).setFormContainer(list)
                .setName(workflow.getName())
                .setCreditsDeducted(workflow.getCreditsDeducted());
    }

    @Override
    public PageVo<WorkflowsVo> getWorkflowsPage(final String prompt, final Long categoryId, final Long page) {
        final long pageSize = 20;

        // 创建查询条件
        QueryWrapper<Workflow> queryWrapper = new QueryWrapper<>();
        
        // 添加名称模糊搜索条件
        if (StringUtils.isNotEmpty(prompt)) {
            queryWrapper.lambda().like(Workflow::getName, prompt);
        }
        
        // 如果有筛选ID，添加筛选条件
        if (categoryId != null && categoryId > 0) {
            queryWrapper.lambda().eq(Workflow::getWorkflowCategoryId, categoryId.toString());
        }
        
        // 添加排序
        queryWrapper.lambda().orderByDesc(Workflow::getCreateTime);
        
        // 执行分页查询
        Page<Workflow> pageResult = workflowsMapper.selectPage(new Page<>(page, pageSize), queryWrapper);
        
        // 获取所有需要查询的筛选ID
        List<String> filterIds = pageResult.getRecords().stream()
                .map(Workflow::getWorkflowCategoryId)
                .filter(id -> StringUtils.isNotEmpty(id))
                .distinct()
                .collect(Collectors.toList());
        
        // 一次性查询所有需要的筛选条件
        Map<String, String> filterNameMap = new HashMap<>();
        if (!filterIds.isEmpty()) {
            List<WorkflowCategory> filters = workflowsFilterMapper.selectList(
                    new QueryWrapper<WorkflowCategory>()
                            .lambda()
                            .in(WorkflowCategory::getId, filterIds.stream().map(Long::valueOf).collect(Collectors.toList()))
                            .select(WorkflowCategory::getId, WorkflowCategory::getName)
            );
            
            filterNameMap = filters.stream()
                    .collect(Collectors.toMap(
                            filter -> filter.getId().toString(), 
                            WorkflowCategory::getName
                    ));
        }
        
        // 转换结果，为每个工作流设置对应的筛选名称
        final Map<String, String> finalFilterNameMap = filterNameMap;
        IPage<WorkflowsVo> voPage = pageResult.convert(workflow -> {
            String filterName = null;
            if (StringUtils.isNotEmpty(workflow.getWorkflowCategoryId())) {
                filterName = finalFilterNameMap.get(workflow.getWorkflowCategoryId());
            }
            
            return new WorkflowsVo()
                .setWorkflowId(workflow.getId())
                .setDescription(workflow.getDescription())
                .setUrl(workflow.getUrl())
                .setName(workflow.getName())
                .setCategoryName(filterName)
                .setCreditsDeducted(workflow.getCreditsDeducted());
        });
        
        // 返回分页结果
        return new PageVo<WorkflowsVo>()
            .setTotal(voPage.getTotal())
            .setItems(voPage.getRecords());
    }

    /**
     * 获取工作流筛选列表
     * 
     * @return 筛选列表
     */
    @Override
    public List<WorkflowCategoryVo> getWorkflowCategoryList() {
        // 查询所有筛选条件并按创建时间倒序排序
        List<WorkflowCategory> filterList = workflowsFilterMapper.selectList(
            new QueryWrapper<WorkflowCategory>()
                .lambda()
                .orderByDesc(WorkflowCategory::getCreateTime)
        );
        
        // 将实体转换为VO
        return filterList.stream()
            .map(filter -> new WorkflowCategoryVo()
                .setCategoryId(filter.getId())
                .setName(filter.getName())
  )
            .collect(Collectors.toList());
    }

    @Override
    public void cancelTask(final CancelTaskDto dto) {
        final Long currentLoginId = UserUtils.getCurrentLoginId();
        final String key = COMFYUI_TASK_LIST + currentLoginId;

        // 获取任务状态
        TaskInfoStructure taskInfo = (TaskInfoStructure) redisUtils.hashGet(key, dto.getTaskId());
        if (taskInfo == null) {
            throw new ComfyuiException("任务不存在");
        }

        // 只能取消等待中的任务
        if (!taskInfo.getStatus().equals(TaskStatusEnum.WAIT.getDec())) {
            throw new ComfyuiException("只能取消等待中的任务");
        }

        // 使用全局锁确保任务取消操作的原子性
        RLock cancelLock = redissonClient.getLock(ComfyuiConstant.COMFYUI_GLOBAL_SUBMIT_LOCK);
        try {
            // 设置锁的超时时间为10秒，等待时间也为10秒，确保有足够的时间完成操作
            if (!cancelLock.tryLock(10, 10, TimeUnit.SECONDS)) {
                throw new ComfyuiException("系统繁忙，请稍后再试");
            }

            try {
                // 获取Redis模板，用于直接操作Redis
                RedisTemplate<String, Object> redisTemplate = redisUtils.getRedisTemplate();

                // 从队列中直接删除任务，使用LREM命令
                List<Object> queueItems = redisUtils.listGet(ComfyuiConstant.COMFYUI_QUEUE, 0, -1);
                boolean removed = false;

                for (Object item : queueItems) {
                    if (item instanceof TaskStructure) {
                        TaskStructure taskStructure = (TaskStructure) item;
                        if (taskStructure.getTaskId().equals(dto.getTaskId())) {
                            // 使用RedisTemplate直接执行LREM命令，从列表中删除元素
                            Long removeResult = redisTemplate.opsForList().remove(
                                    ComfyuiConstant.COMFYUI_QUEUE,
                                    1,
                                    item);

                            removed = (removeResult != null && removeResult > 0);
                            break;
                        }
                    }
                }

                if (!removed) {
                    log.warn("任务 {} 在队列中未找到，可能已经开始处理", dto.getTaskId());

                }

                // 任务取消，退还冻结的积分
                if (taskInfo.getCreditsDeducted() != null && taskInfo.getCreditsDeducted() > 0) {
                    boolean refunded = creditUtils.refundCredits(
                        currentLoginId, 
                        taskInfo.getCreditsDeducted(), 
                        "任务取消积分退还 - " + taskInfo.getWorkflowName()
                    );
                    if (!refunded) {
                        log.warn("任务 {} 取消但积分退还失败，用户ID: {}, 积分: {}", 
                            dto.getTaskId(), currentLoginId, taskInfo.getCreditsDeducted());
                    }
                }

                // 更新任务状态为已取消
                redisUtils.hashPut(key, dto.getTaskId(), taskInfo.setStatus(TaskStatusEnum.CANCELED.getDec()));

                // 统计：今日取消任务数 +1
                recordTaskCancelled();

                // 从队列索引中移除任务
                redisUtils.hashDelete(COMFYUI_QUEUE_INDEX, dto.getTaskId());
                
                // 注意：不要从时间索引ZSet中删除任务，保留以便分页查询能找到已取消的任务
                // redisUtils.zsetRemove(COMFYUI_TASK_TIME_INDEX + currentLoginId, dto.getTaskId());

                // 更新其他任务在队列中的位置
                Map<Object, Object> tasks = redisUtils.hashGetAll(COMFYUI_QUEUE_INDEX);
                for (Map.Entry<Object, Object> entry : tasks.entrySet()) {
                    Object id = entry.getKey();
                    Integer currentValue = (Integer) entry.getValue();
                    if (currentValue != null && currentValue > taskInfo.getLocation()) {
                        redisUtils.hashPut(COMFYUI_QUEUE_INDEX, id.toString(), currentValue - 1);
                    }
                }

            } finally {
                cancelLock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ComfyuiException("取消任务过程被中断");
        }
    }

    /**
     * 记录任务提交统计
     */
    private void recordTaskSubmitted() {
        try {
            String today = java.time.LocalDate.now().toString();
            String key = com.cn.common.constant.SystemStatsConstant.TASK_SUBMITTED_PREFIX + today;
            redisUtils.increment(key, 1L);
            redisUtils.expire(key, com.cn.common.constant.CacheExpireConstant.EXPIRE_48_HOURS);
            log.debug("记录任务提交统计: date={}", today);
        } catch (Exception e) {
            log.error("记录任务提交统计失败", e);
        }
    }

    /**
     * 记录任务取消统计
     */
    private void recordTaskCancelled() {
        try {
            String today = java.time.LocalDate.now().toString();
            String key = com.cn.common.constant.SystemStatsConstant.TASK_CANCELLED_PREFIX + today;
            redisUtils.increment(key, 1L);
            redisUtils.expire(key, com.cn.common.constant.CacheExpireConstant.EXPIRE_48_HOURS);
            log.debug("记录任务取消统计: date={}", today);
        } catch (Exception e) {
            log.error("记录任务取消统计失败", e);
        }
    }

}
