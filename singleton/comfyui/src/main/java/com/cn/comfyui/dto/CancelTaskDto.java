package com.cn.comfyui.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 取消任务DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class CancelTaskDto {

    @NotEmpty(message = "任务ID不能为空")
    private String taskId;

}
