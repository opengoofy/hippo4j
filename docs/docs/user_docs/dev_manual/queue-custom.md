---
sidebar_position: 4
---

# Custom Blocking Queue

Hippo4j extends blocking queues through SPI, allowing users to implement custom blocking queue types in Hippo4j.

## 1. Define Custom Queue Class

Implement the interface `cn.hippo4j.common.executor.support.CustomBlockingQueue<T>`:

```java
public class MyArrayBlockingQueue implements CustomBlockingQueue<Runnable> {

    @Override
    public Integer getType() {
        return 1001; 
    }

    @Override
    public String getName() {
        return "MyArrayBlockingQueue"; 
    }

    @Override
    public BlockingQueue<Runnable> generateBlockingQueue() {
        return new ArrayBlockingQueue<>(256);
    }
}
```

## 2. Declare SPI File

Create a file in the `src/main/resources/META-INF/services/` directory:

```
cn.hippo4j.common.executor.support.CustomBlockingQueue
```

File content (single line):

```
com.example.queue.MyArrayBlockingQueue
```

## 3. Server-side Activation

When the `queueType` and `capacity` delivered by the server match the custom type, the framework will automatically create the queue through SPI.

### 3.1 Queue Creation and Validation

```java
// Create queue
BlockingQueue<T> q = BlockingQueueManager.createQueue(queueType, capacity);

// Validate queue configuration
boolean valid = BlockingQueueManager.validateQueueConfig(queueType, capacity);

// Dynamic capacity adjustment (only supported by ResizableCapacityLinkedBlockingQueue)
boolean ok = BlockingQueueManager.changeQueueCapacity(executor.getQueue(), newCapacity);
```

### 3.2 Queue Type Switching

When you need to switch queue types, use the `ThreadPoolRebuilder.rebuildAndSwitch` method, which creates a new thread pool instance and safely migrates tasks:

```java
boolean ok = ThreadPoolRebuilder.rebuildAndSwitch(
    executor,           // Current thread pool
    newQueueType,      // New queue type
    capacity,          // Queue capacity
    threadPoolId       // Thread pool ID
);
```

Server-side dynamic refresh implementation:

```java
// ServerThreadPoolDynamicRefresh#handleQueueChanges
boolean queueTypeChanged = parameter.getQueueType() != null && 
    !Objects.equals(BlockingQueueManager.getQueueType(executor.getQueue()), parameter.getQueueType());

if (queueTypeChanged) {
    // Use safe rebuild approach for queue switching
    boolean ok = ThreadPoolRebuilder.rebuildAndSwitch(
        executor,
        parameter.getQueueType(),
        parameter.getCapacity(),
        threadPoolId
    );
    if (ok) {
        log.info("Queue type rebuilt and switched to: {}", 
                 BlockingQueueTypeEnum.getBlockingQueueNameByType(parameter.getQueueType()));
    }
}
```


