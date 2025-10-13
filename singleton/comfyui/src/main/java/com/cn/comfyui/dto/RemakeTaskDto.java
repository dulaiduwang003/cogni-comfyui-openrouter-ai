package com.cn.comfyui.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 重制任务DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class RemakeTaskDto {

    @NotBlank(message = "任务ID不能为空")
    private String taskId;
}
