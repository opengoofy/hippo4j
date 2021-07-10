package io.dynamic.threadpool.starter.handler;

import io.dynamic.threadpool.starter.toolkit.thread.QueueTypeEnum;
import io.dynamic.threadpool.starter.core.ResizableCapacityLinkedBlockIngQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池修改工具类
 *
 * @author chen.ma
 * @date 2021/6/25 17:19
 */
@Slf4j
public class ThreadPoolChangeHandler {

    public static void changePool(ThreadPoolExecutor executor, Integer coreSize, Integer maxSize, Integer queueType, Integer capacity, Integer keepAliveTime) {
        if (coreSize != null) {
            executor.setCorePoolSize(coreSize);
        }

        if (maxSize != null) {
            executor.setMaximumPoolSize(maxSize);
        }

        if (capacity != null && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.type, queueType)) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockIngQueue) {
                ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) executor.getQueue();
                queue.setCapacity(capacity);
            } else {
                log.warn("[Pool change] The queue length cannot be modified. Queue type mismatch.");
            }
        }

        if (keepAliveTime != null) {
            executor.setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);
        }
    }
}
