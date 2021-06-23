package io.dynamic.threadpool.server.notify;

import io.dynamic.threadpool.server.notify.listener.Subscriber;

/**
 * Event publisher.
 *
 * @author chen.ma
 * @date 2021/6/23 18:58
 */
public interface EventPublisher {

    void addSubscriber(Subscriber subscriber);

}
