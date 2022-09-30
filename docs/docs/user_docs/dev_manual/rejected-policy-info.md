---
sidebar_position: 0
---

# 内置拒绝策略

内置两种拒绝策略说明：

**RunsOldestTaskPolicy**：添加新任务并由主线程运行最早的任务。

```java
public class RunsOldestTaskPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }
        BlockingQueue<Runnable> workQueue = executor.getQueue();
        Runnable firstWork = workQueue.poll();
        boolean newTaskAdd = workQueue.offer(r);
        if (firstWork != null) {
            firstWork.run();
        }
        if (!newTaskAdd) {
            executor.execute(r);
        }
    }
}
```

**SyncPutQueuePolicy**：主线程把拒绝任务以阻塞的方式添加到队列。

```java
@Slf4j
public class SyncPutQueuePolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            log.error("Adding Queue task to thread pool failed.", e);
        }
    }
}
```