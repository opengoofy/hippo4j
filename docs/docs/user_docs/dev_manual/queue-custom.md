---
sidebar_position: 4
---

# 阻塞队列自定义

Hippo4j 通过 SPI 的方式对拒绝策略进行扩展，可以让用户在 Hippo4j 中完成自定义阻塞队列实现。

## 1. 定义自定义队列类

实现接口 `cn.hippo4j.common.executor.support.CustomBlockingQueue<T>`：

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

## 2. 声明 SPI 文件

在 `src/main/resources/META-INF/services/` 目录下新增文件：

```
cn.hippo4j.common.executor.support.CustomBlockingQueue
```

文件内容仅一行：

```
com.example.queue.MyArrayBlockingQueue
```

## 3. 服务端生效方式

当服务端下发的 `queueType` 与 `capacity` 命中自定义类型时，框架会通过 SPI 自动创建队列。

### 3.1 队列创建与验证

```java
// 创建队列
BlockingQueue<T> q = BlockingQueueManager.createQueue(queueType, capacity);

// 验证队列配置
boolean valid = BlockingQueueManager.validateQueueConfig(queueType, capacity);

// 动态调整容量（仅 ResizableCapacityLinkedBlockingQueue 支持）
boolean ok = BlockingQueueManager.changeQueueCapacity(executor.getQueue(), newCapacity);
```

### 3.2 队列类型切换

当需要切换队列类型时，使用 `ThreadPoolRebuilder.rebuildAndSwitch` 方法，该方法会创建新的线程池实例并安全地迁移任务：

```java
boolean ok = ThreadPoolRebuilder.rebuildAndSwitch(
    executor,           // 当前线程池
    newQueueType,      // 新队列类型
    capacity,          // 队列容量
    threadPoolId       // 线程池ID
);
```

服务端动态刷新处的实现：

```java
// ServerThreadPoolDynamicRefresh#handleQueueChanges
boolean queueTypeChanged = parameter.getQueueType() != null && 
    !Objects.equals(BlockingQueueManager.getQueueType(executor.getQueue()), parameter.getQueueType());

if (queueTypeChanged) {
    // 使用安全的重建方式切换队列
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


