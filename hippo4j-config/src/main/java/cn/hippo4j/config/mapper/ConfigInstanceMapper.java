package cn.hippo4j.config.mapper;

import cn.hippo4j.config.model.ConfigInstanceInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Config instance mapper.
 *
 * @author chen.ma
 * @date 2021/12/5 19:18
 */
@Mapper
public interface ConfigInstanceMapper extends BaseMapper<ConfigInstanceInfo> {
}
