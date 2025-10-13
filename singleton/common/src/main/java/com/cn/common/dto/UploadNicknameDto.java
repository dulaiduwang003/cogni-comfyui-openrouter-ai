package com.cn.common.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 明明不是下雨天 github@dulaiduwang003 2024/9/10 下午11:06
 */
@Data
@Accessors(chain = true)
public class UploadNicknameDto implements Serializable {

    @NotEmpty(message = "用户昵称不能为空")
    private String nickname;
}
