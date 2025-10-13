package com.cn.comfyui.service;

import com.cn.common.base.BasePage;
import com.cn.comfyui.dto.WorkflowResultDto;
import com.cn.comfyui.vo.WorkflowResultVo;

/**
 * 工作流结果服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface WorkflowResultService {

    BasePage<WorkflowResultVo> getWorkflowResultPage(final Long page);

    WorkflowResultVo getWorkflowResultDetail(final Long workflowResultId);

    void deleteWorkflowResult(final WorkflowResultDto dto);

}
