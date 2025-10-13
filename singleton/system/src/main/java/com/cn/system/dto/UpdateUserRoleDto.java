package com.cn.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 更新用户角色DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class UpdateUserRoleDto {

    @NotNull(message = "用户ID不能为空")
    private Long id;

    @NotBlank(message = "角色不能为空")
    private String role;
} 