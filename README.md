<img align="center" width="400" alt="image" src="https://user-images.githubusercontent.com/77398366/181906454-b46f6a14-7c2c-4b8f-8b0a-40432521bed8.png">

# 动态可观测线程池框架，提高线上运行保障能力

[![Gitee](https://gitee.com/agentart/hippo4j/badge/star.svg?theme=gvp)](https://gitee.com/agentart/hippo4j) [![GitHub](https://img.shields.io/github/stars/opengoofy/hippo4j)](https://github.com/opengoofy/hippo4j) [![OpenIssue](http://isitmaintained.com/badge/open/opengoofy/hippo4j.svg)](https://github.com/opengoofy/hippo4j/issues) [![Contributors](https://img.shields.io/github/contributors/opengoofy/hippo4j?color=3ba272)](https://github.com/opengoofy/hippo4j/graphs/contributors) [![License](https://img.shields.io/github/license/opengoofy/hippo4j?color=5470c6)](https://github.com/opengoofy/hippo4j/blob/develop/LICENSE)

-------

## 什么是 Hippo-4J

Hippo-4J 通过对 JDK 线程池增强，以及扩展三方框架底层线程池等功能，为业务系统提高线上运行保障能力。

- 🏗 全局管控 - 管理应用线程池实例；

- ⚡️ 动态变更 - 应用运行时动态变更线程池参数，包括不限于：核心、最大线程数、阻塞队列容量、拒绝策略等；

- 🐳 通知报警 - 内置四种报警通知策略，线程池活跃度、容量水位、拒绝策略以及任务执行时间超长；

- 👀 运行监控 - 实时查看线程池运行时数据，最近半小时线程池运行数据图表展示；

- 👐 功能扩展 - 支持线程池任务传递上下文；项目关闭时，支持等待线程池在指定时间内完成任务；

- 👯‍♀️ 多种模式 - 内置两种使用模式：[依赖配置中心](https://hippo4j.cn/docs/user_docs/getting-started/hippo4j-core-start) 和 [无中间件依赖](https://hippo4j.cn/docs/user_docs/getting-started/hippo4j-server-start)；

- 🛠 容器管理 - Tomcat、Jetty、Undertow 容器线程池运行时查看和线程数变更；

- 🌈 中间件适配 - Apache RocketMQ、Dubbo、RabbitMQ、Hystrix 消费线程池运行时数据查看和线程数变更。

> 看完有收获，右上角帮忙点个小星星，开源作者为爱发电也不容易 🤣

## 快速开始

对于本地演示目的，请参阅 [Quick start](https://hippo4j.cn/docs/user_docs/getting-started/hippo4j-server-start)

演示环境：
- http://console.hippo4j.cn/index.html
- 用户名/密码：hippo4j/hippo4j

## 安全检测

[![OSCS Status](https://www.oscs1024.com/platform/badge/opengoofy/hippo4j.svg?size=large)](https://www.murphysec.com/dr/s285JA1FVHD7tayWLt)

## 联系我

图片加载不出来，访问 [官网站点](https://hippo4j.cn/docs/user_docs/other/group)

![image](https://user-images.githubusercontent.com/77398366/180110548-7a05b74d-0316-4066-96f4-1c9331638633.png)

## 开发者

感谢所有为 Hippo-4J 做出贡献的开发者！

<a href="https://github.com/opengoofy/hippo4j/graphs/contributors"><img src="https://opencollective.com/hippo4j/contributors.svg?width=890&button=false"/></a>

## 我们的荣誉

Hippo-4J 获得了一些宝贵的荣誉，这属于每一位对 Hippo-4J 做出过贡献的成员，谢谢各位的付出。

<img align="center" width="880" alt="image" src="https://user-images.githubusercontent.com/77398366/170607238-7308c9be-1d63-46a6-852c-eef2e4cf7405.JPG">

## 友情链接

- [HertzBeat](https://github.com/dromara/hertzbeat)：易用友好的云监控系统, 无需Agent, 强大自定义监控能力。   
- [JavaGuide](https://github.com/Snailclimb/JavaGuide)：一份涵盖大部分 Java 程序员所需要掌握的核心知识。
- [toBeBetterJavaer](https://github.com/itwanger/toBeBetterJavaer)：一份通俗易懂、风趣幽默的 Java 学习指南。
- [Guide-Rpc-Framework](https://github.com/Snailclimb/guide-rpc-framework)：一款基于 Netty+Kyro+Zookeeper 实现的自定义 RPC 框架。
- [Austin](https://github.com/ZhongFuCheng3y/austin)：消息推送平台，支持短信、邮件、微信公众号、企业微信、钉钉等多种消息类型。
