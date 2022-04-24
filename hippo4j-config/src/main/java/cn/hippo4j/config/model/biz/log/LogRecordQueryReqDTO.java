package cn.hippo4j.config.model.biz.log;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 日志记录查询.
 *
 * @author chen.ma
 * @date 2021/11/17 21:43
 */
@Data
public class LogRecordQueryReqDTO extends Page {

    /**
     * 业务标识
     */
    private String bizNo;

    /**
     * 业务类型
     */
    private String category;

    /**
     * 操作人
     */
    private String operator;

}
