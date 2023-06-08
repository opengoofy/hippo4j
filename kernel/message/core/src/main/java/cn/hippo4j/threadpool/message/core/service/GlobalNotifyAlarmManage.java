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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global notify alarm manage.
 */
public class GlobalNotifyAlarmManage {

    /**
     * Notify alarm map.
     */
    private static final Map<String, ThreadPoolNotifyAlarm> NOTIFY_ALARM_MAP = new ConcurrentHashMap();

    /**
     * Get thread-pool notify alarm.
     *
     * @param key thread-pool key
     * @return thread-pool notify alarm
     */
    public static ThreadPoolNotifyAlarm get(String key) {
        return NOTIFY_ALARM_MAP.get(key);
    }

    /**
     * Put thread-pool notify alarm.
     *
     * @param key thread-pool key
     * @param val thread-pool notify alarm
     */
    public static void put(String key, ThreadPoolNotifyAlarm val) {
        NOTIFY_ALARM_MAP.put(key, val);
    }
}
