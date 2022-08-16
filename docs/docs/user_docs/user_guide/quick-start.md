---
sidebar_position: 3
---

# 快速开始

:::tip
Hippo4J 支持两种运行模式，依赖配置中心（Hippo4J-Core）或 Hippo4J Server，下文描述接入 Hippo4J
Server，[Hippo4J-Core 接入参考此处](/docs/user_docs/getting-started/hippo4j-core-start.md) 。
:::

## 如何运行 Demo

Clone Hippo4J [源代码](https://github.com/longtai-cn/hippo4j)，导入初始化 SQL 语句并运行示例程序。

1. 导入 [Hippo4J 初始化 SQL 语句](https://github.com/longtai-cn/hippo4j/blob/develop/hippo4j-server/conf/hippo4j_manager.sql)；
2. 启动 [Hippo4J-Server](https://github.com/longtai-cn/hippo4j/tree/develop/hippo4j-server) 模块下 ServerApplication 应用类；
3. 启动 [hippo4J-spring-boot-starter-example](https://github.com/opengoofy/hippo4j/tree/develop/hippo4j-example/hippo4j-spring-boot-starter-example) 模块下 Hippo4JServerExampleApplication 应用类。

通过 Server 控制台访问，路径：`http://localhost:6691/index.html#/hippo4j/dynamic/thread-pool/instance`。

默认用户名密码：admin / 123456

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220813173811668.png)

修改相关参数， 观察 Hippo4j-Example 控制台日志输出，日志输出包括不限于此信息即为成功。

```tex
2022-08-13 21:26:25.814  INFO 38972 --- [change.config-5] c.h.s.s.c.ServerThreadPoolDynamicRefresh : [message-consume] Dynamic thread pool change parameter.
    corePoolSize: [5 => 5]
    maximumPoolSize: [6 => 7]
    capacity: [10 => 10]
    keepAliveTime: [3 => 3]
    executeTimeOut: [0 => 0]
    rejectedType: [CustomErrorLogRejectedExecutionHandler => CustomErrorLogRejectedExecutionHandler]
    allowCoreThreadTimeOut: [false => false]
```

另外，当 Client 集群部署时，可以修改某一个实例，或选择 `全部修改` 按钮，修改所有实例线程池信息。

线程池参数动态变更通知，或线程池运行时报警，详情参考 [通知报警](/docs/user_docs/user_guide/alarm.md)。
