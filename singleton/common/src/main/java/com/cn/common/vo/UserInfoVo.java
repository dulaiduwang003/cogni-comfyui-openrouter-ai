package com.cn.common.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author 明明不是下雨天 github@dulaiduwang003 2024/9/10 下午9:14
 */
@Data
@Accessors(chain = true)
public class UserInfoVo implements Serializable {



    private String nickname;

    private String avatar;

    private List<String> roles;
}
