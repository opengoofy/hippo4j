---
sidebar_position: 1
---

# 简介

## 动态可观测线程池框架

Hippo-4J 通过对 JDK 线程池增强，以及扩展三方框架底层线程池等功能，为业务系统提高线上运行保障能力。

- 🏗 全局管控 - 管理应用线程池实例；

- ⚡️ 动态变更 - 应用运行时动态变更线程池参数，包括不限于：核心、最大线程数、阻塞队列容量、拒绝策略等；

- 🐳 通知报警 - 内置四种报警通知策略，线程池活跃度、容量水位、拒绝策略以及任务执行时间超长；

- 👀 运行监控 - 实时查看线程池运行时数据，最近半小时线程池运行数据图表展示；

- 👐 功能扩展 - 支持线程池任务传递上下文；项目关闭时，支持等待线程池在指定时间内完成任务；

- 👯‍♀️ 多种模式 - 内置两种使用模式：[依赖配置中心](https://hippo4j.cn/docs/user_docs/getting-started/hippo4j-core-start) 和 [无中间件依赖](https://hippo4j.cn/docs/user_docs/getting-started/hippo4j-server-start)；

- 🛠 容器管理 - Tomcat、Jetty、Undertow 容器线程池运行时查看和线程数变更；

- 🌈 中间件适配 - Apache RocketMQ、Dubbo、RabbitMQ、Hystrix 消费线程池运行时数据查看和线程数变更。

> 看完有收获，GitHub 右上角帮忙点个小星星，开源作者为爱发电也不容易 🤣

## 快速开始

对于本地演示目的，请参阅 [Quick start](getting-started/hippo4j-server-start)

演示环境：
- [http://console.hippo4j.cn/index.html](http://console.hippo4j.cn/index.html)
- 用户/密码：hippo4j/hippo4j

## 联系我

![image](https://user-images.githubusercontent.com/77398366/169202380-6c068acd-700a-41fa-8823-e01c92bb5e88.png)

## 开发者

感谢所有为 Hippo-4J 做出贡献的开发者！

<a href="https://github.com/opengoofy/hippo4j/graphs/contributors"><img src="https://contrib.rocks/image?repo=opengoofy/hippo4j"/></a>

## 我们的荣誉

Hippo-4J 获得了一些宝贵的荣誉，这属于每一位对 Hippo-4J 做出过贡献的成员，谢谢各位的付出。

![](https://user-images.githubusercontent.com/77398366/170607238-7308c9be-1d63-46a6-852c-eef2e4cf7405.JPG)

## Stars 趋势

![](https://starchart.cc/longtai-cn/hippo4j.svg)

## 友情链接

- [HertzBeat](https://github.com/dromara/hertzbeat)：易用友好的云监控系统, 无需Agent, 强大自定义监控能力。     
- [toBeBetterJavaer](https://github.com/itwanger/toBeBetterJavaer)：一份通俗易懂、风趣幽默的Java学习指南，内容涵盖Java基础、Java并发编程等核心知识点。

## 鸣谢

Hippo4J 项目基于或参考以下项目：[Nacos](https://github.com/alibaba/nacos)、[Eureka](https://github.com/Netflix/Eureka)、[Mzt-Biz-Log](https://github.com/mouzt/mzt-biz-log)。

感谢 JetBrains 提供的免费开源 License
