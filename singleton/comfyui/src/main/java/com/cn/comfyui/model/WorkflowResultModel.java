package com.cn.comfyui.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 工作流结果模型
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class WorkflowResultModel {

    private String url;

    private String type;

    private Long workflowResultId;

}
