<!--@nrg.languages=zh,en-->
<!--@nrg.defaultLanguage=zh-->
<!--@nrg.fileNamePattern.en=README-EN.md-->
<!--zh-->
## 动态可观测线程池，提高系统运行保障能力<!--zh-->
<!--zh-->
<!--zh-->
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)<!--zh-->
[![Build Status](https://github.com/opengoofy/hippo4j/actions/workflows/ci.yml/badge.svg?event=push)](https://github.com/opengoofy/hippo4j)<!--zh-->
<!--zh-->
![](https://img.shields.io/github/stars/opengoofy/hippo4j?color=5470c6)<!--zh-->
![](https://img.shields.io/github/forks/opengoofy/hippo4j?color=3ba272)<!--zh-->
![](https://img.shields.io/github/contributors/opengoofy/hippo4j)<!--zh-->
[![star](https://gitcode.com/opengoofy/hippo4j/star/badge.svg)](https://gitcode.com/opengoofy/hippo4j)<!--zh-->
[![Docker Pulls](https://img.shields.io/docker/pulls/hippo4j/hippo4j-server.svg?label=docker%20pulls&color=fac858)](https://store.docker.com/community/images/hippo4j/hippo4j-server)<!--zh-->
[![codecov](https://codecov.io/gh/opengoofy/hippo4j/branch/develop/graph/badge.svg?token=WBUVJN107I)](https://codecov.io/gh/opengoofy/hippo4j)<!--zh-->
[![EN doc](https://img.shields.io/badge/readme-English-orange.svg)](https://github.com/opengoofy/hippo4j/blob/develop/README-EN.md)<!--zh-->
<!--zh-->
> 📢 新项目<!--zh-->
> <!--zh-->
> [Ragent AI（点击跳转）](https://nageoffer.com/ragent) —— 企业级 Agentic RAG 项目，拿个 offer 社群在 AI 领域的首个开源作品。架构设计、代码实现保持一贯的高标准，适合写进校招/社招简历。毕竟现在哪家公司不关注 AI？<!--zh-->
<!--zh-->
<!--zh-->
### 线程池痛点<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
线程池是一种基于池化思想管理线程的工具，使用线程池可以减少创建销毁线程的开销，避免线程过多导致系统资源耗尽。在高并发以及大批量的任务处理场景，线程池的使用是必不可少的。<!--zh-->
<!--zh-->
如果有在项目中实际使用线程池，相信你可能会遇到以下痛点：<!--zh-->
<!--zh-->
- 线程池随便定义，线程资源过多，造成服务器高负载。<!--zh-->
<!--zh-->
- 线程池参数不易评估，随着业务的并发提升，业务面临出现故障的风险。<!--zh-->
- 线程池任务执行时间超过平均执行周期，开发人员无法感知。<!--zh-->
- 线程池任务堆积，触发拒绝策略，影响既有业务正常运行。<!--zh-->
- 当业务出现超时、熔断等问题时，因为没有监控，无法确定是不是线程池引起。<!--zh-->
- 原生线程池不支持运行时变量的传递，比如 MDC 上下文遇到线程池就 GG。<!--zh-->
- 无法执行优雅关闭，当项目关闭时，大量正在运行的线程池任务被丢弃。<!--zh-->
- 线程池运行中，任务执行停止，怀疑发生死锁或执行耗时操作，但是无从下手。<!--zh-->
<!--zh-->
### 什么是 Hippo4j<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
提供以下功能支持：<!--zh-->
<!--zh-->
- 全局管控 - 管理应用线程池实例。<!--zh-->
<!--zh-->
- 动态变更 - 应用运行时动态变更线程池参数，包括但不限于：核心、最大线程数、阻塞队列容量、拒绝策略等。<!--zh-->
- 通知报警 - 内置四种报警通知策略，线程池活跃度、容量水位、拒绝策略以及任务执行时间超长。<!--zh-->
- 数据采集 - 支持多种方式采集线程池数据，包括但不限于：日志、内置采集、Prometheus、InfluxDB、ElasticSearch 等。<!--zh-->
- 运行监控 - 实时查看线程池运行时数据，自定义时间内线程池运行数据图表展示。<!--zh-->
- 功能扩展 - 支持线程池任务传递上下文；项目关闭时，支持等待线程池在指定时间内完成任务。<!--zh-->
- 多种模式 - 内置两种使用模式：[依赖配置中心](https://hippo4j.cn/docs/user_docs/getting_started/config/hippo4j-config-start) 和 [无中间件依赖](https://hippo4j.cn/docs/user_docs/getting_started/server/hippo4j-server-start)。<!--zh-->
- 容器管理 - Tomcat、Jetty、Undertow 容器线程池运行时查看和线程数变更。<!--zh-->
- 框架适配 - Dubbo、Hystrix、RabbitMQ、RocketMQ 等消费线程池运行时数据查看和线程数变更。<!--zh-->
- 变更审核 - 提供多种用户角色，普通用户变更线程池参数需要 Admin 用户审核方可生效。<!--zh-->
- 动态化插件 - 内置多种线程池插件，支持用户自定义插件以及运行时扩展。<!--zh-->
- 多版本适配 - 经过实际测试，已支持客户端 SpringBoot 1.5.x => 2.7.5 版本（更高版本未测试）。<!--zh-->
<!--zh-->
### 架构设计<!--zh-->
<!--zh-->
---<!--zh-->
<img width="1307" alt="image" src="https://user-images.githubusercontent.com/106363931/233792824-f879500f-fea1-4872-be15-957236f6bf2b.png"><!--zh-->
<!--zh-->
### 快速开始<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
对于本地演示目的，请参阅 [Quick start](https://hippo4j.cn/docs/user_docs/user_guide/quick-start)<!--zh-->
<!--zh-->
演示环境： [http://console.hippo4j.cn/index.html](http://console.hippo4j.cn/index.html)<!--zh-->
<!--zh-->
### 接入登记<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
更多接入的公司，欢迎在 [登记地址](https://github.com/opengoofy/hippo4j/issues/13) 登记，登记仅仅为了产品推广。<!--zh-->
<!--zh-->
### 联系我<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
开源不易，右上角点个 Star 鼓励一下吧！<!--zh-->
<!--zh-->
如果大家想要实时关注 Hippo4j 更新的文章以及分享的干货的话，可以关注我的公众号。<!--zh-->
<!--zh-->
使用过程中有任何问题，或者对项目有什么建议，关注公众号回复：加群，和 `1000+` 志同道合的朋友交流讨论。<!--zh-->
<!--zh-->
<img width="586" alt="image" src="https://user-images.githubusercontent.com/77398366/225888779-367f42a6-8401-4867-8e80-44214e1d17c1.png"><!--zh-->
<!--zh-->
<!--zh-->
### 深入原理<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
如果您公司没有使用 Hippo4j 场景的话，我也建议去阅读下项目的底层原理，主要有以下几个原因：<!--zh-->
<!--zh-->
- 为了提高代码质量以及后续的扩展行为，运用多种设计模式实现高内聚、低耦合。<!--zh-->
<!--zh-->
- 框架底层依赖 Spring 框架运行，并在源码中大量使用 Spring 相关功能。<!--zh-->
- 运用 JUC 并发包下多种工具保障多线程运行安全，通过实际场景理解并发编程。<!--zh-->
- 借鉴主流开源框架 Nacos、Eureka 实现轻量级配置中心和注册中心功能。<!--zh-->
- 自定义 RPC 框架实现，封装 Netty 完成客户端/服务端网络通信优化。<!--zh-->
- 通过 CheckStyle、Spotless 等插件规范代码编写，保障高质量代码行为和代码样式。<!--zh-->
<!--zh-->
### 友情链接<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
- [[ Sa-Token ]](https://github.com/dromara/sa-token)：一个轻量级 java 权限认证框架，让鉴权变得简单、优雅！<!--zh-->
<!--zh-->
- [[ HertzBeat ]](https://github.com/dromara/hertzbeat)：易用友好的云监控系统, 无需 Agent, 强大自定义监控能力。<!--zh-->
- [[ JavaGuide ]](https://github.com/Snailclimb/JavaGuide)：一份涵盖大部分 Java 程序员所需要掌握的核心知识。<!--zh-->
- [[ toBeBetterJavaer ]](https://github.com/itwanger/toBeBetterJavaer)：一份通俗易懂、风趣幽默的 Java 学习指南。<!--zh-->
- [[ Jpom ]](https://gitee.com/dromara/Jpom)：简而轻的低侵入式在线构建、自动部署、日常运维、项目监控软件。<!--zh-->
- [[ 12306 ]](https://gitee.com/nageoffer/12306)：完成高仿 12306 用户+抢票+订单+支付服务，帮助学生主打就业的项目。<!--zh-->
- [[ CongoMall ]](https://gitee.com/nageoffer/congomall)：企业级商城，基于 DDD 领域驱动模型开发，包含商城业务和基础架构。<!--zh-->
<!--zh-->
### 贡献者<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
感谢所有为项目作出贡献的开发者。如果有意贡献，参考 [good first issue](https://github.com/opengoofy/hippo4j/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22)。<!--zh-->
<!--zh-->
<!-- readme: contributors -start --><!--zh-->
<!--zh-->
<!-- readme: contributors -end --><!--zh-->
<!--zh-->
### 鸣谢<!--zh-->
<!--zh-->
---<!--zh-->
<!--zh-->
Hippo4j 社区收到 Jetbrains 多份 Licenses，并已分配项目 [活跃开发者](https://hippo4j.cn/community/team/)，非常感谢 Jetbrains 对开源社区的支持。<!--zh-->
<!--zh-->
![JetBrains Logo (Main) logo](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)<!--zh-->
# Dynamic and observable thread pool framework<!--en-->
<!--en-->
<!--en-->
[![License](https://img.shields.io/badge/license-Apache--2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)<!--en-->
[![Build Status](https://github.com/opengoofy/hippo4j/actions/workflows/ci.yml/badge.svg?event=push)](https://github.com/opengoofy.hippo4j)<!--en-->
<!--en-->
![](https://img.shields.io/github/stars/opengoofy/hippo4j?color=5470c6)<!--en-->
![](https://img.shields.io/github/forks/opengoofy/hippo4j?color=3ba272)<!--en-->
![](https://img.shields.io/github/contributors/opengoofy/hippo4j)<!--en-->
[![Docker Pulls](https://img.shields.io/docker/pulls/hippo4j/hippo4j-server.svg?label=docker%20pulls&color=fac858)](https://store.docker.com/community/images/hippo4j/hippo4j-server)<!--en-->
[![codecov](https://codecov.io/gh/opengoofy/hippo4j/branch/develop/graph/badge.svg?token=WBUVJN107I)](https://codecov.io/gh/opengoofy/hippo4j)<!--en-->
[![EN doc](https://img.shields.io/badge/readme-English-orange.svg)](https://github.com/opengoofy/hippo4j/blob/develop/README-EN.md)<!--en-->
<!--en-->
| **Stargazers Over Time**                                                                                              | **Contributors Over Time**                                                                                                                                                                                                                       |<!--en-->
|:---------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|<!--en-->
| [![Stargazers over time](https://api.star-history.com/svg?repos=opengoofy/hippo4j&type=Date)](https://api.star-history.com/svg?repos=opengoofy/hippo4j&type=Date) | [![Contributor over time](https://contributor-graph-api.apiseven.com/contributors-svg?chart=contributorOverTime&repo=opengoofy/hippo4j)](https://www.apiseven.com/en/contributor-graph?chart=contributorOverTime&repo=opengoofy/hippo4j) |<!--en-->
<!--en-->
### Thread pool pain points<!--en-->
<!--en-->
---<!--en-->
<!--en-->
A thread pool is a tool for managing threads based on the idea of pooling.<!--en-->
<!--en-->
Using a thread pool reduces the overhead of creating and destroying threads and avoids running out of system resources due to too many threads.<!--en-->
<!--en-->
The use of thread pools is essential in highly concurrent and high-volume task processing scenarios.<!--en-->
<!--en-->
If you have actually used thread pools in your projects, I believe you may have encountered the following pain points:<!--en-->
<!--en-->
- Thread pools are defined randomly, with too many thread resources, causing high server load.<!--en-->
<!--en-->
- The thread pool parameters are not easily evaluated and the business is at risk of failure.<!--en-->
- Thread pool task execution time exceeds the average execution cycle and developers are not informed.<!--en-->
- Thread pool tasks pile up and affect business operations.<!--en-->
- Wireless process pool monitoring when the service has timeouts, meltdowns, and other problems.<!--en-->
- Thread pools do not support the passing of runtime variables, such as MDC contexts.<!--en-->
- When a project is closed, a large number of running thread pool tasks are discarded.<!--en-->
- Thread pool running, task execution stopped, don't know the problem.<!--en-->
<!--en-->
### What is Hippo4j<!--en-->
<!--en-->
---<!--en-->
<!--en-->
Hippo4j through the JDK thread pool enhancements, as well as extending the three-party framework underlying thread pools and other features for business systems to improve online operational security capabilities.<!--en-->
<!--en-->
The following functional support is provided:<!--en-->
<!--en-->
- Global Control - Managing Application Thread Pool Instances.<!--en-->
<!--en-->
- Dynamic changes - dynamically changing thread pool parameters at application runtime.<!--en-->
- Notify alarms - Four built-in alarm notification policies.<!--en-->
- Run Monitoring - Real-time view of thread pool runtime data.<!--en-->
- Feature extensions - support for thread pooling task passing contexts, etc.<!--en-->
- Multiple Modes - Two built-in usage modes: Configuration Center Mode and No Middleware Mode.<!--en-->
- Container Management - Tomcat, Jetty, Undertow container thread pool runtime view and thread count changes.<!--en-->
- Framework adaptation - Dubbo, Hystrix, Polaris, RabbitMQ, RocketMQ and other consumer thread pool runtime data view and thread count changes.<!--en-->
<!--en-->
### Quick Start<!--en-->
<!--en-->
---<!--en-->
<!--en-->
For local presentation purposes, see [Quick start](https://hippo4j.cn/docs/user_docs/user_guide/quick-start).<!--en-->
<!--en-->
Demo Environment: [http://console.hippo4j.cn/index.html](http://console.hippo4j.cn/index.html).<!--en-->
<!--en-->
### Who is using<!--en-->
<!--en-->
---<!--en-->
<!--en-->
More companies with access are welcome to register at [registration address](https://github.com/opengoofy/hippo4j/issues/13), registration is only for product promotion.<!--en-->
<!--en-->
### Contributors<!--en-->
<!--en-->
---<!--en-->
<!--en-->
Thanks to all the developers who contributed to the project. If interested in contributing, refer to [good first issue](https://github.com/opengoofy/hippo4j/issues?q=is%3Aopen+is%3Aissue+label%3A%22good+first+issue%22).<!--en-->
<!--en-->
<a href="https://github.com/opengoofy/hippo4j/graphs/contributors"><!--en-->
    <img src="https://contrib.rocks/image?repo=opengoofy/hippo4j" /><!--en-->
</a><!--en-->
