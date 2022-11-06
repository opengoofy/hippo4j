---
sidebar_position: 4
---

# 参数默认配置

曾有多名小伙伴反馈说，项目中线程池一多，配置文件中配置就显得很臃肿。为此 hippo4j-config 开发出了动态线程池默认配置。

```yaml
spring:
  dynamic:
    thread-pool:
      default-executor:
        core-pool-size: 4
        maximum-pool-size: 6
        blocking-queue: ResizableCapacityLinkedBlockingQueue
        queue-capacity: 1024
        execute-time-out: 1000
        keep-alive-time: 9999
        rejected-handler: AbortPolicy
        active-alarm: 90
        capacity-alarm: 85
        alarm: true
        allow-core-thread-time-out: true
        notify:
          interval: 5
          receives: chen.ma
      executors:
        - thread-pool-id: message-produce
        - thread-pool-id: message-consume
          core-pool-size: 80
          maximum-pool-size: 100
          execute-time-out: 1000
          notify:
            interval: 6
            receives: chen.ma
```

`spring.dynamic.thread-pool.executors` 层级下，仅需要配置 `thread-pool-id`，其余配置从 `spring.dynamic.thread-pool.default-executor` 读取。

如果 `spring.dynamic.thread-pool.executors` 下配置和 `spring.dynamic.thread-pool.default-executor` 冲突，以前者为主。

通过该自定义配置方式，可减少大量重复线程池参数配置项，提高核心配置简洁度。

提示：`spring.dynamic.thread-pool.default-executor` 层级下参数，不提供动态刷新功能。
