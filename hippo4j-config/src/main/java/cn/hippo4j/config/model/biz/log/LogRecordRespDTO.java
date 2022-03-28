package cn.hippo4j.config.model.biz.log;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 日志记录返回.
 *
 * @author chen.ma
 * @date 2021/11/17 21:37
 */
@Data
public class LogRecordRespDTO {

    /**
     * 业务标识
     */
    private String bizNo;

    /**
     * 日志内容
     */
    private String action;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 业务类型
     */
    private String category;

    /**
     * gmtCreate
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}
