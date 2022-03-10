package cn.hippo4j.core.executor.manage;

import cn.hippo4j.common.notify.ThreadPoolNotifyAlarm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global notify alarm manage.
 *
 * @author chen.ma
 * @date 2022/2/24 20:12
 */
public class GlobalNotifyAlarmManage {

    /**
     * Notify alarm map.
     */
    private static final Map<String, ThreadPoolNotifyAlarm> NOTIFY_ALARM_MAP = new ConcurrentHashMap();

    /**
     * Get.
     *
     * @param key
     * @return
     */
    public static ThreadPoolNotifyAlarm get(String key) {
        return NOTIFY_ALARM_MAP.get(key);
    }

    /**
     * Put.
     *
     * @param key
     * @param val
     */
    public static void put(String key, ThreadPoolNotifyAlarm val) {
        NOTIFY_ALARM_MAP.put(key, val);
    }

}
