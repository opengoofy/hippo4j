package io.dynamic.threadpool.common.enums;

/**
 * 队列类型枚举
 *
 * @author chen.ma
 * @date 2021/6/25 12:30
 */
public enum QueueTypeEnum {

    /**
     * {@link java.util.concurrent.ArrayBlockingQueue}
     */
    ARRAY_BLOCKING_QUEUE(1),

    /**
     * {@link java.util.concurrent.LinkedBlockingQueue}
     */
    Linked_Blocking_QUEUE(2),

    /**
     * {@link java.util.concurrent.LinkedBlockingDeque}
     */
    Linked_Blocking_Deque(3),

    /**
     * {@link java.util.concurrent.SynchronousQueue}
     */
    SynchronousQueue(4),

    /**
     * {@link java.util.concurrent.LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE(5),

    /**
     * {@link java.util.concurrent.PriorityBlockingQueue}
     */
    PriorityBlockingQueue(6),

    /**
     * {@link "io.dynamic.threadpool.starter.core.ResizableCapacityLinkedBlockIngQueue"}
     */
    Resizable_LINKED_Blocking_QUEUE(9);

    public Integer type;

    QueueTypeEnum(int type) {
        this.type = type;
    }

}
