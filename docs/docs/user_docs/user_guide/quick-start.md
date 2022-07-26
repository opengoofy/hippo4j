---
sidebar_position: 3
---

# 快速开始

:::tip
Hippo4J 支持两种运行模式，依赖配置中心（Hippo4J-Core）或 Hippo4J Server，下文描述接入 Hippo4J Server，[Hippo4J-Core 接入参考此处](/docs/user_docs/getting-started/hippo4j-core-start.md) 。
:::

## 如何运行 Demo

Clone Hippo4J [源代码](https://github.com/longtai-cn/hippo4j)，导入初始化 SQL 语句并运行示例程序。

1. 导入 [Hippo4J 初始化 SQL 语句](https://github.com/longtai-cn/hippo4j/blob/develop/hippo4j-server/conf/hippo4j_manager.sql)；
2. 启动 [Hippo4J-Server](https://github.com/longtai-cn/hippo4j/tree/develop/hippo4j-server) 模块下 ServerApplication 应用类；
3. 启动 [Hippo4J-spring-boot-starter-example](https://github.com/opengoofy/hippo4j/tree/develop/hippo4j-example/hippo4j-spring-boot-starter-example) 模块下 Hippo4JServerExampleApplication 应用类；


通过接口修改线程池中的配置。HTTP POST 路径：`http://localhost:6691/hippo4j/v1/cs/configs`，Body 请求体如下：

```json
{
    "ignore": "tenantId、itemId、tpId 代表唯一线程池，请不要修改",
    "tenantId": "prescription",
    "itemId": "dynamic-threadpool-example",
    "tpId": "message-produce",
    "coreSize": 10,
    "maxSize": 15,
    "queueType": 9,
    "capacity": 100,
    "keepAliveTime": 10,
    "rejectedType": 3,
    "isAlarm": 0,
    "capacityAlarm": 90,
    "livenessAlarm": 90
}
```


接口调用成功后，观察 Hippo4j-Example 控制台日志输出，日志输出包括不限于此信息即为成功。

```tex
[🔥 MESSAGE-PRODUCE] Changed thread pool. 
coreSize :: [2 => 10], maxSize :: [10 => 15], queueType :: [ArrayBlockingQueue => ResizableCapacityLinkedBlockIngQueue], capacity :: [200 => 200], keepAliveTime :: [25 => 10], rejectedType :: [AbortPolicy => DiscardPolicy]
```

:::tip
也可以通过 Server 控制台访问，路径：`http://localhost:6691/index.html`。

默认用户名密码：admin / 123456
:::


另外，当 Client 集群部署时，可以选择修改所有实例或某一实例。

修改请求路径：`http://localhost:6691/hippo4j/v1/cs/configs?identify=xxx`，Body 体同上。

`identify`：代表客户端唯一标识，参数不传或为空，会修改该线程池 Client 集群下所有线程池实例参数。

线程池参数动态变更通知，或线程池运行时报警，详情参考 [通知报警](/docs/user_docs/user_guide/alarm.md)。

