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

/**
 * Blocking queue runtime manager.
 * Provides queue management operations: capacity adjustment, type recognition, and configuration validation.
 * 
 * <p>Note: For queue creation, use {@link BlockingQueueTypeEnum#createBlockingQueue(int, Integer)} 
 * or {@link BlockingQueueTypeEnum#createBlockingQueue(String, Integer)} directly.</p>
 */
@Slf4j
public class BlockingQueueManager {

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
