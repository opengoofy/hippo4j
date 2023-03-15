package cn.hippo4j.common.executor.support;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;


class BlockingQueueTypeEnumTest {
    //get all blocking queue names
    private static final List<String> BLOCKING_QUEUE_NAMES = Arrays.stream(BlockingQueueTypeEnum.values()).map(BlockingQueueTypeEnum::getName).collect(Collectors.toList());

    @Test
    void assertCreateBlockingQueueNormal() {
        //check legal param: name and capacity
        for (String name : BLOCKING_QUEUE_NAMES) {
            BlockingQueue<Object> blockingQueueByName = BlockingQueueTypeEnum.createBlockingQueue(name, 10);
            Assert.assertNotNull(blockingQueueByName);
        }
    }

    @Test
    void assertCreateBlockingQueueWithIllegalName() {
        //check illegal null name
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(null, 10));
        //check unexistent name
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue("ABC", 10));
    }

    @Test
    void assertCreateBlockingQueueWithIllegalCapacity() {
        //check illegal null capacity
        for (String name : BLOCKING_QUEUE_NAMES) {
            BlockingQueue<Object> blockingQueueWithNullCapacity = BlockingQueueTypeEnum.createBlockingQueue(name, null);
            Assert.assertNotNull(blockingQueueWithNullCapacity);
        }
        //check illegal negatives capacity
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
    void assertCreateBlockingQueueWithIllegalParams() {
        //check illegal name and capacity
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue("HelloWorld", null));
        Assert.assertNotNull(BlockingQueueTypeEnum.createBlockingQueue(null, null));
    }

    @Test
    void assertCreateBlockingQueueWithType() {

    }

    @Test
    void assertGetBlockingQueueNameByType() {
        //check legal range of type
        Assert.assertEquals("ArrayBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(1));
        Assert.assertEquals("LinkedBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(2));
        Assert.assertEquals("LinkedBlockingDeque", BlockingQueueTypeEnum.getBlockingQueueNameByType(3));
        Assert.assertEquals("SynchronousQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(4));
        Assert.assertEquals("LinkedTransferQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(5));
        Assert.assertEquals("PriorityBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(6));
        Assert.assertEquals("ResizableCapacityLinkedBlockingQueue", BlockingQueueTypeEnum.getBlockingQueueNameByType(9));
        //check illegal range of type
        Assert.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(0));
        Assert.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(-1));
        Assert.assertEquals("", BlockingQueueTypeEnum.getBlockingQueueNameByType(100));
    }

    @Test
    void assertGetBlockingQueueTypeEnumByName() {
        //check legal range of name
        Assert.assertEquals(BlockingQueueTypeEnum.ARRAY_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("ArrayBlockingQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedBlockingQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_DEQUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedBlockingDeque"));
        Assert.assertEquals(BlockingQueueTypeEnum.SYNCHRONOUS_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("SynchronousQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_TRANSFER_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedTransferQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.PRIORITY_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("PriorityBlockingQueue"));
        Assert.assertEquals(BlockingQueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("ResizableCapacityLinkedBlockingQueue"));
        //check illegal range of name
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("Hello"));
        Assert.assertEquals(BlockingQueueTypeEnum.LINKED_BLOCKING_QUEUE, BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(null));
    }
}