package com.cn.comfyui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.comfyui.dto.BatchDeleteWorkflowResultDto;
import com.cn.comfyui.dto.WorkflowResultDto;
import com.cn.comfyui.excepitons.ComfyuiException;
import com.cn.comfyui.service.WorkflowResultService;
import com.cn.comfyui.vo.WorkflowResultVo;
import com.cn.common.base.BasePage;
import com.cn.common.entity.WorkflowResult;
import com.cn.common.mapper.WorkflowResultMapper;
import com.cn.common.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 作品服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowResultServiceImpl implements WorkflowResultService {

    private final WorkflowResultMapper worksMapper;

    @Override
    public WorkflowResultVo getWorkflowResultDetail(final Long workflowResultId) {
        Long currentUserId = UserUtils.getCurrentLoginId();

        // 查询作品基本信息
        LambdaQueryWrapper<WorkflowResult> worksQuery = new LambdaQueryWrapper<WorkflowResult>()
                .eq(WorkflowResult::getId, workflowResultId)
                .eq(WorkflowResult::getUserId, currentUserId);

        WorkflowResult works = worksMapper.selectOne(worksQuery);
        if (works == null) {
            throw new ComfyuiException("作品不存在或无访问权限");
        }

        // 构建并返回作品详情
        return new WorkflowResultVo()
                .setWorkflowResultId(workflowResultId)
                .setCreateTime(works.getCreateTime())
                .setType(works.getType())
                .setWorkflowName(works.getWorkflowName())
                .setTaskId(works.getTaskId())
                .setUrl(works.getUrl())
                .setWorkflowId(works.getWorkflowId())
                .setFormParams(works.getFormParams());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWorkflowResult(final WorkflowResultDto dto) {
        Long workflowResultId = dto.getWorkflowResultId();
        Long currentUserId = UserUtils.getCurrentLoginId();

        // 验证权限
        long count = worksMapper.selectCount(
                new LambdaQueryWrapper<WorkflowResult>()
                        .eq(WorkflowResult::getId, workflowResultId)
                        .eq(WorkflowResult::getUserId, currentUserId));

        if (count <= 0) {
            throw new ComfyuiException("作品不存在");
        }

        // 删除作品相关数据
        worksMapper.delete(
                new LambdaQueryWrapper<WorkflowResult>()
                        .eq(WorkflowResult::getId, workflowResultId));

    }

    @Override
    public BasePage<WorkflowResultVo> getWorkflowResultPage(final Long pageNum) {
        Long currentUserId = UserUtils.getCurrentLoginId();

        // 设置分页参数，每页显示20条记录
        Page<WorkflowResult> page = new Page<>(pageNum, 20L);

        // 构建查询条件：按用户ID查询，按创建时间倒序排列
        LambdaQueryWrapper<WorkflowResult> queryWrapper = new LambdaQueryWrapper<WorkflowResult>()
                .eq(WorkflowResult::getUserId, currentUserId)
                .orderByDesc(WorkflowResult::getCreateTime);

        // 执行分页查询
        Page<WorkflowResult> worksPage = worksMapper.selectPage(page, queryWrapper);

        IPage<WorkflowResultVo> convert = worksPage.convert(c -> new WorkflowResultVo()
                .setCreateTime(c.getCreateTime())
                .setWorkflowResultId(c.getId())
                .setTaskId(c.getTaskId())
                .setWorkflowName(c.getWorkflowName())
                .setType(c.getType())
                .setUrl(c.getUrl())
                .setWorkflowId(c.getWorkflowId())
                .setFormParams(c.getFormParams()));

        // 构建返回结果
        return new BasePage<WorkflowResultVo>()
                .setItems(convert.getRecords())
                .setTotal(worksPage.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteWorkflowResult(final BatchDeleteWorkflowResultDto dto) {
        List<Long> workflowResultIds = dto.getWorkflowResultIds();
        Long currentUserId = UserUtils.getCurrentLoginId();

        if (workflowResultIds == null || workflowResultIds.isEmpty()) {
            throw new ComfyuiException("作品ID列表不能为空");
        }

        // 验证所有作品是否属于当前用户
        long count = worksMapper.selectCount(
                new LambdaQueryWrapper<WorkflowResult>()
                        .in(WorkflowResult::getId, workflowResultIds)
                        .eq(WorkflowResult::getUserId, currentUserId));

        if (count != workflowResultIds.size()) {
            throw new ComfyuiException("部分作品不存在或无权限删除");
        }

        // 批量删除作品
        worksMapper.delete(
                new LambdaQueryWrapper<WorkflowResult>()
                        .in(WorkflowResult::getId, workflowResultIds)
                        .eq(WorkflowResult::getUserId, currentUserId));

        log.info("用户 {} 批量删除了 {} 个作品", currentUserId, workflowResultIds.size());
    }


}
