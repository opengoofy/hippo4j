package cn.hippo4j.common.model.register.notify;

import lombok.*;

/**
 * Dynamic thread-pool register core notify parameter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicThreadPoolRegisterCoreNotifyParameter {

    /**
     * Whether to enable thread pool running alarm
     */
    @NonNull
    private Boolean alarm;

    /**
     * Active alarm
     */
    @NonNull
    private Integer activeAlarm;

    /**
     * Capacity alarm
     */
    @NonNull
    private Integer capacityAlarm;

    /**
     * Interval
     */
    private Integer interval;

    /**
     * Receive
     */
    private String receives;
}
