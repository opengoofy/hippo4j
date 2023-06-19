/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.threadpool.message.core.service;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.message.api.AlarmControlDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Alarm control assembly.
 */
public class AlarmControlHandler {

    private final Map<String, ReentrantLock> threadPoolLock = new HashMap<>();

    private final Map<String, Cache<String, String>> threadPoolAlarmCache = new ConcurrentHashMap<>();

    /**
     * Control message push alarm frequency.
     *
     * @param alarmControl alarm control
     * @return is it possible to send
     */
    public boolean isSendAlarm(AlarmControlDTO alarmControl) {
        String threadPoolKey = alarmControl.buildPk();
        Cache<String, String> cache = threadPoolAlarmCache.get(threadPoolKey);
        if (cache == null) {
            return false;
        }
        String pkId = cache.getIfPresent(alarmControl.getTypeEnum().name());
        if (StringUtil.isBlank(pkId)) {
            ReentrantLock lock = threadPoolLock.get(threadPoolKey);
            lock.lock();
            try {
                pkId = cache.getIfPresent(alarmControl.getTypeEnum().name());
                if (StringUtil.isBlank(pkId)) {
                    cache.put(alarmControl.getTypeEnum().name(), "-");
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
     * @param threadPoolId thread-pool id
     * @param platform     platform
     * @param interval     interval
     */
    public void initCacheAndLock(String threadPoolId, String platform, Integer interval) {
        String threadPoolKey = threadPoolId + Constants.GROUP_KEY_DELIMITER + platform;
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(interval, TimeUnit.MINUTES)
                .build();
        threadPoolAlarmCache.put(threadPoolKey, cache);
        // Set the lock to prevent false sending of alarm information.
        ReentrantLock reentrantLock = new ReentrantLock();
        threadPoolLock.put(threadPoolKey, reentrantLock);
    }
}
