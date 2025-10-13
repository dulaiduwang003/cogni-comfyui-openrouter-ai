package com.cn.comfyui.vo;

import com.cn.comfyui.model.WorkflowResultModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务进度VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class TaskProgressVo implements Serializable {

    private String taskId;

    private String workflowName;

    private Long progress;

    private WorkflowResultModel workflowResultModel;

    private String status;

    private Long location;

    private LocalDateTime createTime;

    /**
     * 任务消耗的积分数量
     */
    private Long creditsDeducted;

}
