package com.cn.llm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 删除会话DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
public class DeleteSessionDto {
    @NotBlank(message = "sessionId不能为空")
    private String sessionId;
} 