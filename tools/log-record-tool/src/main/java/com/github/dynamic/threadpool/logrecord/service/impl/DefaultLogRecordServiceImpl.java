package com.github.dynamic.threadpool.logrecord.service.impl;

import com.github.dynamic.threadpool.logrecord.model.LogRecordInfo;
import com.github.dynamic.threadpool.logrecord.service.LogRecordService;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认实现日志存储.
 *
 * @author chen.ma
 * @date 2021/10/24 17:59
 */
@Slf4j
public class DefaultLogRecordServiceImpl implements LogRecordService {

    @Override
    public void record(LogRecordInfo logRecordInfo) {
        log.info("Log print :: {}", logRecordInfo);
    }

}
