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

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * BlockingQueueTypeEnum test class
 */
public final class BlockingQueueTypeEnumTest {

    // get all blocking queue names
    private static final List<String> BLOCKING_QUEUE_NAMES = Arrays.stream(BlockingQueueTypeEnum.values()).map(BlockingQueueTypeEnum::getName).collect(Collectors.toList());

    @Test
    public void testAssertCreateBlockingQueueNormal() {
        // check legal param: name and capacity
        for (String name : BLOCKING_QUEUE_NAMES) {
            BlockingQueue<Object> blockingQueueByName = BlockingQueueTypeEnum.createBlockingQueue(name, 10);
            Assert.assertNotNull(blockingQueueByName);
        }
    }

    @Test
    public void testAssertCreateBlockingQueueWithIllegalName() {
        // check illegal null name
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(null, 10));
        // check unexistent name
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue("ABC", 10));
    }

    @Test
    public void testAssertCreateBlockingQueueWithIllegalCapacity() {
        // check illegal null capacity
        for (String name : BLOCKING_QUEUE_NAMES) {
            BlockingQueue<Object> blockingQueueWithNullCapacity = BlockingQueueTypeEnum.createBlockingQueue(name, null);
            Assert.assertNotNull(blockingQueueWithNullCapacity);
        }
        // check illegal negatives capacity
        final String arrayBlockingQueueName = BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE.getName();
        Assert.assertThrows(IllegalArgumentException.class, () -> BlockingQueueTypeEnum.createBlockingQueue(arrayBlockingQueueName, -100));

        final String linkedBlockingQueueName = BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE.getName();
        Assert.assertThrows(IllegalArgumentException.class, () -> BlockingQueueTypeEnum.createBlockingQueue(linkedBlockingQueueName, -100));

        final String linkedBlockingDequeName = BlockingQueueTypeEnum.LINKED_BLOCKING_DEQUE.getName();
        Assert.assertThrows(IllegalArgumentException.class, () -> BlockingQueueTypeEnum.createBlockingQueue(linkedBlockingDequeName, -100));

        final String synchronousQueueName = BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE.getName();
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(synchronousQueueName, -99));

        final String linkedTransferQueueName = BlockingQueueTypeEnum.LINKED_TRANSFER_QUEUE.getName();
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(linkedTransferQueueName, -0));

        final String priorityBlockingQueueName = BlockingQueueTypeEnum.PRIORITY_BLOCKING_QUEUE.getName();
        Assert.assertThrows(IllegalArgumentException.class, () -> BlockingQueueTypeEnum.createBlockingQueue(priorityBlockingQueueName, -100));

        final String resizableLinkedBlockingQueueName = BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.getName();
        Assert.assertThrows(IllegalArgumentException.class, () -> BlockingQueueTypeEnum.createBlockingQueue(resizableLinkedBlockingQueueName, -100));
    }

    @Test
    public void testAssertCreateBlockingQueueWithIllegalParams() {
        // check illegal name and capacity
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue("HelloWorld", null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(null, null));
    }

    @Test
    public void testAssertCreateBlockingQueueWithType() {
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(1, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(2, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(3, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(4, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(5, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(6, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(9, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(100, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(-1, null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(0, null));
    }

    @Test
    public void testAssertGetBlockingQueueNameByType() {
        // check legal range of type
        Assert.assertEquals("ArrayBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(1));
        Assert.assertEquals("LinkedBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(2));
        Assert.assertEquals("LinkedBlockingDeque", BlockingQueueTypeEnum.getBlockingQueueNameByType(3));
        Assert.assertEquals("SynchronousQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(4));
        Assert.assertEquals("LinkedTransferQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(5));
        Assert.assertEquals("PriorityBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(6));
        Assert.assertEquals("ResizableCapacityLinkedBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(9));
        // check illegal range of type
        Assert.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(0));
        Assert.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(-1));
        Assert.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(100));
    }

    @Test
    public void testAssertGetBlockingQueueTypeEnumByName() {
        // check legal range of name
        Assert.assertEquals(BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("ArrayBlockingQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedBlockingQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_DEQUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedBlockingDeque"));
        Assert.assertEquals(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("SynchronousQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_TRANSFER_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedTransferQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.PRIORITY_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("PriorityBlockingQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("ResizableCapacityLinkedBlockingQueue"));
        // check illegal range of name
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("Hello"));
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(null));
    }
}