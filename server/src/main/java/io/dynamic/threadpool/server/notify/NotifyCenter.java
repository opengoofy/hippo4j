package io.dynamic.threadpool.server.notify;

import io.dynamic.threadpool.server.notify.listener.SmartSubscriber;
import io.dynamic.threadpool.server.notify.listener.Subscriber;
import io.dynamic.threadpool.server.toolkit.ClassUtil;
import io.dynamic.threadpool.server.toolkit.MapUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Unified Event Notify Center.
 *
 * @author chen.ma
 * @date 2021/6/23 18:58
 */
public class NotifyCenter {

    private static final NotifyCenter INSTANCE = new NotifyCenter();

    public static int ringBufferSize = 16384;

    private DefaultSharePublisher sharePublisher;

    private static BiFunction<Class<? extends Event>, Integer, EventPublisher> publisherFactory = null;

    private final Map<String, EventPublisher> publisherMap = new ConcurrentHashMap(16);

    public static <T> void registerSubscriber(final Subscriber consumer) {
        if (consumer instanceof SmartSubscriber) {
            for (Class<? extends Event> subscribeType : ((SmartSubscriber) consumer).subscribeTypes()) {
                if (ClassUtil.isAssignableFrom(SlowEvent.class, subscribeType)) {
                    INSTANCE.sharePublisher.addSubscriber(consumer, subscribeType);
                } else {
                    addSubscriber(consumer, subscribeType);
                }
            }
            return;
        }

        final Class<? extends Event> subscribeType = consumer.subscribeType();
        if (ClassUtil.isAssignableFrom(SlowEvent.class, subscribeType)) {
            INSTANCE.sharePublisher.addSubscriber(consumer, subscribeType);
            return;
        }

        addSubscriber(consumer, subscribeType);
    }

    private static void addSubscriber(final Subscriber consumer, Class<? extends Event> subscribeType) {

        final String topic = ClassUtil.getCanonicalName(subscribeType);
        synchronized (NotifyCenter.class) {
            // MapUtils.computeIfAbsent is a unsafe method.
            MapUtil.computeIfAbsent(INSTANCE.publisherMap, topic, publisherFactory, subscribeType, ringBufferSize);
        }
        EventPublisher publisher = INSTANCE.publisherMap.get(topic);
        publisher.addSubscriber(consumer);
    }

}
