package cn.hippo4j.common.notify;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Thread pool notify alarm.
 *
 * @author chen.ma
 * @date 2021/8/15 13:13
 */
@Data
@AllArgsConstructor
public class ThreadPoolNotifyAlarm {

    /**
     * isAlarm
     */
    private Boolean isAlarm;

    /**
     * livenessAlarm
     */
    private Integer livenessAlarm;

    /**
     * capacityAlarm
     */
    private Integer capacityAlarm;

}
