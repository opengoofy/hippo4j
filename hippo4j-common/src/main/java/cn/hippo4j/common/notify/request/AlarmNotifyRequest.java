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

    private String active;

    private String appName;

    private String identify;

    private Integer corePoolSize;

    private Integer maximumPoolSize;

    private Integer poolSize;

    private Integer activeCount;

    private Integer largestPoolSize;

    private Long completedTaskCount;

    private String queueName;

    private Integer capacity;

    private Integer queueSize;

    private Integer remainingCapacity;

    private String rejectedExecutionHandlerName;

    private Long rejectCountNum;

}
