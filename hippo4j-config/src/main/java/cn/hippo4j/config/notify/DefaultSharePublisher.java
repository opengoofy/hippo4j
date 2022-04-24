package cn.hippo4j.config.notify;

import cn.hippo4j.config.event.AbstractEvent;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hippo4j.config.notify.listener.AbstractSubscriber;
import cn.hippo4j.config.event.AbstractSlowEvent;

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

    private final Map<Class<? extends AbstractSlowEvent>, Set<AbstractSubscriber>> subMappings = new ConcurrentHashMap();

    protected final ConcurrentHashSet<AbstractSubscriber> subscribers = new ConcurrentHashSet();

    private final Lock lock = new ReentrantLock();

    public void addSubscriber(AbstractSubscriber subscriber, Class<? extends AbstractEvent> subscribeType) {
        Class<? extends AbstractSlowEvent> subSlowEventType = (Class<? extends AbstractSlowEvent>) subscribeType;
        subscribers.add(subscriber);

        lock.lock();
        try {
            Set<AbstractSubscriber> sets = subMappings.get(subSlowEventType);
            if (sets == null) {
                Set<AbstractSubscriber> newSet = new ConcurrentHashSet();
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
