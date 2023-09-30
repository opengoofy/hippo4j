---
sidebar_position: 2
---

# 架构设计

简单来说，Hippo4j 从部署的角度上分为两种角色：Server 端和 Client 端。

Server 端是 Hippo4j 项目打包出的 Java 进程，功能包括用户权限、线程池监控以及执行持久化的动作。

Client 端指的是我们 SpringBoot 应用，通过引入 Hippo4j Starter Jar 包负责与 Server 端进行交互。

比如拉取 Server 端线程池数据、动态更新线程池配置以及采集上报线程池运行时数据等。

## 基础组件

### 配置中心（Config）

配置中心位于 Server 端，它的主要作用是监控 Server 端线程池配置变更，实时通知到 Client 实例执行线程池变更流程。

代码设计基于 Nacos 1.x 版本的 **长轮询以及异步 Servlet 机制** 实现。

### 注册中心（Discovery）

负责管理 Client 端（单机或集群）注册到 Server 端的实例，包括不限于**实例注册、续约、过期剔除** 等操作，代码基于 Eureka 源码实现。

上面的配置中心很容易理解，动态线程池参数变更的根本。但是注册中心是用来做什么的？

注册中心管理 Client 端注册的实例，通过这些实例可以 **实时获取线程池的运行时参数信息**。

目前的设计是如此，不排除后续基于 Discovery 做更多的扩展。

### 控制台（Console）

对接前端项目，包括不限于以下模块管理：

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211107122504126.png)

## 消息通知（Notify）

Hippo4j 内置了很多需要通知的事件，比如：线程池参数变更通知、线程池活跃度报警、拒绝策略执行报警以及阻塞队列容量报警等。

目前 Notify 已经接入了钉钉、企业微信和飞书，后续持续集成邮件、短信等通知渠道；并且，Notify 模块提供了消息事件的 SPI 方案，可以接受三方自定义的推送。

## Hippo4j-Spring-Boot-Starter

熟悉 SpringBoot 的小伙伴对 Starter 应该不会陌生。Hippo4j 提供以 Starter Jar 包的形式嵌套在应用内，负责与 Server 端完成交互。

## 功能架构

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211105230953626.png)
