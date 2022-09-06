<img align="center" width="400" alt="image" src="https://user-images.githubusercontent.com/77398366/181906454-b46f6a14-7c2c-4b8f-8b0a-40432521bed8.png">

# 动态可观测线程池框架，提高线上运行保障能力

[![Gitee](https://gitee.com/agentart/hippo4j/badge/star.svg?theme=gvp)](https://gitee.com/agentart/hippo4j) [![GitHub](https://img.shields.io/github/stars/opengoofy/hippo4j)](https://github.com/opengoofy/hippo4j) [![Docker Pulls](https://img.shields.io/docker/pulls/hippo4j/hippo4j-server.svg)](https://store.docker.com/community/images/hippo4j/hippo4j-server) [![Contributors](https://img.shields.io/github/contributors/opengoofy/hippo4j?color=3ba272)](https://github.com/opengoofy/hippo4j/graphs/contributors) [![License](https://img.shields.io/github/license/opengoofy/hippo4j?color=5470c6)](https://github.com/opengoofy/hippo4j/blob/develop/LICENSE)

-------

## 线程池痛点

线程池是一种基于池化思想管理线程的工具，使用线程池可以减少创建销毁线程的开销，避免线程过多导致系统资源耗尽。在高并发以及大批量的任务处理场景，线程池的使用是必不可少的。

如果有在项目中实际使用线程池，相信你可能会遇到以下痛点：

- 线程池随便定义，线程资源过多，造成服务器高负载。

- 线程池参数不易评估，随着业务的并发提升，业务面临出现故障的风险。
- 线程池任务执行时间超过平均执行周期，开发人员无法感知。
- 线程池任务堆积，触发拒绝策略，影响既有业务正常运行。
- 当业务出现超时、熔断等问题时，因为没有监控，无法确定是不是线程池引起。
- 原生线程池不支持运行时变量的传递，比如 MDC 上下文遇到线程池就 GG。
- 无法执行优雅关闭，当项目关闭时，大量正在运行的线程池任务被丢弃。
- 线程池运行中，任务执行停止，怀疑发生死锁或执行耗时操作，但是无从下手。

## 什么是 Hippo-4J

Hippo-4J 通过对 JDK 线程池增强，以及扩展三方框架底层线程池等功能，为业务系统提高线上运行保障能力。

- 全局管控 - 管理应用线程池实例；

- 动态变更 - 应用运行时动态变更线程池参数，包括不限于：核心、最大线程数、阻塞队列容量、拒绝策略等；
- 通知报警 - 内置四种报警通知策略，线程池活跃度、容量水位、拒绝策略以及任务执行时间超长；
- 运行监控 - 实时查看线程池运行时数据，最近半小时线程池运行数据图表展示；
- 功能扩展 - 支持线程池任务传递上下文；项目关闭时，支持等待线程池在指定时间内完成任务；
- 多种模式 - 内置两种使用模式：[依赖配置中心](https://hippo4j.cn/docs/user_docs/getting_started/config/hippo4j-config-start) 和 [无中间件依赖](https://hippo4j.cn/docs/user_docs/getting_started/server/hippo4j-server-start)；
- 容器管理 - Tomcat、Jetty、Undertow 容器线程池运行时查看和线程数变更；
- 中间件适配 - Apache RocketMQ、Dubbo、RabbitMQ、Hystrix 消费线程池运行时数据查看和线程数变更。

> 开源作者为爱发电不容易，看完有收获，右上角帮忙点个小星星 🤩

## 快速开始

对于本地演示目的，请参阅 [Quick start](https://hippo4j.cn/docs/user_docs/user_guide/quick-start)

演示环境：
- http://console.hippo4j.cn/index.html
- 用户名/密码：hippo4j/hippo4j

## 荣誉墙

Hippo-4J 获得了一些宝贵的荣誉，肯定了 Hippo-4J 作为一款开源框架所带来的价值。

<img align="center" width="680" alt="image" src="https://user-images.githubusercontent.com/77398366/187014905-b50bdc8b-ca0e-4137-9a02-1e6b06106191.jpg">

## 开发者

Hippo-4J 获得的成就属于每一位对 Hippo-4J 做出过贡献的成员，感谢各位的付出。

如果屏幕前的同学有意提交 Hippo-4J，请参考 [good first issue](https://github.com/opengoofy/hippo4j/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+first+issue%22) 或者 [good pro issue](https://github.com/opengoofy/hippo4j/issues?q=is%3Aissue+is%3Aopen+label%3A%22good+pro+issue%22) 任务列表。

<a href="https://github.com/opengoofy/hippo4j/graphs/contributors"><img src="https://opencollective.com/hippo4j/contributors.svg?width=890&button=false" /></a>

## 友情链接

- [[ Sa-Token ]](https://github.com/dromara/sa-token)：一个轻量级 java 权限认证框架，让鉴权变得简单、优雅！   

- [[ HertzBeat ]](https://github.com/dromara/hertzbeat)：易用友好的云监控系统, 无需 Agent, 强大自定义监控能力。   

- [[ JavaGuide ]](https://github.com/Snailclimb/JavaGuide)：一份涵盖大部分 Java 程序员所需要掌握的核心知识。

- [[ toBeBetterJavaer ]](https://github.com/itwanger/toBeBetterJavaer)：一份通俗易懂、风趣幽默的 Java 学习指南。

## 联系我

![image](https://user-images.githubusercontent.com/77398366/185774220-c11951f9-e130-4d60-8204-afb5c51d4401.png)

扫码添加微信，备注：hippo4j，邀您加入群聊。若图片加载不出来，访问 [官网站点](https://hippo4j.cn/docs/user_docs/other/group)
