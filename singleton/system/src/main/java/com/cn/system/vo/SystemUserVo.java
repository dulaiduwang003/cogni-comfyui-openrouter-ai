package com.cn.system.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 系统用户VO
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Data
@Accessors(chain = true)
public class SystemUserVo {

    private Long id;

    private String email;

    private String nickname;

    private String avatar;

    private String role;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
} 