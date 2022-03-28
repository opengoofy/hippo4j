package cn.hippo4j.common.notify.request;

import cn.hippo4j.common.notify.request.base.BaseNotifyRequest;
import lombok.Data;

/**
 * Change parameter notify request.
 *
 * @author chen.ma
 * @date 2022/2/22 20:22
 */
@Data
public class ChangeParameterNotifyRequest extends BaseNotifyRequest {

    private String active;

    private String appName;

    private String identify;

    private Integer beforeCorePoolSize;

    private Integer nowCorePoolSize;

    private Integer beforeMaximumPoolSize;

    private Integer nowMaximumPoolSize;

    private Boolean beforeAllowsCoreThreadTimeOut;

    private Boolean nowAllowsCoreThreadTimeOut;

    private Long beforeKeepAliveTime;

    private Long nowKeepAliveTime;

    private Long beforeExecuteTimeOut;

    private Long nowExecuteTimeOut;

    private String blockingQueueName;

    private Integer beforeQueueCapacity;

    private Integer nowQueueCapacity;

    private String beforeRejectedName;

    private String nowRejectedName;

}
