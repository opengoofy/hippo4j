package com.github.dynamic.threadpool.config.notify.listener;

import com.github.dynamic.threadpool.config.event.Event;

import java.util.List;

/**
 * Subscribers to multiple events can be listened to.
 *
 * @author chen.ma
 * @date 2021/6/23 19:02
 */
public abstract class SmartSubscriber extends Subscriber {

    public abstract List<Class<? extends Event>> subscribeTypes();
}
