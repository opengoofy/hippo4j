package com.github.dynamic.threadpool.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.dynamic.threadpool.logrecord.model.LogRecordInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Log record mapper.
 *
 * @author chen.ma
 * @date 2021/10/24 21:01
 */
@Mapper
public interface LogRecordMapper extends BaseMapper<LogRecordInfo> {
}
