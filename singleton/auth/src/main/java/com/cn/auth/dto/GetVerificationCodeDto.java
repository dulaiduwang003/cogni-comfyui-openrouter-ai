package com.cn.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 获取验证码DTO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class GetVerificationCodeDto {

    @NotEmpty(message = "邮箱不能为空")
    private String email;


}
