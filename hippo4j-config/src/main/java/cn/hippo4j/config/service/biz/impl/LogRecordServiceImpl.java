package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.config.mapper.LogRecordMapper;
import cn.hippo4j.tools.logrecord.model.LogRecordInfo;
import cn.hippo4j.tools.logrecord.service.LogRecordService;
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
