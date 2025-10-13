package com.cn.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 管理员初始化配置属性
 *
 * @author bdth github@dulaiduwang003
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "admin.init")
public class AdminInitConfiguration {

    /**
     * 是否启用管理员初始化
     */
    private Boolean enabled = true;

    /**
     * 管理员邮箱
     */
    private String email;

    /**
     * 管理员密码
     */
    private String password;

    /**
     * 管理员昵称
     */
    private String nickname = "系统管理员";

    /**
     * 初始积分
     */
    private Long initialCredits = 1000000L;
}

