package cn.hippo4j.tools.logrecord.model;

import lombok.Builder;
import lombok.Data;

/**
 * 日志操作记录.
 *
 * @author chen.ma
 * @date 2021/10/24 21:07
 */
@Data
@Builder
public class LogRecordOps {

    private String successLogTemplate;

    private String failLogTemplate;

    private String operatorId;

    private String bizKey;

    private String bizNo;

    private String category;

    private String detail;

    private String condition;

}
