package cn.hippo4j.config.mapper;

import cn.hippo4j.config.model.ConfigAllInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Config info mapper.
 *
 * @author chen.ma
 * @date 2021/6/29 22:44
 */
@Mapper
public interface ConfigInfoMapper extends BaseMapper<ConfigAllInfo> {
}
