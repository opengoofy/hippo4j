package cn.hippo4j.config.model.biz.threadpool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Thread pool query req dto.
 *
 * @author chen.ma
 * @date 2021/6/30 21:22
 */
@Data
public class ThreadPoolQueryReqDTO extends Page {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String itemId;

    /**
     * tpId
     */
    private String tpId;

}
