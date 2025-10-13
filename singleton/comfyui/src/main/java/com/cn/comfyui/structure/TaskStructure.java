package com.cn.comfyui.structure;

import com.cn.comfyui.model.TaskNodeContainer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 任务结构
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class TaskStructure implements Serializable {

    private Long userId;

    private Long workflowId;

    private String taskId;

    private List<TaskNodeContainer> taskNodeContainer;

}
