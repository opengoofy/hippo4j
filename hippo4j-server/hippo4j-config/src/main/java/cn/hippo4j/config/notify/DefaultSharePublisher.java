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

package cn.hippo4j.config.notify;

import cn.hippo4j.config.event.AbstractEvent;
import cn.hippo4j.config.notify.listener.AbstractSubscriber;
import cn.hippo4j.config.event.AbstractSlowEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Default share publisher.
 */
public class DefaultSharePublisher extends DefaultPublisher {

    private final Map<Class<? extends AbstractSlowEvent>, Set<AbstractSubscriber>> subMappings = new ConcurrentHashMap();

    protected final Set<AbstractSubscriber> subscribers = Collections.synchronizedSet(new HashSet<>());

    private final Lock lock = new ReentrantLock();

    public void addSubscriber(AbstractSubscriber subscriber, Class<? extends AbstractEvent> subscribeType) {
        Class<? extends AbstractSlowEvent> subSlowEventType = (Class<? extends AbstractSlowEvent>) subscribeType;
        subscribers.add(subscriber);
        lock.lock();
        try {
            Set<AbstractSubscriber> sets = subMappings.get(subSlowEventType);
            if (sets == null) {
                Set<AbstractSubscriber> newSet = Collections.synchronizedSet(new HashSet<>());
                newSet.add(subscriber);
                subMappings.put(subSlowEventType, newSet);
                return;
            }
            sets.add(subscriber);
        } finally {
            lock.unlock();
        }
    }
}
