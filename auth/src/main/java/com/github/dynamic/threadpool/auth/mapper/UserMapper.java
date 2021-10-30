package com.github.dynamic.threadpool.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dynamic.threadpool.auth.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * User mapper.
 *
 * @author chen.ma
 * @date 2021/10/30 21:42
 */
@Mapper
public interface UserMapper extends BaseMapper<UserInfo> {
}
