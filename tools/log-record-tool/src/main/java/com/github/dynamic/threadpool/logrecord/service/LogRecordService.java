package com.github.dynamic.threadpool.logrecord.service;

import com.github.dynamic.threadpool.logrecord.model.LogRecordInfo;

/**
 * 日志记录.
 *
 * @author chen.ma
 * @date 2021/10/23 22:43
 */
public interface LogRecordService {

    /**
     * 保存日志.
     *
     * @param logRecordInfo
     */
    void record(LogRecordInfo logRecordInfo);

}
