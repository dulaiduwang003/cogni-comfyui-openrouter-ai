package com.cn.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 删除工作流类别DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class DeleteWorkflowCategoryDto {

    @NotNull(message = "categoryId 不能为空")
    private Long categoryId;
}


