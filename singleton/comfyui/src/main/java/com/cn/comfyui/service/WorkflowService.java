package com.cn.comfyui.service;

import com.cn.comfyui.dto.CancelTaskDto;
import com.cn.comfyui.dto.RemakeTaskDto;
import com.cn.comfyui.dto.SubmitTaskDto;
import com.cn.comfyui.vo.TaskProgressVo;
import com.cn.comfyui.vo.WorkflowCategoryVo;
import com.cn.comfyui.vo.WorkflowInterfaceVo;
import com.cn.comfyui.vo.WorkflowsVo;
import com.cn.common.vo.PageVo;

import java.util.List;

/**
 * 工作流服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface WorkflowService {

    String submitTask(final SubmitTaskDto dto);

    TaskProgressVo getTaskProgress(final String taskId);

    List<TaskProgressVo> getTaskProgressList();

    PageVo<TaskProgressVo> getTaskProgressPage(final Long page, final String status);

    WorkflowInterfaceVo getWorkflowInterface(final Long workflowId);

    PageVo<WorkflowsVo> getWorkflowsPage(final String prompt, final Long categoryId, final Long page);

    void cancelTask(final CancelTaskDto dto);

    String remakeTask(final RemakeTaskDto dto);

    List<WorkflowCategoryVo> getWorkflowCategoryList();

}
