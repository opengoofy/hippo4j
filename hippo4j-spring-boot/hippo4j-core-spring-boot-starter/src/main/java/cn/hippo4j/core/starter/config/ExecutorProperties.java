package cn.hippo4j.core.starter.config;

import cn.hippo4j.common.notify.ThreadPoolNotifyAlarm;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.Objects;

/**
 * Executor properties.
 *
 * @author chen.ma
 * @date 2022/2/25 00:40
 */
@Data
@Accessors(chain = true)
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

    public Map<String, String> receives() {
        return Objects.isNull(this.notify) || this.notify.getReceives() == null ? Maps.newHashMap() : this.notify.getReceives();
    }

}
