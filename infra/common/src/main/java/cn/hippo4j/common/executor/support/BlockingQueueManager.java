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

package cn.hippo4j.common.executor.support;

import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Blocking queue manager for dynamic queue type switching.
 * Supports SPI extension and dynamic queue replacement.
 */
@Slf4j
public class BlockingQueueManager {

    static {
        ServiceLoaderRegistry.register(CustomBlockingQueue.class);
    }

    /**
     * Create blocking queue by type and capacity
     *
     * @param queueType queue type
     * @param capacity queue capacity
     * @param <T> queue element type
     * @return blocking queue instance
     */
    public static <T> BlockingQueue<T> createQueue(Integer queueType, Integer capacity) {
        if (queueType == null) {
            queueType = BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE.getType();
        }
        return BlockingQueueTypeEnum.createBlockingQueue(queueType, capacity);
    }

    /**
     * Create blocking queue by name and capacity
     *
     * @param queueName queue name
     * @param capacity queue capacity
     * @param <T> queue element type
     * @return blocking queue instance
     */
    public static <T> BlockingQueue<T> createQueue(String queueName, Integer capacity) {
        if (queueName == null || queueName.isEmpty()) {
            queueName = BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE.getName();
        }
        return BlockingQueueTypeEnum.createBlockingQueue(queueName, capacity);
    }

    /**
     * Check if queue type can be dynamically changed
     *
     * @param currentQueue current queue instance
     * @param newQueueType new queue type
     * @return true if can be changed
     */
    public static boolean canChangeQueueType(BlockingQueue<?> currentQueue, Integer newQueueType) {
        if (currentQueue == null || newQueueType == null) {
            return true;
        }
        // Special case: ResizableCapacityLinkedBlockingQueue can only change capacity
        if (currentQueue instanceof ResizableCapacityLinkedBlockingQueue) {
            return Objects.equals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getType(), newQueueType);
        }
        // For other queue types, check if they are the same type
        String currentQueueName = currentQueue.getClass().getSimpleName();
        String newQueueName = BlockingQueueTypeEnum.getBlockingQueueNameByType(newQueueType);
        return !Objects.equals(currentQueueName, newQueueName);
    }

    /**
     * Check if queue capacity can be dynamically changed
     *
     * @param currentQueue current queue instance
     * @return true if capacity can be changed
     */
    public static boolean canChangeCapacity(BlockingQueue<?> currentQueue) {
        if (currentQueue == null) {
            return false;
        }
        // Only ResizableCapacityLinkedBlockingQueue supports capacity change
        return currentQueue instanceof ResizableCapacityLinkedBlockingQueue;
    }

    /**
     * Change queue capacity if supported
     *
     * @param currentQueue current queue instance
     * @param newCapacity new capacity
     * @return true if capacity was changed
     */
    public static boolean changeQueueCapacity(BlockingQueue<?> currentQueue, Integer newCapacity) {
        if (!canChangeCapacity(currentQueue) || newCapacity == null) {
            return false;
        }
        try {
            if (currentQueue instanceof ResizableCapacityLinkedBlockingQueue) {
                ResizableCapacityLinkedBlockingQueue<?> resizableQueue =
                        (ResizableCapacityLinkedBlockingQueue<?>) currentQueue;
                resizableQueue.setCapacity(newCapacity);
                log.debug("Queue capacity changed to: {}", newCapacity);
                return true;
            }
        } catch (Exception e) {
            log.error("Failed to change queue capacity to: {}", newCapacity, e);
        }
        return false;
    }

    /**
     * Replace queue in thread pool executor
     *
     * @param executor thread pool executor
     * @param newQueue new queue instance
     * @param <T> queue element type
     * @return true if queue was replaced
     */
    public static <T> boolean replaceQueue(ThreadPoolExecutor executor, BlockingQueue<T> newQueue) {
        if (executor == null || newQueue == null) {
            return false;
        }
        try {
            if (executor.getActiveCount() > 0 || !executor.getQueue().isEmpty()) {
                return false;
            }
            try {
                java.lang.reflect.Field field = ThreadPoolExecutor.class.getDeclaredField("workQueue");
                field.setAccessible(true);
                field.set(executor, newQueue);
                return true;
            } catch (Throwable ignore) {
                log.warn("JDK security prevents replacing workQueue; skip.");
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to replace queue", e);
            return false;
        }
    }

    /**
     * Get queue type from queue instance
     *
     * @param queue queue instance
     * @return queue type, null if not found
     */
    public static Integer getQueueType(BlockingQueue<?> queue) {
        if (queue == null) {
            return null;
        }
        String queueName = queue.getClass().getSimpleName();
        for (BlockingQueueTypeEnum typeEnum : BlockingQueueTypeEnum.values()) {
            if (Objects.equals(typeEnum.getName(), queueName)) {
                return typeEnum.getType();
            }
        }
        Collection<CustomBlockingQueue> customQueues = ServiceLoaderRegistry
                .getSingletonServiceInstances(CustomBlockingQueue.class);
        for (CustomBlockingQueue customQueue : customQueues) {
            if (Objects.equals(customQueue.getName(), queueName)) {
                return customQueue.getType();
            }
        }
        return null;
    }

    /**
     * Get queue name from queue instance
     *
     * @param queue queue instance
     * @return queue name
     */
    public static String getQueueName(BlockingQueue<?> queue) {
        if (queue == null) {
            return "Unknown";
        }
        return queue.getClass().getSimpleName();
    }

    /**
     * Validate queue configuration
     *
     * @param queueType queue type
     * @param capacity queue capacity
     * @return validation result
     */
    public static boolean validateQueueConfig(Integer queueType, Integer capacity) {
        if (queueType == null) {
            return false;
        }
        String queueName = BlockingQueueTypeEnum.getBlockingQueueNameByType(queueType);
        if (queueName.isEmpty()) {
            Collection<CustomBlockingQueue> customQueues = ServiceLoaderRegistry
                    .getSingletonServiceInstances(CustomBlockingQueue.class);
            boolean found = customQueues.stream()
                    .anyMatch(customQueue -> Objects.equals(customQueue.getType(), queueType));
            if (!found) {
                log.warn("Invalid queue type: {}", queueType);
                return false;
            }
        }
        if (capacity != null && capacity <= 0) {
            if (queueType.equals(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE.getType()) ||
                    queueType.equals(BlockingQueueTypeEnum.LINKED_TRANSFER_QUEUE.getType())) {
                return true;
            }
            log.warn("Invalid capacity: {} for queue type: {}", capacity, queueType);
            return false;
        }
        return true;
    }
}
