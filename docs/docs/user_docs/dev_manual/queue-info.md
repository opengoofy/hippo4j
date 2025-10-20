---
sidebar_position: 3
---

# Built-in Blocking Queues

Hippo4j provides multiple built-in blocking queue types that are ready to use out of the box. You can also extend custom queue types through SPI.

## Built-in Queue Types

The following types can be directly selected in the server or configuration (Enum: `BlockingQueueTypeEnum`):

- ArrayBlockingQueue (bounded array-based queue)
- LinkedBlockingQueue (linked list queue)
- LinkedBlockingDeque (double-ended queue)
- SynchronousQueue (synchronous handoff queue)
- LinkedTransferQueue (transferable queue)
- PriorityBlockingQueue (priority queue)
- ResizableCapacityLinkedBlockingQueue (dynamically resizable linked list queue)

Among them, `ResizableCapacityLinkedBlockingQueue` supports online capacity changes without rebuilding the thread pool, making it suitable for dynamic tuning scenarios.

## Code Reference

Enum definition:

```java
// cn.hippo4j.common.executor.support.BlockingQueueTypeEnum
RESIZABLE_LINKED_BLOCKING_QUEUE(9, "ResizableCapacityLinkedBlockingQueue") {
    @Override
    <T> BlockingQueue<T> of(Integer capacity) {
        return new ResizableCapacityLinkedBlockingQueue<>(capacity);
    }
    @Override
    <T> BlockingQueue<T> of() {
        return new ResizableCapacityLinkedBlockingQueue<>();
    }
}
```

Creation and validation:

```java
// cn.hippo4j.common.executor.support.BlockingQueueManager
BlockingQueue<T> q = BlockingQueueManager.createQueue(queueType, capacity);
boolean valid = BlockingQueueManager.validateQueueConfig(queueType, capacity);
boolean ok = BlockingQueueManager.changeQueueCapacity(executor.getQueue(), newCapacity);
```

## Usage Recommendations

- Need online capacity adjustment: prioritize `ResizableCapacityLinkedBlockingQueue`
- Need strictly bounded: choose `ArrayBlockingQueue`
- Need unbounded throughput: choose `LinkedBlockingQueue`
- Need priority: choose `PriorityBlockingQueue`
- Need synchronous handoff: choose `SynchronousQueue`

For custom queue types, please refer to "Custom Blocking Queue".

