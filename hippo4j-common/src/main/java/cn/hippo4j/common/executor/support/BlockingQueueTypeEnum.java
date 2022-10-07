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

import cn.hippo4j.common.spi.DynamicThreadPoolServiceLoader;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Stream;

/**
 * Blocking queue type enum.
 */
public enum BlockingQueueTypeEnum {

    /**
     * {@link java.util.concurrent.ArrayBlockingQueue}
     */
    ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue"),

    /**
     * {@link java.util.concurrent.LinkedBlockingQueue}
     */
    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue"),

    /**
     * {@link java.util.concurrent.LinkedBlockingDeque}
     */
    LINKED_BLOCKING_DEQUE(3, "LinkedBlockingDeque"),

    /**
     * {@link java.util.concurrent.SynchronousQueue}
     */
    SYNCHRONOUS_QUEUE(4, "SynchronousQueue"),

    /**
     * {@link java.util.concurrent.LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE(5, "LinkedTransferQueue"),

    /**
     * {@link java.util.concurrent.PriorityBlockingQueue}
     */
    PRIORITY_BLOCKING_QUEUE(6, "PriorityBlockingQueue"),

    /**
     * {@link ResizableCapacityLinkedBlockingQueue}
     */
    RESIZABLE_LINKED_BLOCKING_QUEUE(9, "ResizableCapacityLinkedBlockingQueue");

    @Getter
    private Integer type;

    @Getter
    private String name;

    BlockingQueueTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private static final int DEFAULT_CAPACITY = 1024;

    static {
        DynamicThreadPoolServiceLoader.register(CustomBlockingQueue.class);
    }

    public static BlockingQueue createBlockingQueue(String blockingQueueName, Integer capacity) {
        BlockingQueue blockingQueue = null;
        BlockingQueueTypeEnum queueTypeEnum = Stream.of(BlockingQueueTypeEnum.values())
                .filter(each -> Objects.equals(each.name, blockingQueueName))
                .findFirst()
                .orElse(null);
        if (queueTypeEnum != null) {
            blockingQueue = createBlockingQueue(queueTypeEnum.type, capacity);
            if (Objects.equals(blockingQueue.getClass().getSimpleName(), blockingQueueName)) {
                return blockingQueue;
            }
        }
        Collection<CustomBlockingQueue> customBlockingQueues = DynamicThreadPoolServiceLoader
                .getSingletonServiceInstances(CustomBlockingQueue.class);
        blockingQueue = Optional.ofNullable(blockingQueue)
                .orElseGet(
                        () -> customBlockingQueues.stream()
                                .filter(each -> Objects.equals(blockingQueueName, each.getName()))
                                .map(each -> each.generateBlockingQueue())
                                .findFirst()
                                .orElseGet(() -> {
                                    int temCapacity = capacity;
                                    if (capacity == null || capacity <= 0) {
                                        temCapacity = DEFAULT_CAPACITY;
                                    }
                                    return new LinkedBlockingQueue(temCapacity);
                                }));
        return blockingQueue;
    }

    public static BlockingQueue createBlockingQueue(int type, Integer capacity) {
        BlockingQueue blockingQueue = null;
        if (Objects.equals(type, ARRAY_BLOCKING_QUEUE.type)) {
            blockingQueue = new ArrayBlockingQueue(capacity);
        } else if (Objects.equals(type, LINKED_BLOCKING_QUEUE.type)) {
            blockingQueue = new LinkedBlockingQueue(capacity);
        } else if (Objects.equals(type, LINKED_BLOCKING_DEQUE.type)) {
            blockingQueue = new LinkedBlockingDeque(capacity);
        } else if (Objects.equals(type, SYNCHRONOUS_QUEUE.type)) {
            blockingQueue = new SynchronousQueue();
        } else if (Objects.equals(type, LINKED_TRANSFER_QUEUE.type)) {
            blockingQueue = new LinkedTransferQueue();
        } else if (Objects.equals(type, PRIORITY_BLOCKING_QUEUE.type)) {
            blockingQueue = new PriorityBlockingQueue(capacity);
        } else if (Objects.equals(type, RESIZABLE_LINKED_BLOCKING_QUEUE.type)) {
            blockingQueue = new ResizableCapacityLinkedBlockingQueue(capacity);
        }
        Collection<CustomBlockingQueue> customBlockingQueues = DynamicThreadPoolServiceLoader
                .getSingletonServiceInstances(CustomBlockingQueue.class);
        blockingQueue = Optional.ofNullable(blockingQueue).orElseGet(() -> customBlockingQueues.stream()
                .filter(each -> Objects.equals(type, each.getType()))
                .map(each -> each.generateBlockingQueue())
                .findFirst()
                .orElse(new LinkedBlockingQueue(capacity)));
        return blockingQueue;
    }

    public static String getBlockingQueueNameByType(int type) {
        Optional<BlockingQueueTypeEnum> queueTypeEnum = Arrays.stream(BlockingQueueTypeEnum.values())
                .filter(each -> each.type == type)
                .findFirst();
        return queueTypeEnum.map(each -> each.name).orElse("");
    }

    public static BlockingQueueTypeEnum getBlockingQueueTypeEnumByName(String name) {
        Optional<BlockingQueueTypeEnum> queueTypeEnum = Arrays.stream(BlockingQueueTypeEnum.values())
                .filter(each -> each.name.equals(name))
                .findFirst();
        return queueTypeEnum.orElse(LINKED_BLOCKING_QUEUE);
    }
}
