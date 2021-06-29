package io.dynamic.threadpool.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dynamic.threadpool.server.model.TenantInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Tenant Info Mapper.
 *
 * @author chen.ma
 * @date 2021/6/29 22:44
 */
@Mapper
public interface TenantInfoMapper extends BaseMapper<TenantInfo> {
}
