package com.cn.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新工作流DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class UpdateWorkflowDto {

    @NotNull(message = "workflowId 不能为空")
    private Long workflowId;

    @NotBlank(message = "name 不能为空")
    private String name;

    @NotNull(message = "workflowCategoryId 不能为空")
    private Long workflowCategoryId;
}


