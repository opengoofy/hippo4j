package cn.hippo4j.core.starter.config;

import cn.hippo4j.common.notify.ThreadPoolNotifyAlarm;
import lombok.Data;

/**
 * Executor properties.
 *
 * @author chen.ma
 * @date 2022/2/25 00:40
 */
@Data
public class ExecutorProperties {

    /**
     * threadPoolId
     */
    private String threadPoolId;

    /**
     * corePoolSize
     */
    private Integer corePoolSize;

    /**
     * maximumPoolSize
     */
    private Integer maximumPoolSize;

    /**
     * queueCapacity
     */
    private Integer queueCapacity;

    /**
     * blockingQueue
     */
    private String blockingQueue;

    /**
     * rejectedHandler
     */
    private String rejectedHandler;

    /**
     * keepAliveTime
     */
    private Long keepAliveTime;

    /**
     * executeTimeOut
     */
    private Long executeTimeOut;

    /**
     * allowCoreThreadTimeOut
     */
    private Boolean allowCoreThreadTimeOut;

    /**
     * threadNamePrefix
     */
    private String threadNamePrefix;

    /**
     * Notify
     */
    private ThreadPoolNotifyAlarm notify;

}
