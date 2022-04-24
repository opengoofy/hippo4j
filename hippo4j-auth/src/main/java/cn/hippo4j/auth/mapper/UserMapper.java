package cn.hippo4j.auth.mapper;

import cn.hippo4j.auth.model.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
