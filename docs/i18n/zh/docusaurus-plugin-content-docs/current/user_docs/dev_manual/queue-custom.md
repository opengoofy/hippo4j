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
// 创建队列 - 使用 BlockingQueueTypeEnum
BlockingQueue<T> q = BlockingQueueTypeEnum.createBlockingQueue(queueType, capacity);
// 或者通过队列名称创建
BlockingQueue<T> q2 = BlockingQueueTypeEnum.createBlockingQueue("ArrayBlockingQueue", capacity);

// 验证队列配置 - 使用 BlockingQueueManager
boolean valid = BlockingQueueManager.validateQueueConfig(queueType, capacity);

// 动态调整容量（仅 ResizableCapacityLinkedBlockingQueue 支持）- 使用 BlockingQueueManager
boolean ok = BlockingQueueManager.changeQueueCapacity(executor.getQueue(), newCapacity);
```

### 3.2 队列类型何时生效

**重要说明**：队列类型（queueType）的变更需要客户端应用重启后生效。

- **配置模板**：在线程池管理页面编辑队列类型，会保存到数据库，但不会推送到运行中的客户端。
- **生效时机**：客户端应用重启时，会从服务端读取最新配置，并使用反射替换线程池的 `workQueue` 字段。
- **运行时调整**：运行时仅支持队列容量的动态调整（仅限 `ResizableCapacityLinkedBlockingQueue`），不支持队列类型切换。

服务端动态刷新处的实现：

```java
// ServerThreadPoolDynamicRefresh#handleQueueChanges
// 仅支持容量调整，不支持队列类型切换
if (parameter.getCapacity() != null) {
    if (BlockingQueueManager.canChangeCapacity(executor.getQueue())) {
        boolean success = BlockingQueueManager.changeQueueCapacity(
            executor.getQueue(), parameter.getCapacity());
        if (success) {
            log.info("Queue capacity changed to: {}", parameter.getCapacity());
        }
    }
}
```


