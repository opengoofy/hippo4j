package com.github.dynamic.threadpool.config.service.biz.impl;

import com.github.dynamic.threadpool.config.mapper.LogRecordMapper;
import com.github.dynamic.threadpool.logrecord.model.LogRecordInfo;
import com.github.dynamic.threadpool.logrecord.service.LogRecordService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 操作日志保存数据库.
 *
 * @author chen.ma
 * @date 2021/10/24 20:57
 */
@Service
@AllArgsConstructor
public class LogRecordServiceImpl implements LogRecordService {

    private final LogRecordMapper logRecordMapper;

    @Override
    public void record(LogRecordInfo logRecordInfo) {
        logRecordMapper.insert(logRecordInfo);
    }

}
