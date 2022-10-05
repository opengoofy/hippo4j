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

package cn.hippo4j.message.service;

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.message.dto.AlarmControlDTO;
import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
     * @param alarmControl
     * @return
     */
    public boolean isSendAlarm(AlarmControlDTO alarmControl) {
        String threadPoolKey = alarmControl.buildPk();
        Cache<String, String> cache = threadPoolAlarmCache.get(threadPoolKey);
        if (cache == null) {
            return false;
        }
        String pkId = cache.containsKey(alarmControl.getTypeEnum().name()) ? cache.get(alarmControl.getTypeEnum().name()) : null;
        if (StrUtil.isBlank(pkId)) {
            ReentrantLock lock = threadPoolLock.get(threadPoolKey);
            lock.lock();
            try {
                pkId = cache.containsKey(alarmControl.getTypeEnum().name()) ? cache.get(alarmControl.getTypeEnum().name()) : null;
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
        long milliseconds = interval * 60 * 1000;
        Cache<String, String> cache = CacheUtil.newTimedCache(milliseconds);
        threadPoolAlarmCache.put(threadPoolKey, cache);
        // Set the lock to prevent false sending of alarm information.
        ReentrantLock reentrantLock = new ReentrantLock();
        threadPoolLock.put(threadPoolKey, reentrantLock);
    }
}
