package cn.hippo4j.config.notify;

import cn.hippo4j.config.event.AbstractEvent;
import cn.hippo4j.config.notify.listener.AbstractSmartSubscriber;
import cn.hippo4j.config.notify.listener.AbstractSubscriber;
import cn.hippo4j.config.toolkit.ClassUtil;
import cn.hippo4j.config.toolkit.MapUtil;
import cn.hippo4j.config.event.AbstractSlowEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Unified event notify center.
 *
 * @author chen.ma
 * @date 2021/6/23 18:58
 */
@Slf4j
public class NotifyCenter {

    private static final NotifyCenter INSTANCE = new NotifyCenter();

    public static int ringBufferSize = 16384;

    public static int shareBufferSize = 1024;

    private DefaultSharePublisher sharePublisher;

    private static Class<? extends EventPublisher> clazz = null;

    private static EventPublisher eventPublisher = new DefaultPublisher();

    private static BiFunction<Class<? extends AbstractEvent>, Integer, EventPublisher> publisherFactory = null;

    private final Map<String, EventPublisher> publisherMap = new ConcurrentHashMap(16);

    static {
        publisherFactory = (cls, buffer) -> {
            try {
                EventPublisher publisher = eventPublisher;
                publisher.init(cls, buffer);
                return publisher;
            } catch (Throwable ex) {
                log.error("Service class newInstance has error :: {}", ex);
                throw new RuntimeException(ex);
            }
        };

        INSTANCE.sharePublisher = new DefaultSharePublisher();
        INSTANCE.sharePublisher.init(AbstractSlowEvent.class, shareBufferSize);
    }

    public static void registerSubscriber(final AbstractSubscriber consumer) {
        if (consumer instanceof AbstractSmartSubscriber) {
            for (Class<? extends AbstractEvent> subscribeType : ((AbstractSmartSubscriber) consumer).subscribeTypes()) {
                if (ClassUtil.isAssignableFrom(AbstractSlowEvent.class, subscribeType)) {
                    INSTANCE.sharePublisher.addSubscriber(consumer, subscribeType);
                } else {
                    addSubscriber(consumer, subscribeType);
                }
            }
            return;
        }

        final Class<? extends AbstractEvent> subscribeType = consumer.subscribeType();
        if (ClassUtil.isAssignableFrom(AbstractSlowEvent.class, subscribeType)) {
            INSTANCE.sharePublisher.addSubscriber(consumer, subscribeType);
            return;
        }

        addSubscriber(consumer, subscribeType);
    }

    private static void addSubscriber(final AbstractSubscriber consumer, Class<? extends AbstractEvent> subscribeType) {
        final String topic = ClassUtil.getCanonicalName(subscribeType);
        synchronized (NotifyCenter.class) {
            MapUtil.computeIfAbsent(INSTANCE.publisherMap, topic, publisherFactory, subscribeType, ringBufferSize);
        }
        EventPublisher publisher = INSTANCE.publisherMap.get(topic);
        publisher.addSubscriber(consumer);
    }

    public static boolean publishEvent(final AbstractEvent event) {
        try {
            return publishEvent(event.getClass(), event);
        } catch (Throwable ex) {
            log.error("There was an exception to the message publishing :: {}", ex);
            return false;
        }
    }

    private static boolean publishEvent(final Class<? extends AbstractEvent> eventType, final AbstractEvent event) {
        if (ClassUtil.isAssignableFrom(AbstractSlowEvent.class, eventType)) {
            return INSTANCE.sharePublisher.publish(event);
        }

        final String topic = ClassUtil.getCanonicalName(eventType);

        EventPublisher publisher = INSTANCE.publisherMap.get(topic);
        if (publisher != null) {
            return publisher.publish(event);
        }
        log.warn("There are no [{}] publishers for this event, please register", topic);
        return false;
    }

    public static EventPublisher registerToPublisher(final Class<? extends AbstractEvent> eventType, final int queueMaxSize) {
        if (ClassUtil.isAssignableFrom(AbstractSlowEvent.class, eventType)) {
            return INSTANCE.sharePublisher;
        }

        final String topic = ClassUtil.getCanonicalName(eventType);
        synchronized (NotifyCenter.class) {
            MapUtil.computeIfAbsent(INSTANCE.publisherMap, topic, publisherFactory, eventType, queueMaxSize);
        }
        return INSTANCE.publisherMap.get(topic);
    }

}
