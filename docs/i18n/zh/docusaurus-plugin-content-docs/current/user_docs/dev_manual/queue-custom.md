---
sidebar_position: 4
---

# 阻塞队列自定义

Hippo4j 通过 SPI 的方式对拒绝策略进行扩展，可以让用户在 Hippo4j 中完成自定义阻塞队列实现。

## 1. 定义自定义队列类

实现接口 `cn.hippo4j.common.executor.support.CustomBlockingQueue<T>`：

```java
package com.example.queue;

import cn.hippo4j.common.executor.support.CustomBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

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

当服务端下发的 `queueType` 与 `capacity` 命中自定义类型时，框架会通过 SPI 自动创建队列：

```java
// 创建与验证
BlockingQueue<T> q = BlockingQueueManager.createQueue(queueType, capacity);
boolean valid = BlockingQueueManager.validateQueueConfig(queueType, capacity);

// 在线替换
boolean swapped = BlockingQueueManager.replaceQueue(executor, q);

// 动态调整容量（仅 ResizableCapacityLinkedBlockingQueue 支持）
boolean ok = BlockingQueueManager.changeQueueCapacity(executor.getQueue(), newCapacity);
```

服务端动态刷新处：

```java
// ServerThreadPoolDynamicRefresh#handleQueueChanges
boolean queueTypeChanged = parameter.getQueueType() != null && 
    !Objects.equals(BlockingQueueManager.getQueueType(executor.getQueue()), parameter.getQueueType());

if (queueTypeChanged) {
    boolean swapped = BlockingQueueManager.replaceQueue(
        executor, BlockingQueueManager.createQueue(parameter.getQueueType(), parameter.getCapacity()));
    ...
}
```


