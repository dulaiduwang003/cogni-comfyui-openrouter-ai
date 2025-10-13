package com.cn.comfyui.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 作品结果DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class WorkflowResultDto {


    @NotNull(message = "作品ID不能为空")
    private Long workflowResultId;

}
