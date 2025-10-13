package com.cn.comfyui.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工作流结果VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class WorkflowResultVo implements Serializable {

    private Long workflowResultId;

    private String type;

    private String url;

    private String workflowName;

    private String taskId;

    private LocalDateTime createTime;
}
