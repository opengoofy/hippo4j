package cn.hippo4j.config.notify;

import cn.hippo4j.config.event.AbstractEvent;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hippo4j.config.notify.listener.AbstractSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * The default event publisher implementation.
 *
 * @author chen.ma
 * @date 2021/6/23 19:06
 */
@Slf4j
public class DefaultPublisher extends Thread implements EventPublisher {

    protected final ConcurrentHashSet<AbstractSubscriber> subscribers = new ConcurrentHashSet();

    private BlockingQueue<AbstractEvent> queue;

    private volatile boolean initialized = false;

    private volatile boolean shutdown = false;

    private int queueMaxSize = -1;

    protected volatile Long lastEventSequence = -1L;

    private static final AtomicReferenceFieldUpdater<DefaultPublisher, Long> UPDATER = AtomicReferenceFieldUpdater
            .newUpdater(DefaultPublisher.class, Long.class, "lastEventSequence");

    @Override
    public void init(Class<? extends AbstractEvent> type, int bufferSize) {
        setDaemon(true);
        setName("dynamic.thread-pool.publisher-" + type.getName());
        this.queueMaxSize = bufferSize;
        this.queue = new ArrayBlockingQueue(bufferSize);
        start();
    }

    @Override
    public synchronized void start() {
        if (!initialized) {
            // start just called once
            super.start();
            if (queueMaxSize == -1) {
                queueMaxSize = NotifyCenter.ringBufferSize;
            }
            initialized = true;
        }
    }

    @Override
    public void run() {
        openEventHandler();
    }

    private void openEventHandler() {
        try {
            int waitTimes = 60;
            for (; ; ) {
                if (shutdown || hasSubscriber() || waitTimes <= 0) {
                    break;
                }
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                waitTimes--;
            }

            for (; ; ) {
                if (shutdown) {
                    break;
                }
                final AbstractEvent event = queue.take();
                receiveEvent(event);
                UPDATER.compareAndSet(this, lastEventSequence, Math.max(lastEventSequence, event.sequence()));
            }
        } catch (Throwable ex) {
            log.error("Event listener exception :: {}", ex);
        }
    }

    @Override
    public void addSubscriber(AbstractSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public boolean publish(AbstractEvent event) {
        boolean success = this.queue.offer(event);
        if (!success) {
            log.warn("Unable to plug in due to interruption, synchronize sending time, event :: {}", event);
            receiveEvent(event);
            return true;
        }
        return true;
    }

    @Override
    public void notifySubscriber(AbstractSubscriber subscriber, AbstractEvent event) {
        final Runnable job = () -> subscriber.onEvent(event);

        final Executor executor = subscriber.executor();

        if (executor != null) {
            executor.execute(job);
        } else {
            try {
                job.run();
            } catch (Throwable e) {
                log.error("Event callback exception :: {}", e);
            }
        }
    }

    private boolean hasSubscriber() {
        return !CollectionUtils.isEmpty(subscribers);
    }

    void receiveEvent(AbstractEvent event) {
        for (AbstractSubscriber subscriber : subscribers) {
            notifySubscriber(subscriber, event);
        }
    }

}
