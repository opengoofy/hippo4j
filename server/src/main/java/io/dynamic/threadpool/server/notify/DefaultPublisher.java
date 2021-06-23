package io.dynamic.threadpool.server.notify;

import cn.hutool.core.collection.ConcurrentHashSet;
import io.dynamic.threadpool.server.notify.listener.Subscriber;

/**
 * The default event publisher implementation.
 *
 * @author chen.ma
 * @date 2021/6/23 19:06
 */
public class DefaultPublisher extends Thread implements EventPublisher {

    protected final ConcurrentHashSet<Subscriber> subscribers = new ConcurrentHashSet();

    @Override
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }
}
