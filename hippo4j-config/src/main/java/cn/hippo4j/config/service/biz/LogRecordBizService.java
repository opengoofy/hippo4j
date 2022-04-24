package cn.hippo4j.config.service.biz;

import cn.hippo4j.config.model.biz.log.LogRecordQueryReqDTO;
import cn.hippo4j.config.model.biz.log.LogRecordRespDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 操作日志.
 *
 * @author chen.ma
 * @date 2021/11/17 21:41
 */
public interface LogRecordBizService {

    /**
     * 查询操作日志.
     *
     * @param pageQuery
     * @return
     */
    IPage<LogRecordRespDTO> queryPage(LogRecordQueryReqDTO pageQuery);

}
