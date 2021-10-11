package com.github.dynamic.threadpool.config.notify;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.github.dynamic.threadpool.config.notify.listener.Subscriber;
import com.github.dynamic.threadpool.config.event.Event;
import com.github.dynamic.threadpool.config.event.SlowEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Default share publisher.
 *
 * @author chen.ma
 * @date 2021/6/23 19:05
 */
public class DefaultSharePublisher extends DefaultPublisher {

    private final Map<Class<? extends SlowEvent>, Set<Subscriber>> subMappings = new ConcurrentHashMap();

    protected final ConcurrentHashSet<Subscriber> subscribers = new ConcurrentHashSet();

    private final Lock lock = new ReentrantLock();

    public void addSubscriber(Subscriber subscriber, Class<? extends Event> subscribeType) {
        Class<? extends SlowEvent> subSlowEventType = (Class<? extends SlowEvent>) subscribeType;
        subscribers.add(subscriber);

        lock.lock();
        try {
            Set<Subscriber> sets = subMappings.get(subSlowEventType);
            if (sets == null) {
                Set<Subscriber> newSet = new ConcurrentHashSet();
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
