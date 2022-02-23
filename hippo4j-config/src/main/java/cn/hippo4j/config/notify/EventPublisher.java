package cn.hippo4j.config.notify;

import cn.hippo4j.config.event.AbstractEvent;
import cn.hippo4j.config.notify.listener.AbstractSubscriber;

/**
 * Event publisher.
 *
 * @author chen.ma
 * @date 2021/6/23 18:58
 */
public interface EventPublisher {

    /**
     * Init.
     *
     * @param type
     * @param bufferSize
     */
    void init(Class<? extends AbstractEvent> type, int bufferSize);

    /**
     * Add subscriber.
     *
     * @param subscriber
     */
    void addSubscriber(AbstractSubscriber subscriber);

    /**
     * Publish.
     *
     * @param event
     * @return
     */
    boolean publish(AbstractEvent event);

    /**
     * Notify subscriber.
     *
     * @param subscriber
     * @param event
     */
    void notifySubscriber(AbstractSubscriber subscriber, AbstractEvent event);

}
