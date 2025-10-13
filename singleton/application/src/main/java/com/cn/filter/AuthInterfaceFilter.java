package com.cn.filter;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.cn.common.constant.UserConstant;
import com.cn.common.structure.UserInfoStructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 权限拦截处理类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterfaceFilter implements StpInterface {


    /**
     * 获取当前用户拥有的权限
     *
     * @return the role list
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        try {
            UserInfoStructure structure = (UserInfoStructure) StpUtil.getSessionByLoginId(loginId).get(UserConstant.USER_INFO);
            return Collections.singletonList(structure.getRole());
        } catch (Exception e) {
            log.error("获取用户权限类型失败 信息:{}", e.getMessage());
            throw new RuntimeException();
        }
    }

    /**
     * 获取当前用户拥有的权限
     *
     * @param o the o
     * @param s the s
     * @return the permission list
     */
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }


}
