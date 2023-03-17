---
sidebar_position: 1
---

# 简介

## 线程池痛点

线程池是一种基于池化思想管理线程的工具，使用线程池可以减少创建销毁线程的开销，避免线程过多导致系统资源耗尽。

在高并发以及大批量的任务处理场景，线程池的使用是必不可少的。

如果有在项目中实际使用线程池，相信你可能会遇到以下痛点：

- 线程池随便定义，线程资源过多，造成服务器高负载。

- 线程池参数不易评估，随着业务的并发提升，业务面临出现故障的风险。
- 线程池任务执行时间超过平均执行周期，开发人员无法感知。
- 线程池任务堆积，触发拒绝策略，影响既有业务正常运行。
- 当业务出现超时、熔断等问题时，因为没有监控，无法确定是不是线程池引起。
- 原生线程池不支持运行时变量的传递，比如 MDC 上下文遇到线程池就 GG。
- 无法执行优雅关闭，当项目关闭时，大量正在运行的线程池任务被丢弃。
- 线程池运行中，任务执行停止，怀疑发生死锁或执行耗时操作，但是无从下手。

## 什么是 Hippo4j

Hippo4j 通过对 JDK 线程池增强，以及扩展三方框架底层线程池等功能，为业务系统提高线上运行保障能力。

提供以下功能支持：

- 全局管控 - 管理应用线程池实例。

- 动态变更 - 应用运行时动态变更线程池参数，包括不限于：核心、最大线程数、阻塞队列容量、拒绝策略等。
- 通知报警 - 内置四种报警通知策略，线程池活跃度、容量水位、拒绝策略以及任务执行时间超长。
- 运行监控 - 实时查看线程池运行时数据，最近半小时线程池运行数据图表展示。
- 功能扩展 - 支持线程池任务传递上下文；项目关闭时，支持等待线程池在指定时间内完成任务。
- 多种模式 - 内置两种使用模式：[依赖配置中心](https://hippo4j.cn/docs/user_docs/getting_started/config/hippo4j-config-start) 和 [无中间件依赖](https://hippo4j.cn/docs/user_docs/getting_started/server/hippo4j-server-start)。
- 容器管理 - Tomcat、Jetty、Undertow 容器线程池运行时查看和线程数变更。
- 框架适配 - Dubbo、Hystrix、RabbitMQ、RocketMQ 等消费线程池运行时数据查看和线程数变更。

## 快速开始

对于本地演示目的，请参阅 [Quick start](https://hippo4j.cn/docs/user_docs/user_guide/quick-start)

演示环境： [http://console.hippo4j.cn/index.html](http://console.hippo4j.cn/index.html)

## 接入登记

更多接入的公司，欢迎在 [登记地址](https://github.com/opengoofy/hippo4j/issues/13) 登记，登记仅仅为了产品推广。

## 联系我

开源不易，右上角点个 Star 鼓励一下吧！

如果大家想要实时关注 Hippo4j 更新的文章以及分享的干货的话，可以关注我的公众号。

使用过程中有任何问题，或者对项目有什么建议，关注公众号回复：加群，和 1000+ 志同道合的朋友交流讨论。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20230317191041262-mini.png)

## 友情链接

- [[ LiteFlow ]](https://liteflow.yomahub.com/)：轻量，快速，稳定可编排的组件式规则引擎。

- [[ Sa-Token ]](https://github.com/dromara/sa-token)：一个轻量级 java 权限认证框架，让鉴权变得简单、优雅！
- [[ HertzBeat ]](https://github.com/dromara/hertzbeat)：易用友好的云监控系统, 无需 Agent, 强大自定义监控能力。
- [[ JavaGuide ]](https://github.com/Snailclimb/JavaGuide)：一份涵盖大部分 Java 程序员所需要掌握的核心知识。
- [[ toBeBetterJavaer ]](https://github.com/itwanger/toBeBetterJavaer)：一份通俗易懂、风趣幽默的 Java 学习指南。

## 贡献者

感谢所有为项目作出贡献的开发者。如果有意贡献，参考 [good first issue](https://github.com/opengoofy/hippo4j/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22)。
