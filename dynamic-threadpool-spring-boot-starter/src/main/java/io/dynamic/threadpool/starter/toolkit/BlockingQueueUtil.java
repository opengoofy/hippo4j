package io.dynamic.threadpool.starter.toolkit;

import io.dynamic.threadpool.common.enums.QueueTypeEnum;
import io.dynamic.threadpool.starter.core.ResizableCapacityLinkedBlockIngQueue;

import java.util.Objects;
import java.util.concurrent.*;

/**
 * 阻塞队列工具类
 *
 * @author chen.ma
 * @date 2021/6/20 16:50
 */
public class BlockingQueueUtil {

    public static BlockingQueue createBlockingQueue(Integer type, Integer capacity) {
        BlockingQueue blockingQueue = null;
        if (Objects.equals(type, QueueTypeEnum.ARRAY_BLOCKING_QUEUE.type)) {
            blockingQueue = new ArrayBlockingQueue(capacity);
        } else if (Objects.equals(type, QueueTypeEnum.Linked_Blocking_QUEUE.type)) {
            blockingQueue = new LinkedBlockingQueue(capacity);
        } else if (Objects.equals(type, QueueTypeEnum.Linked_Blocking_Deque.type)) {
            blockingQueue = new LinkedBlockingDeque(capacity);
        } else if (Objects.equals(type, QueueTypeEnum.SynchronousQueue.type)) {
            blockingQueue = new SynchronousQueue();
        } else if (Objects.equals(type, QueueTypeEnum.LINKED_TRANSFER_QUEUE.type)) {
            blockingQueue = new LinkedTransferQueue();
        } else if (Objects.equals(type, QueueTypeEnum.PriorityBlockingQueue.type)) {
            blockingQueue = new PriorityBlockingQueue(capacity);
        } else if (Objects.equals(type, QueueTypeEnum.Resizable_LINKED_Blocking_QUEUE.type)) {
            blockingQueue = new ResizableCapacityLinkedBlockIngQueue(capacity);
        }
        return blockingQueue;
    }
}
