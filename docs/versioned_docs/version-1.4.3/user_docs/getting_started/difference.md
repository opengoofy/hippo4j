---
sidebar_position: 0
---

# 运行模式介绍

1.1.0 版本发布后，Hippo4j 分为两种使用模式：轻量级依赖配置中心以及无中间件依赖版本。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220319154626314.png)

### hippo4j-config

**轻量级动态线程池管理**，依赖 Nacos、Apollo、Zookeeper、ETCD、Polaris 等三方配置中心（任选其一）完成线程池参数动态变更，支持运行时报警、监控等功能。

> 监控功能配置详见：[线程池监控](/docs/user_docs/getting_started/config/hippo4j-config-monitor)

![](https://images-machen.oss-cn-beijing.aliyuncs.com/20220814_hippo4j_monitor.jpg)

### hippo4j-server

**部署 hippo4j-server 服务**，通过可视化 Web 界面完成线程池的创建、变更以及查看，不依赖三方中间件。

相比较 hippo4j-config，功能会更强大，但同时也引入了一定的复杂性。需要部署一个 Java 服务，以及依赖 MySQL 数据库。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/1644032018254-min.gif)

### 使用总结

|      | hippo4j-config                                 | hippo4j-server                                              |
| ---- | ---------------------------------------------------- | ------------------------------------------------------------ |
| 依赖 | Nacos、Apollo、Zookeeper、ETCD、Polaris 配置中心（任选其一） | 部署 Hippo4j Server（内部无依赖中间件） |
| 使用 | 配置中心补充线程池相关参数                 | Hippo4j Server Web 控制台添加线程池记录                                                         |
| 功能 | 包含基础功能：参数动态化、运行时监控、报警等         | 基础功能之外扩展控制台界面、线程池堆栈查看、线程池运行信息实时查看、历史运行信息查看、线程池配置集群个性化等 |

使用建议：根据公司情况选择，如果基本功能可以满足使用，选择 hippo4j-config 使用即可；如果希望更多的功能，可以选择 hippo4j-server。

**两者在进行替换的时候，无需修改业务代码**。
