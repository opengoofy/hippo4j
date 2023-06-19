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

package cn.hippo4j.common.extension.design;

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Send observer notification.
 */
@Slf4j
public class AbstractSubjectCenter {

    private static final Map<String, List<Observer>> OBSERVERS_MAP = new ConcurrentHashMap();

    /**
     * Register observer.
     *
     * @param observer
     */
    public static void register(Observer observer) {
        register(SubjectType.SPRING_CONTENT_REFRESHED.name(), observer);
    }

    /**
     * Register observer.
     *
     * @param subjectType
     * @param observer
     */
    public static void register(SubjectType subjectType, Observer observer) {
        register(subjectType.name(), observer);
    }

    /**
     * Register observer.
     *
     * @param subject
     * @param observer
     */
    public static void register(String subject, Observer observer) {
        if (StringUtil.isBlank(subject) || observer == null) {
            log.warn("Register observer. A string whose subject or observer is empty or empty.");
            return;
        }
        List<Observer> observers = OBSERVERS_MAP.get(subject);
        if (CollectionUtil.isEmpty(observers)) {
            observers = new ArrayList();
        }
        observers.add(observer);
        OBSERVERS_MAP.put(subject, observers);
    }

    /**
     * Remove observer.
     *
     * @param observer
     */
    public static void remove(Observer observer) {
        remove(SubjectType.SPRING_CONTENT_REFRESHED.name(), observer);
    }

    /**
     * Remove observer.
     *
     * @param subject
     * @param observer
     */
    public static void remove(String subject, Observer observer) {
        List<Observer> observers = OBSERVERS_MAP.get(subject);
        if (StringUtil.isBlank(subject) || CollectionUtil.isEmpty(observers) || observer == null) {
            log.warn("Remove observer. A string whose subject or observer is empty or empty.");
            return;
        }
        observers.remove(observer);
    }

    /**
     * Notify.
     *
     * @param subjectType
     * @param observerMessage
     */
    public static void notify(SubjectType subjectType, ObserverMessage observerMessage) {
        notify(subjectType.name(), observerMessage);
    }

    /**
     * Notify.
     *
     * @param subject
     * @param observerMessage
     */
    public static void notify(String subject, ObserverMessage observerMessage) {
        List<Observer> observers = OBSERVERS_MAP.get(subject);
        if (CollectionUtil.isEmpty(observers)) {
            log.warn("Under the subject, there is no observer group.");
            return;
        }
        observers.parallelStream().forEach(each -> {
            try {
                each.accept(observerMessage);
            } catch (Exception ex) {
                log.error("Notification subject: {} observer exception", subject);
            }
        });
    }

    /**
     * Subject type.
     */
    public enum SubjectType {

        /**
         * Spring content refreshed.
         */
        SPRING_CONTENT_REFRESHED,

        /**
         * Clear config cache.
         */
        CLEAR_CONFIG_CACHE,

        /**
         * Thread-pool dynamic refresh.
         */
        THREAD_POOL_DYNAMIC_REFRESH
    }
}
