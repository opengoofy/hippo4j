package cn.hippo4j.starter.alarm;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 报警控制组件.
 *
 * @author chen.ma
 * @date 2021/10/28 21:24
 */
public class AlarmControlHandler {

    public static Map<String, Cache<String, String>> THREAD_POOL_ALARM_CACHE = Maps.newConcurrentMap();

    /**
     * 控制消息推送报警频率.
     *
     * @param alarmControl
     * @return
     */
    public boolean isSendAlarm(AlarmControlDTO alarmControl) {
        Cache<String, String> cache = THREAD_POOL_ALARM_CACHE.get(alarmControl.buildPk());
        if (cache != null) {
            String pkId = cache.getIfPresent(alarmControl.getTypeEnum().name());
            if (StrUtil.isBlank(pkId)) {
                // val 无意义
                cache.put(alarmControl.getTypeEnum().name(), IdUtil.simpleUUID());
                return true;
            }
        }

        return false;
    }

}
