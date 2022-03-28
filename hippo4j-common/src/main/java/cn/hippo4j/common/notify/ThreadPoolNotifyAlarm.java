package cn.hippo4j.common.notify;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Thread pool notify alarm.
 *
 * @author chen.ma
 * @date 2021/8/15 19:13
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ThreadPoolNotifyAlarm {

    /**
     * isAlarm
     */
    @NonNull
    private Boolean isAlarm;

    /**
     * activeAlarm
     */
    @NonNull
    private Integer activeAlarm;

    /**
     * capacityAlarm
     */
    @NonNull
    private Integer capacityAlarm;

    /**
     * interval
     */
    private Integer interval;

    /**
     * receive
     */
    private String receive;

    /**
     * receives
     * ps：暂不启用该配置，后续如果开发邮箱时或许有用
     */
    @Deprecated
    private Map<String, String> receives;

}
