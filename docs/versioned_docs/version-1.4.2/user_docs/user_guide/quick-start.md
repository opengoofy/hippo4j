---
sidebar_position: 3
---

# 快速开始

## 服务启动

使用 Docker 运行服务端，默认使用内置 H2 数据库，数据持久化到 Docker 容器存储卷中。

```shell
docker run -d -p 6691:6691 --name hippo4j-server hippo4j/hippo4j-server
```

> 如果没有 Docker，可以使用源码编译的方式，启动 [Hippo4j-Server/Hippo4j-Bootstrap](https://github.com/longtai-cn/hippo4j/tree/develop/hippo4j-server/hippo4j-bootstrap) 模块下 ServerApplication 应用类。

启动示例项目，[hippo4j-spring-boot-starter-example](https://github.com/opengoofy/hippo4j/tree/develop/hippo4j-example/hippo4j-spring-boot-starter-example) 模块下 ServerExampleApplication 应用类。

访问 Server 控制台，路径 `http://localhost:6691/index.html`，默认用户名密码：admin / 123456

## 配置变更

访问控制台动态线程池菜单下线程池实例，修改动态线程池相关参数。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220813173811668.png)

观察 Hippo4j-Example 控制台日志输出，日志输出包括不限于此信息即为成功。

```tex
2022-09-10 00:23:29.783  INFO 50322 --- [change.config_0] c.h.s.s.c.ServerThreadPoolDynamicRefresh : [message-consume] Dynamic thread pool change parameter.
    corePoolSize: 2 => 4
    maximumPoolSize: 6 => 12
    capacity: 1024 => 2048
    keepAliveTime: 9999 => 9999
    executeTimeOut: 800 => 3000
    rejectedType: SyncPutQueuePolicy => RunsOldestTaskPolicy
    allowCoreThreadTimeOut: true => true
```

另外，当 Client 集群部署时，可以修改某一个实例，或选择 `全部修改` 按钮，修改所有实例线程池信息。
