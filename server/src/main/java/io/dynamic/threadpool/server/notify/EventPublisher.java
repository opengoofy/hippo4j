package io.dynamic.threadpool.server.notify;

import io.dynamic.threadpool.server.event.Event;
import io.dynamic.threadpool.server.notify.listener.Subscriber;

/**
 * Event publisher.
 *
 * @author chen.ma
 * @date 2021/6/23 18:58
 */
public interface EventPublisher {

    void init(Class<? extends Event> type, int bufferSize);

    void addSubscriber(Subscriber subscriber);

    boolean publish(Event event);

    void notifySubscriber(Subscriber subscriber, Event event);

}
