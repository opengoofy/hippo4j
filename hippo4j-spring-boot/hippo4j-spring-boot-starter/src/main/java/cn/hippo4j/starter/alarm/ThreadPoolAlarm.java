package cn.hippo4j.starter.alarm;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Thread pool alarm.
 *
 * @author chen.ma
 * @date 2021/8/15 13:13
 */
@Data
@AllArgsConstructor
public class ThreadPoolAlarm {

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
