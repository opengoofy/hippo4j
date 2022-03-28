package cn.hippo4j.common.notify.request;

import cn.hippo4j.common.notify.NotifyTypeEnum;
import cn.hippo4j.common.notify.request.base.BaseNotifyRequest;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Alarm notify request.
 *
 * @author chen.ma
 * @date 2022/2/22 19:41
 */
@Data
@Accessors(chain = true)
public class AlarmNotifyRequest extends BaseNotifyRequest {

    /**
     * interval
     */
    private Integer interval;

    /**
     * notifyTypeEnum
     */
    private NotifyTypeEnum notifyTypeEnum;

    /**
     * active
     */
    private String active;

    /**
     * appName
     */
    private String appName;

    /**
     * identify
     */
    private String identify;

    /**
     * corePoolSize
     */
    private Integer corePoolSize;

    /**
     * maximumPoolSize
     */
    private Integer maximumPoolSize;

    /**
     * poolSize
     */
    private Integer poolSize;

    /**
     * activeCount
     */
    private Integer activeCount;

    /**
     * largestPoolSize
     */
    private Integer largestPoolSize;

    /**
     * completedTaskCount
     */
    private Long completedTaskCount;

    /**
     * queueName
     */
    private String queueName;

    /**
     * capacity
     */
    private Integer capacity;

    /**
     * queueSize
     */
    private Integer queueSize;

    /**
     * remainingCapacity
     */
    private Integer remainingCapacity;

    /**
     * rejectedExecutionHandlerName
     */
    private String rejectedExecutionHandlerName;

    /**
     * rejectCountNum
     */
    private Long rejectCountNum;

    /**
     * executeTime
     */
    private Long executeTime;

    /**
     * executeTimeOut
     */
    private Long executeTimeOut;

    /**
     * executeTimeoutTrace
     */
    private String executeTimeoutTrace;

}
