package com.cn.system.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cn.common.entity.User;
import com.cn.common.enums.RoleEnum;
import com.cn.common.mapper.UserMapper;
import com.cn.common.vo.PageVo;
import com.cn.system.dto.*;
import com.cn.system.service.SystemUserService;
import com.cn.system.vo.SystemUserVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统用户服务实现类
 *
 * @author 时间海 @github dulaiduwang003
 * @email 2074055628@qq.com
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SystemUserServiceImpl implements SystemUserService {

    private final UserMapper userMapper;

    @Override
    public PageVo<SystemUserVo> page(Integer page, Integer size, String keyword, String role) {
        Page<User> mpPage = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.like(User::getEmail, keyword)
                   .or().like(User::getNickname, keyword);
        }
        if (StringUtils.isNotBlank(role)) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getId);
        IPage<User> result = userMapper.selectPage(mpPage, wrapper);
        List<SystemUserVo> items = result.getRecords().stream().map(this::toVo).collect(Collectors.toList());
        return new PageVo<SystemUserVo>().setTotal(result.getTotal()).setItems(items);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(CreateUserDto dto) {
        // 校验邮箱是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, dto.getEmail());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new IllegalArgumentException("该邮箱已被注册");
        }
        
        User user = new User()
                .setEmail(dto.getEmail())
                .setPassword(SaSecureUtil.md5(dto.getPassword()))
                .setNickname(StringUtils.defaultIfBlank(dto.getNickname(), dto.getEmail()))
                .setAvatar(dto.getAvatar())
                .setRole(StringUtils.defaultIfBlank(dto.getRole(), RoleEnum.USER.getDesc()));
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateUserDto dto) {
        User user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        if (StringUtils.isNotBlank(dto.getEmail())) user.setEmail(dto.getEmail());
        if (StringUtils.isNotBlank(dto.getNickname())) user.setNickname(dto.getNickname());
        if (StringUtils.isNotBlank(dto.getAvatar())) user.setAvatar(dto.getAvatar());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(DeleteUserDto dto) {
        userMapper.deleteById(dto.getId());
    }

    private SystemUserVo toVo(User u) {
        return new SystemUserVo()
                .setId(u.getId())
                .setEmail(u.getEmail())
                .setNickname(u.getNickname())
                .setAvatar(u.getAvatar())
                .setRole(u.getRole())
                .setCreateTime(u.getCreateTime())
                .setUpdateTime(u.getUpdateTime());
    }
} 