package cn.hippo4j.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.hippo4j.config.model.ItemInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Item info mapper.
 *
 * @author chen.ma
 * @date 2021/6/29 21:53
 */
@Mapper
public interface ItemInfoMapper extends BaseMapper<ItemInfo> {
}
