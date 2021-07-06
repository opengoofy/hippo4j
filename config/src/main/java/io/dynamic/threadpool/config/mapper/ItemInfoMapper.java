package io.dynamic.threadpool.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.dynamic.threadpool.config.model.ItemInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Item Info Mapper.
 *
 * @author chen.ma
 * @date 2021/6/29 21:53
 */
@Mapper
public interface ItemInfoMapper extends BaseMapper<ItemInfo> {
}
