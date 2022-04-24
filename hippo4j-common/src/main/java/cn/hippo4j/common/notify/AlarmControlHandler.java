package cn.hippo4j.common.notify;

import cn.hippo4j.common.constant.Constants;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Alarm control assembly.
 *
 * @author chen.ma
 * @date 2021/10/28 21:24
 */
public class AlarmControlHandler {

    private final Map<String, ReentrantLock> threadPoolLock = Maps.newHashMap();

    private final Map<String, Cache<String, String>> threadPoolAlarmCache = Maps.newConcurrentMap();

    /**
     * Control message push alarm frequency.
     *
     * @param alarmControl
     * @return
     */
    public boolean isSendAlarm(AlarmControlDTO alarmControl) {
        String threadPoolKey = alarmControl.buildPk();
        Cache<String, String> cache = threadPoolAlarmCache.get(threadPoolKey);
        if (cache == null) {
            return false;
        }

        String pkId = cache.getIfPresent(alarmControl.getTypeEnum().name());
        if (StrUtil.isBlank(pkId)) {
            ReentrantLock lock = threadPoolLock.get(threadPoolKey);
            lock.lock();
            try {
                pkId = cache.getIfPresent(alarmControl.getTypeEnum().name());
                if (StrUtil.isBlank(pkId)) {
                    // Val meaningless.
                    cache.put(alarmControl.getTypeEnum().name(), IdUtil.simpleUUID());
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }

        return false;
    }

    /**
     * Init cache and lock.
     *
     * @param threadPoolId
     * @param platform
     * @param interval
     */
    public void initCacheAndLock(String threadPoolId, String platform, Integer interval) {
        String threadPoolKey = StrUtil.builder(threadPoolId, Constants.GROUP_KEY_DELIMITER, platform).toString();
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .expireAfterWrite(interval, TimeUnit.MINUTES)
                .build();
        threadPoolAlarmCache.put(threadPoolKey, cache);

        // Set the lock to prevent false sending of alarm information.
        ReentrantLock reentrantLock = new ReentrantLock();
        threadPoolLock.put(threadPoolKey, reentrantLock);
    }

}
