/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.config.notify;

import cn.hippo4j.config.event.AbstractEvent;
import cn.hippo4j.config.notify.listener.AbstractSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * The default event publisher implementation.
 */
@Slf4j
public class DefaultPublisher extends Thread implements EventPublisher {

    protected final Set<AbstractSubscriber> subscribers = Collections.synchronizedSet(new HashSet<>());

    private BlockingQueue<AbstractEvent> queue;

    private volatile boolean initialized = false;

    private volatile boolean shutdown = false;

    private int queueMaxSize = -1;

    protected volatile Long lastEventSequence = -1L;

    private static final int DEFAULT_QUEUE_MAX_SIZE = -1;

    private static final int DEFAULT_WAIT_TIMES = 60;

    private static final long SLEEP_1S = 1000L;

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
            super.start();
            if (queueMaxSize == DEFAULT_QUEUE_MAX_SIZE) {
                queueMaxSize = NotifyCenter.RING_BUFFER_SIZE;
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
            int waitTimes = DEFAULT_WAIT_TIMES;
            for (;;) {
                if (shutdown || hasSubscriber() || waitTimes <= 0) {
                    break;
                }
                try {
                    Thread.sleep(SLEEP_1S);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                waitTimes--;
            }
            for (;;) {
                if (shutdown) {
                    break;
                }
                final AbstractEvent event = queue.take();
                receiveEvent(event);
                UPDATER.compareAndSet(this, lastEventSequence, Math.max(lastEventSequence, event.sequence()));
            }
        } catch (Throwable ex) {
            log.error("Event listener exception.", ex);
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
            log.warn("Unable to plug in due to interruption, synchronize sending time, event: {}", event);
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
                log.error("Event callback exception: {}", e);
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
