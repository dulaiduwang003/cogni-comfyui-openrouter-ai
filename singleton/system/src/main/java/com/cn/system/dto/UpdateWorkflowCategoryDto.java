package com.cn.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新工作流类别DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class UpdateWorkflowCategoryDto {

    @NotNull(message = "categoryId 不能为空")
    private Long categoryId;

    @NotBlank(message = "name 不能为空")
    private String name;
}


