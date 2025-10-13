package com.cn.system.service;

import com.cn.common.vo.PageVo;
import com.cn.system.dto.*;
import com.cn.system.vo.SystemUserVo;

/**
 * 系统用户服务接口
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
public interface SystemUserService {

    PageVo<SystemUserVo> page(Integer page, Integer size, String keyword, String role);

    Long create(CreateUserDto dto);

    void update(UpdateUserDto dto);

    void delete(DeleteUserDto dto);
} 