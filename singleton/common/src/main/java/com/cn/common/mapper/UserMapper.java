package com.cn.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cn.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * The interface User mapper.
 *
 * @author bdth github@dulaiduwang003
 * @version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
