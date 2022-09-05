---
sidebar_position: 3
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# 快速开始

## 服务启动

MySQL 创建名为 `hippo4j_manager` 的数据库，字符集选择 `utf8mb4`，并导入 [Hippo4J 初始化 SQL 语句](https://github.com/longtai-cn/hippo4j/blob/develop/hippo4j-server/conf/hippo4j_manager.sql)。

使用 Docker 运行服务端，可以灵活定制相关参数。如果 MySQL 非 Docker 部署，`MYSQL_HOST` 需要使用本地 IP。

```shell
docker run -d -p 6691:6691 --name hippo4j-server \
-e MYSQL_HOST=127.0.0.1 \
-e MYSQL_PORT=3306 \
-e MYSQL_DB=hippo4j_manager \
-e MYSQL_USERNAME=root \
-e MYSQL_PASSWORD=root \
hippo4j/hippo4j-server
```

> 如果没有 Docker，可以使用源码编译的方式，启动 [Hippo4J-Server](https://github.com/longtai-cn/hippo4j/tree/develop/hippo4j-server) 模块下 ServerApplication 应用类。

启动示例项目，[hippo4j-spring-boot-starter-example](https://github.com/opengoofy/hippo4j/tree/develop/hippo4j-example/hippo4j-spring-boot-starter-example) 模块下 Hippo4JServerExampleApplication 应用类。

访问 Server 控制台，路径 `http://localhost:6691/index.html`，默认用户名密码：admin / 123456

## 配置变更

访问控制台动态线程池菜单下线程池实例，修改动态线程池相关参数。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220813173811668.png)

观察 Hippo4j-Example 控制台日志输出，日志输出包括不限于此信息即为成功。

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
