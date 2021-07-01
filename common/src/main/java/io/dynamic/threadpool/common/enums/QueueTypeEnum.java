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
    LINKED_BLOCKING_QUEUE(2),

    /**
     * {@link java.util.concurrent.LinkedBlockingDeque}
     */
    LINKED_BLOCKING_DEQUE(3),

    /**
     * {@link java.util.concurrent.SynchronousQueue}
     */
    SYNCHRONOUS_QUEUE(4),

    /**
     * {@link java.util.concurrent.LinkedTransferQueue}
     */
    LINKED_TRANSFER_QUEUE(5),

    /**
     * {@link java.util.concurrent.PriorityBlockingQueue}
     */
    PRIORITY_BLOCKING_QUEUE(6),

    /**
     * {@link "io.dynamic.threadpool.starter.core.ResizableCapacityLinkedBlockIngQueue"}
     */
    RESIZABLE_LINKED_BLOCKING_QUEUE(9);

    public Integer type;

    QueueTypeEnum(int type) {
        this.type = type;
    }

}
