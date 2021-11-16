package cn.hippo4j.starter.alarm;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 报警控制组件.
 *
 * @author chen.ma
 * @date 2021/10/28 21:24
 */
public class AlarmControlHandler {

    private final Cache<String, String> cache;

    public AlarmControlHandler(long alarmInterval) {
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(alarmInterval, TimeUnit.MINUTES)
                .build();
    }

    /**
     * 控制消息推送报警频率.
     *
     * @param alarmControl
     * @return
     */
    public boolean isSend(AlarmControlDTO alarmControl) {
        String pkId = cache.getIfPresent(alarmControl.buildPk());

        if (StrUtil.isBlank(pkId)) {
            // val 无意义
            cache.put(alarmControl.buildPk(), IdUtil.simpleUUID());
            return true;
        }

        return false;
    }

}
