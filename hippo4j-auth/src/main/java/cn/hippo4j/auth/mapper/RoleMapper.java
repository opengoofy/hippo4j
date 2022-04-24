package cn.hippo4j.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.hippo4j.auth.model.RoleInfo;
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
