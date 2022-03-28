package cn.hippo4j.config.model.biz.monitor;

import lombok.Data;

/**
 * Monitor query req dto.
 *
 * @author chen.ma
 * @date 2021/12/10 20:18
 */
@Data
public class MonitorQueryReqDTO {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 项目id
     */
    private String itemId;

    /**
     * 线程池id
     */
    private String tpId;

    /**
     * 实例id
     */
    private String instanceId;

}
