package cn.hippo4j.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.hippo4j.auth.model.PermissionInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Permission mapper.
 *
 * @author chen.ma
 * @date 2021/10/30 22:34
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionInfo> {
}
