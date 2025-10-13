package com.cn.comfyui.dto;

import com.cn.comfyui.model.TaskNodeContainer;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 提交任务DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class SubmitTaskDto {

    @NotNull(message = "工作流ID不能为空")
    private Long workflowId;

    @NotEmpty(message = "操作节点不能为空")
    private List<TaskNodeContainer> nodeContainer;


}
