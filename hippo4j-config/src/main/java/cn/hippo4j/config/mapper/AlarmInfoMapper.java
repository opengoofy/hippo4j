package cn.hippo4j.config.mapper;

import cn.hippo4j.config.model.AlarmInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * Alarm info mapper.
 *
 * @author chen.ma
 * @date 2021/11/17 22:04
 */
@Mapper
public interface AlarmInfoMapper extends BaseMapper<AlarmInfo> {
}
