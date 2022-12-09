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
import cn.hippo4j.common.web.exception.NotSupportedException;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

import static cn.hippo4j.common.web.exception.ErrorCodeEnum.SERVICE_ERROR;

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
    },

    /**
     * {@link java.util.concurrent.LinkedBlockingQueue}
     */
    LINKED_BLOCKING_QUEUE(2, "LinkedBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new LinkedBlockingQueue<>(capacity);
        }
    },

    /**
     * {@link java.util.concurrent.LinkedBlockingDeque}
     */
    LINKED_BLOCKING_DEQUE(3, "LinkedBlockingDeque") {
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
        <T> BlockingQueue<T> of() {
            return new SynchronousQueue<>();
        }
    },

    /**
     * {@link java.util.concurrent.LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE(5, "LinkedTransferQueue") {
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
    },

    /**
     * {@link ResizableCapacityLinkedBlockingQueue}
     */
    RESIZABLE_LINKED_BLOCKING_QUEUE(9, "ResizableCapacityLinkedBlockingQueue") {
        @Override
        <T> BlockingQueue<T> of(Integer capacity) {
            return new ResizableCapacityLinkedBlockingQueue<>(capacity);
        }
    };

    @Getter
    private Integer type;

    @Getter
    private String name;

    BlockingQueueTypeEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private static Map<Integer, BlockingQueueTypeEnum> typeToEnumMap;
    private static Map<String, BlockingQueueTypeEnum> nameToEnumMap;

    static {
        final BlockingQueueTypeEnum[] values = BlockingQueueTypeEnum.values();
        typeToEnumMap = new HashMap<>(values.length);
        nameToEnumMap = new HashMap<>(values.length);
        for (BlockingQueueTypeEnum value : values) {
            typeToEnumMap.put(value.type, value);
            nameToEnumMap.put(value.name, value);
        }
    }

    /**
     * 子类按需实现，默认不支持该实例化方式
     *
     * @param capacity
     * @return
     */
    <T> BlockingQueue<T> of(Integer capacity) {
        throw new NotSupportedException("该队列必须有界", SERVICE_ERROR);
    }

    /**
     * 子类按需实现，默认不支持该实例化方式
     *
     * @return
     */
    <T> BlockingQueue<T> of() {
        throw new NotSupportedException("该队列不支持有界", SERVICE_ERROR);
    }

    private static <T> BlockingQueue<T> of(String blockingQueueName, Integer capacity) {
        final BlockingQueueTypeEnum typeEnum = nameToEnumMap.get(blockingQueueName);
        if (typeEnum == null) {
            return null;
        }
        return Objects.isNull(capacity) ? typeEnum.of() : typeEnum.of(capacity);
    }

    private static <T> BlockingQueue<T> of(int type, Integer capacity) {
        final BlockingQueueTypeEnum typeEnum = typeToEnumMap.get(type);
        if (typeEnum == null) {
            return null;
        }
        return Objects.isNull(capacity) ? typeEnum.of() : typeEnum.of(capacity);
    }


    private static final int DEFAULT_CAPACITY = 1024;

    static {
        DynamicThreadPoolServiceLoader.register(CustomBlockingQueue.class);
    }

    private static <T> BlockingQueue<T> customOrDefaultQueue(Integer capacity, Predicate<CustomBlockingQueue> predicate) {
        Collection<CustomBlockingQueue> customBlockingQueues = DynamicThreadPoolServiceLoader
                .getSingletonServiceInstances(CustomBlockingQueue.class);

        return customBlockingQueues.stream()
                .filter(predicate)
                .map(each -> each.generateBlockingQueue())
                .findFirst()
                .orElseGet(() -> {
                    int temCapacity = capacity;
                    if (capacity == null || capacity <= 0) {
                        temCapacity = DEFAULT_CAPACITY;
                    }
                    return new LinkedBlockingQueue<T>(temCapacity);
                });
    }

    /**
     * 根据队列名创建对应的队列
     *
     * @param blockingQueueName {@linkplain BlockingQueueTypeEnum#name} 队列值
     * @param capacity          队列大小，如果不支持 队列名 对应队列. 则是默认队列 LinkedBlockingQueue 的大小
     * @return 返回要求的队列，或者 LinkedBlockingQueue 队列
     */
    public static <T> BlockingQueue<T> createBlockingQueue(String blockingQueueName, Integer capacity) {
        final BlockingQueue<T> of = of(blockingQueueName, capacity);
        if (of != null) {
            return of;
        }

        return customOrDefaultQueue(capacity,
                (customeQueue) -> Objects.equals(customeQueue.getName(), blockingQueueName));
    }

    /**
     * 根据队列名创建对应的队列
     *
     * @param type     {@linkplain BlockingQueueTypeEnum#type} 队列值
     * @param capacity 队列大小，如果不支持 队列名 对应队列. 则是默认队列 LinkedBlockingQueue 的大小
     * @return 返回要求的队列，或者 LinkedBlockingQueue 队列
     */
    public static <T> BlockingQueue<T> createBlockingQueue(int type, Integer capacity) {
        final BlockingQueue<T> of = of(type, capacity);
        if (of != null) {
            return of;
        }

        return customOrDefaultQueue(capacity,
                (customeQueue) -> Objects.equals(customeQueue.getType(), type));
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
