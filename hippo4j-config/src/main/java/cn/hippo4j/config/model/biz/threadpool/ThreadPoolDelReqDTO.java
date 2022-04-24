package cn.hippo4j.config.model.biz.threadpool;

import lombok.Data;

/**
 * ThreadPool del req dto.
 *
 * @author chen.ma
 * @date 2021/11/11 21:40
 */
@Data
public class ThreadPoolDelReqDTO {

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
