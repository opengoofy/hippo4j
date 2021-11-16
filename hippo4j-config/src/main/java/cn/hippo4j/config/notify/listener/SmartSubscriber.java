package cn.hippo4j.config.notify.listener;

import cn.hippo4j.config.event.Event;

import java.util.List;

/**
 * Subscribers to multiple events can be listened to.
 *
 * @author chen.ma
 * @date 2021/6/23 19:02
 */
public abstract class SmartSubscriber extends Subscriber {

    /**
     * Subscribe types.
     *
     * @return
     */
    public abstract List<Class<? extends Event>> subscribeTypes();

}
