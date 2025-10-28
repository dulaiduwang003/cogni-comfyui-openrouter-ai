package com.cn.comfyui.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 批量删除作品DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class BatchDeleteWorkflowResultDto {

    @NotNull(message = "作品ID列表不能为空")
    @Size(min = 1, max = 50, message = "一次最多删除50个作品")
    private List<Long> workflowResultIds;

}

