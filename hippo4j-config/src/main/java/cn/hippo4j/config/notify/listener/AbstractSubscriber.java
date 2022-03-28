package cn.hippo4j.config.notify.listener;

import cn.hippo4j.config.event.AbstractEvent;

import java.util.concurrent.Executor;

/**
 * An abstract subscriber class for subscriber interface.
 *
 * @author chen.ma
 * @date 2021/6/23 19:02
 */
public abstract class AbstractSubscriber<T extends AbstractEvent> {

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
    public abstract Class<? extends AbstractEvent> subscribeType();

    public Executor executor() {
        return null;
    }

}
