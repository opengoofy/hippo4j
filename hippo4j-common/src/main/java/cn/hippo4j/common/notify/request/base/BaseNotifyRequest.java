package cn.hippo4j.common.notify.request.base;

import lombok.Data;

/**
 * Base notify request.
 *
 * @author chen.ma
 * @date 2022/2/22 19:35
 */
@Data
public class BaseNotifyRequest implements NotifyRequest {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String itemId;

    /**
     * threadPoolId
     */
    private String threadPoolId;

    /**
     * platform
     */
    private String platform;

    /**
     * type
     */
    private String type;

    /**
     * receives
     */
    private String receives;

}
