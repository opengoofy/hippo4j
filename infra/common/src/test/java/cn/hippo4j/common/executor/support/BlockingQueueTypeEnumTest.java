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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * test for {@link BlockingQueueTypeEnum}
 */
public final class BlockingQueueTypeEnumTest {

    // get all blocking queue names
    private static final List<String> BLOCKING_QUEUE_NAMES = Arrays.stream(BlockingQueueTypeEnum.values()).map(BlockingQueueTypeEnum::getName).collect(Collectors.toList());

    @Test
    void testGetType() {
        Assertions.assertEquals(1, BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE.getType());
        Assertions.assertEquals(2, BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE.getType());
        Assertions.assertEquals(3, BlockingQueueTypeEnum.LINKED_BLOCKING_DEQUE.getType());
        Assertions.assertEquals(4, BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE.getType());
        Assertions.assertEquals(5, BlockingQueueTypeEnum.LINKED_TRANSFER_QUEUE.getType());
        Assertions.assertEquals(6, BlockingQueueTypeEnum.PRIORITY_BLOCKING_QUEUE.getType());
        Assertions.assertEquals(9, BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getType());
    }

    @Test
    void testGetName() {
        Assertions.assertEquals("ArrayBlockingQueue", BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE.getName());
        Assertions.assertEquals("LinkedBlockingQueue", BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE.getName());
        Assertions.assertEquals("LinkedBlockingDeque", BlockingQueueTypeEnum.LINKED_BLOCKING_DEQUE.getName());
        Assertions.assertEquals("SynchronousQueue", BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE.getName());
        Assertions.assertEquals("LinkedTransferQueue", BlockingQueueTypeEnum.LINKED_TRANSFER_QUEUE.getName());
        Assertions.assertEquals("PriorityBlockingQueue", BlockingQueueTypeEnum.PRIORITY_BLOCKING_QUEUE.getName());
        Assertions.assertEquals("ResizableCapacityLinkedBlockingQueue", BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getName());
    }

    @Test
    void testValues() {
        Assertions.assertNotNull(BlockingQueueTypeEnum.values());
    }

    @Test
    void testValueOf() {
        Assertions.assertEquals(BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE, BlockingQueueTypeEnum.valueOf("ARRAY_BLOCKING_QUEUE"));
        Assertions.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.valueOf("LINKED_BLOCKING_QUEUE"));
        Assertions.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_DEQUE, BlockingQueueTypeEnum.valueOf("LINKED_BLOCKING_DEQUE"));
        Assertions.assertEquals(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE, BlockingQueueTypeEnum.valueOf("SYNCHRONOUS_QUEUE"));
        Assertions.assertEquals(BlockingQueueTypeEnum.LINKED_TRANSFER_QUEUE, BlockingQueueTypeEnum.valueOf("LINKED_TRANSFER_QUEUE"));
        Assertions.assertEquals(BlockingQueueTypeEnum.PRIORITY_BLOCKING_QUEUE, BlockingQueueTypeEnum.valueOf("PRIORITY_BLOCKING_QUEUE"));
        Assertions.assertEquals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.valueOf("RESIZABLE_LINKED_BLOCKING_QUEUE"));
    }

    @Test
    void testCreateBlockingQueue() {
        // check legal param: name and capacity
        for (String name : BLOCKING_QUEUE_NAMES) {
            BlockingQueue<Object> blockingQueueByName = BlockingQueueTypeEnum.createBlockingQueue(name, 10);
            Assertions.assertNotNull(blockingQueueByName);
        }
        // check illegal null name
        Assertions.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(null, 10));
        // check nonexistent name
        Assertions.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue("ABC", 10));
        // check illegal null capacity
        for (String name : BLOCKING_QUEUE_NAMES) {
            Assertions.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(name, null));
        }
        // check illegal negatives capacity
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> BlockingQueueTypeEnum.createBlockingQueue(BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE.getName(), -100)
        );
        // check normal type
        Stream.of(1, 2, 3, 4, 5, 6, 9, 100, -1, 0).forEach(each ->
                Assertions.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(each, null)));
        // check illegal name and capacity
        Assertions.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue("HelloWorld", null));
        Assertions.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(null, null));
    }

    @Test
    void testGetBlockingQueueNameByType() {
        // check legal range of type
        Assertions.assertEquals("ArrayBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(1));
        Assertions.assertEquals("LinkedBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(2));
        Assertions.assertEquals("LinkedBlockingDeque", BlockingQueueTypeEnum.getBlockingQueueNameByType(3));
        Assertions.assertEquals("SynchronousQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(4));
        Assertions.assertEquals("LinkedTransferQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(5));
        Assertions.assertEquals("PriorityBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(6));
        Assertions.assertEquals("ResizableCapacityLinkedBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(9));
        // check illegal range of type
        Assertions.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(0));
        Assertions.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(-1));
        Assertions.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(100));
    }

    @Test
    void testGetBlockingQueueTypeEnumByName() {
        // check legal range of name
        Assertions.assertEquals(BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("ArrayBlockingQueue"));
        Assertions.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedBlockingQueue"));
        Assertions.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_DEQUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedBlockingDeque"));
        Assertions.assertEquals(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("SynchronousQueue"));
        Assertions.assertEquals(BlockingQueueTypeEnum.LINKED_TRANSFER_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedTransferQueue"));
        Assertions.assertEquals(BlockingQueueTypeEnum.PRIORITY_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("PriorityBlockingQueue"));
        Assertions.assertEquals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("ResizableCapacityLinkedBlockingQueue"));
        // check illegal range of name
        Assertions.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("Hello"));
        Assertions.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(null));
    }
}