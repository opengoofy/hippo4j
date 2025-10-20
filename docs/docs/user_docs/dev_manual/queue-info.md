---
sidebar_position: 3
---

# 内置阻塞队列

Hippo4j 内置多种常用阻塞队列类型，支持开箱即用，亦可通过 SPI 扩展自定义队列类型。

## 内置类型清单

以下类型可直接在服务端或配置中选择（枚举：`BlockingQueueTypeEnum`）：

- ArrayBlockingQueue（数组有界队列）
- LinkedBlockingQueue（链表队列）
- LinkedBlockingDeque（双端队列）
- SynchronousQueue（同步移交队列）
- LinkedTransferQueue（可转移队列）
- PriorityBlockingQueue（优先级队列）
- ResizableCapacityLinkedBlockingQueue（可在线动态调容量的链表队列）

其中 `ResizableCapacityLinkedBlockingQueue` 支持在线变更 `capacity`，无需重建线程池，适合动态调优场景。

## 代码对应

枚举定义：

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

创建与验证：

```java
// cn.hippo4j.common.executor.support.BlockingQueueManager
BlockingQueue<T> q = BlockingQueueManager.createQueue(queueType, capacity);
boolean valid = BlockingQueueManager.validateQueueConfig(queueType, capacity);
boolean ok = BlockingQueueManager.changeQueueCapacity(executor.getQueue(), newCapacity);
```

## 使用建议

- 需要在线调容量：优先选择 `ResizableCapacityLinkedBlockingQueue`
- 需要严格有界：选择 `ArrayBlockingQueue`
- 需要无界吞吐：选择 `LinkedBlockingQueue`
- 需要优先级：选择 `PriorityBlockingQueue`
- 需要同步移交：选择 `SynchronousQueue`

如需自定义队列类型，请参考《阻塞队列自定义》。


