package com.cn.comfyui.structure;

import com.cn.comfyui.model.TaskNodeContainer;
import com.cn.comfyui.model.WorkflowResultModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务信息结构
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class TaskInfoStructure implements Serializable {

    private String taskId;

    private String workflowName;

    private WorkflowResultModel workflowResultModel;

    private String status;

    private Long progress;

    private Long location;

    private Form form;

    private LocalDateTime createTime;

    /**
     * 任务消耗的积分数量
     */
    private Long creditsDeducted;

    @Data
    @Accessors(chain = true)
    public static class Form{

        private Long workflowId;

        private List<TaskNodeContainer> taskNodeContainer;

    }


}
