package com.cn.common.initializer;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cn.common.configuration.AdminInitConfiguration;
import com.cn.common.entity.User;
import com.cn.common.entity.UserCredits;
import com.cn.common.enums.RoleEnum;
import com.cn.common.mapper.UserCreditsMapper;
import com.cn.common.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 管理员账号初始化器
 * 项目启动时自动创建管理员账号
 *
 * @author bdth github@dulaiduwang003
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements ApplicationRunner {

    private final AdminInitConfiguration adminConfig;
    private final UserMapper userMapper;
    private final UserCreditsMapper userCreditsMapper;

    @Override
    public void run(ApplicationArguments args) {
        // 检查是否启用管理员初始化
        if (!Boolean.TRUE.equals(adminConfig.getEnabled())) {
            log.info("管理员账号初始化功能已禁用");
            return;
        }

        // 验证配置
        if (!StringUtils.hasText(adminConfig.getEmail()) || !StringUtils.hasText(adminConfig.getPassword())) {
            log.warn("管理员邮箱或密码未配置，跳过管理员账号初始化");
            return;
        }

        try {
            // 检查邮箱是否已存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, adminConfig.getEmail());
            User existingUser = userMapper.selectOne(queryWrapper);

            if (existingUser != null) {
                log.info("管理员账号已存在，邮箱: {}", adminConfig.getEmail());
                return;
            }

            // 创建管理员账号
            User admin = new User();
            admin.setEmail(adminConfig.getEmail());
            admin.setPassword(SaSecureUtil.md5(adminConfig.getPassword()));
            admin.setNickname(adminConfig.getNickname());
            admin.setRole(RoleEnum.ADMIN.name());


            int insertResult = userMapper.insert(admin);

            if (insertResult > 0) {
                log.info("管理员账号创建成功！");
                log.info("邮箱: {}", adminConfig.getEmail());
                log.info("昵称: {}", adminConfig.getNickname());
                log.info("角色: ADMIN");

                // 初始化管理员积分
                UserCredits credits = new UserCredits();
                credits.setUserId(admin.getId());
                credits.setTotalCredits(adminConfig.getInitialCredits());
                credits.setAvailableCredits(adminConfig.getInitialCredits());
                credits.setFrozenCredits(0L);

                int creditsResult = userCreditsMapper.insert(credits);

                if (creditsResult > 0) {
                    log.info("初始积分: {}", adminConfig.getInitialCredits());
                    log.info("管理员账号初始化完成！");
                } else {
                    log.error("管理员积分初始化失败");
                }
            } else {
                log.error("管理员账号创建失败");
            }

        } catch (Exception e) {
            log.error("管理员账号初始化过程中发生异常: {}", e.getMessage(), e);
        }
    }
}

