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
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.function.Predicate;

/**
 * Blocking queue type enum.
 */
public enum BlockingQueueTypeEnum {

    /**
     * {@link java.util.concurrent.ArrayBlockingQueue}
     */
    ARRAY_BLOCKING_QUEUE(1, "ArrayBlockingQueue") {

        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new ArrayBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new ArrayBlockingQueue<>(DEFAULT_CAPACITY);
        }
    },

    /**
     * {@link java.util.concurrent.LinkedBlockingQueue}
     */
    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue") {

        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedBlockingQueue<>();
        }
    },

    /**
     * {@link java.util.concurrent.LinkedBlockingDeque}
     */
    LINKED_BLOCKING_DEQUE(3, "LinkedBlockingDeque") {

        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedBlockingDeque<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedBlockingDeque<>();
        }
    },

    /**
     * {@link java.util.concurrent.SynchronousQueue}
     */
    SYNCHRONOUS_QUEUE(4, "SynchronousQueue") {

        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new SynchronousQueue<>();
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new SynchronousQueue<>();
        }
    },

    /**
     * {@link java.util.concurrent.LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE(5, "LinkedTransferQueue") {

        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedTransferQueue<>();
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new LinkedTransferQueue<>();
        }
    },

    /**
     * {@link java.util.concurrent.PriorityBlockingQueue}
     */
    PRIORITY_BLOCKING_QUEUE(6, "PriorityBlockingQueue") {

        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new PriorityBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new PriorityBlockingQueue<>();
        }
    },

    /**
     * {@link ResizableCapacityLinkedBlockingQueue}
     */
    RESIZABLE_LINKED_BLOCKING_QUEUE(9, "ResizableCapacityLinkedBlockingQueue") {

        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new ResizableCapacityLinkedBlockingQueue<>(capacity);
        }

        @Override
        <T> BlockingQueue<T> of() {
            return new ResizableCapacityLinkedBlockingQueue<>();
        }
    };

    @Getter
    private final Integer type;

    @Getter
    private final String name;

    /**
     * Create the specified implement of BlockingQueue with init capacity.
     * Abstract method, depends on sub override
     *
     * @param capacity the capacity of the queue
     * @param <T>      the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    abstract <T> BlockingQueue<T> of(Integer capacity);

    /**
     * Create the specified implement of BlockingQueue,has no capacity limit.
     * Abstract method, depends on sub override
     *
     * @param <T> the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    abstract <T> BlockingQueue<T> of();

    BlockingQueueTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private static final Map<Integer, BlockingQueueTypeEnum> TYPE_TO_ENUM_MAP;
    private static final Map<String, BlockingQueueTypeEnum> NAME_TO_ENUM_MAP;

    static {
        final BlockingQueueTypeEnum[] values = BlockingQueueTypeEnum.values();
        TYPE_TO_ENUM_MAP = new HashMap<>(values.length);
        NAME_TO_ENUM_MAP = new HashMap<>(values.length);
        for (BlockingQueueTypeEnum value : values) {
            TYPE_TO_ENUM_MAP.put(value.type, value);
            NAME_TO_ENUM_MAP.put(value.name, value);
        }
    }

    /**
     * Creates a BlockingQueue with the given {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * and capacity.
     *
     * @param blockingQueueName {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * @param capacity          the capacity of the BlockingQueue
     * @param <T>               the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    private static <T> BlockingQueue<T> of(String blockingQueueName, Integer capacity) {
        final BlockingQueueTypeEnum typeEnum = NAME_TO_ENUM_MAP.get(blockingQueueName);
        if (typeEnum == null) {
            return null;
        }
        return Objects.isNull(capacity) ? typeEnum.of() : typeEnum.of(capacity);
    }

    /**
     * Creates a BlockingQueue with the given {@link BlockingQueueTypeEnum#type BlockingQueueTypeEnum.type}
     * and capacity.
     *
     * @param type     {@link BlockingQueueTypeEnum#type BlockingQueueTypeEnum.type}
     * @param capacity the capacity of the BlockingQueue
     * @param <T>      the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    private static <T> BlockingQueue<T> of(int type, Integer capacity) {
        final BlockingQueueTypeEnum typeEnum = TYPE_TO_ENUM_MAP.get(type);
        if (typeEnum == null) {
            return null;
        }
        return Objects.isNull(capacity) ? typeEnum.of() : typeEnum.of(capacity);
    }

    private static final int DEFAULT_CAPACITY = 1024;

    static {
        ServiceLoaderRegistry.register(CustomBlockingQueue.class);
    }

    private static <T> BlockingQueue<T> customOrDefaultQueue(Integer capacity, Predicate<CustomBlockingQueue> predicate) {
        Collection<CustomBlockingQueue> customBlockingQueues = ServiceLoaderRegistry
                .getSingletonServiceInstances(CustomBlockingQueue.class);

        return customBlockingQueues.stream()
                .filter(predicate)
                .map(each -> each.generateBlockingQueue())
                .findFirst()
                .orElseGet(() -> {
                    Integer tempCapacity = capacity;
                    if (capacity == null || capacity <= 0) {
                        tempCapacity = DEFAULT_CAPACITY;
                    }
                    return new LinkedBlockingQueue<T>(tempCapacity);
                });
    }

    /**
     * Creates a BlockingQueue with the given {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * and capacity. if can't find the blockingQueueName with {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name},
     * create custom or default BlockingQueue {@link BlockingQueueTypeEnum#customOrDefaultQueue BlockingQueueTypeEnum.customOrDefaultQueue}.
     *
     * @param blockingQueueName {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name}
     * @param capacity          the capacity of the BlockingQueue
     * @param <T>               the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    public static <T> BlockingQueue<T> createBlockingQueue(String blockingQueueName, Integer capacity) {
        final BlockingQueue<T> of = of(blockingQueueName, capacity);
        if (of != null) {
            return of;
        }

        return customOrDefaultQueue(capacity,
                (customerQueue) -> Objects.equals(customerQueue.getName(), blockingQueueName));
    }

    /**
     * Creates a BlockingQueue with the given {@link BlockingQueueTypeEnum#type BlockingQueueTypeEnum.type}
     * and capacity. if can't find the blockingQueueName with {@link BlockingQueueTypeEnum#type BlockingQueueTypeEnum.type},
     * create custom or default BlockingQueue {@link BlockingQueueTypeEnum#customOrDefaultQueue BlockingQueueTypeEnum.customOrDefaultQueue}.
     *
     * @param type     {@link BlockingQueueTypeEnum#type BlockingQueueTypeEnum.type}
     * @param capacity the capacity of the BlockingQueue
     * @param <T>      the class of the objects in the BlockingQueue
     * @return a BlockingQueue view of the specified T
     */
    public static <T> BlockingQueue<T> createBlockingQueue(int type, Integer capacity) {
        final BlockingQueue<T> of = of(type, capacity);
        if (of != null) {
            return of;
        }

        return customOrDefaultQueue(capacity,
                (customeQueue) -> Objects.equals(customeQueue.getType(), type));
    }

    /**
     * Map {@link BlockingQueueTypeEnum#type BlockingQueueTypeEnum.type } to {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name }
     * or "" if can't mapping.
     *
     * @param type {@link BlockingQueueTypeEnum#type BlockingQueueTypeEnum.type}
     * @return {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name } or "".
     */
    public static String getBlockingQueueNameByType(int type) {
        return Optional.ofNullable(TYPE_TO_ENUM_MAP.get(type))
                .map(value -> value.getName())
                .orElse("");
    }

    /**
     * find {@link BlockingQueueTypeEnum} by {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name }
     * or {@link BlockingQueueTypeEnum#LINKED_BLOCKING_QUEUE} if can't mapping.
     *
     * @param name {@link BlockingQueueTypeEnum#name BlockingQueueTypeEnum.name }
     * @return enum  {@link BlockingQueueTypeEnum}
     */
    public static BlockingQueueTypeEnum getBlockingQueueTypeEnumByName(String name) {
        return Optional.ofNullable(NAME_TO_ENUM_MAP.get(name))
                .orElse(LINKED_BLOCKING_QUEUE);
    }
}
