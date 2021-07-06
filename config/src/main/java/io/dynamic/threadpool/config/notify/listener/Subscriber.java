package io.dynamic.threadpool.config.notify.listener;

import io.dynamic.threadpool.config.event.Event;

import java.util.concurrent.Executor;

/**
 * An abstract subscriber class for subscriber interface.
 *
 * @author chen.ma
 * @date 2021/6/23 19:02
 */
public abstract class Subscriber<T extends Event> {

    /**
     * Event callback.
     *
     * @param event
     */
    public abstract void onEvent(T event);

    /**
     * Type of this subscriber's subscription.
     *
     * @return
     */
    public abstract Class<? extends Event> subscribeType();

    public Executor executor() {
        return null;
    }
}
