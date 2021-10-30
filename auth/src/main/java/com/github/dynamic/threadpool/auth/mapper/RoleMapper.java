package com.github.dynamic.threadpool.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dynamic.threadpool.auth.model.RoleInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Role mapper.
 *
 * @author chen.ma
 * @date 2021/10/30 22:55
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleInfo> {
}
