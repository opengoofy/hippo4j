package cn.hippo4j.tools.logrecord.service.impl;

import cn.hippo4j.tools.logrecord.model.LogRecordInfo;
import cn.hippo4j.tools.logrecord.service.LogRecordService;
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
