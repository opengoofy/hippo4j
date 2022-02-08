![](https://images-machen.oss-cn-beijing.aliyuncs.com/hippo4j-logo-logoly.png)


<p>
  <a href="https://github.com/acmenlt/dynamic-threadpool" target="_blank">
    <img alt="GitHub" src="https://img.shields.io/github/stars/acmenlt/dynamic-threadpool?label=Stars&style=flat-square&logo=GitHub">
  </a>
  <a href="https://github.com/acmenlt/dynamic-threadpool/blob/develop/LICENSE">
    <img src="https://img.shields.io/github/license/acmenlt/dynamic-threadpool?color=42b883&style=flat-square" alt="LICENSE">
  </a>
</p>

## Hippo4J 介绍

Hippo4J 是基于 [美团线程池](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html) 设计理念开发，针对线程池增强动态调参、监控、报警功能
C/S 架构部署使用

部署 Server 端，SpringBoot 项目引入 Starter 与之交互

通过 Web 控制台对线程池参数进行动态调整，同时支持集群内线程池的差异化配置

Starter 组件内置线程池参数变更通知，以及运行过载报警功能（支持多通知平台）

按照租户、项目、线程池的维度划分，配合系统权限，让不同的开发、管理人员负责自己系统的线程池操作

## 解决什么问题

简单来说，Hippo4J 主要为我们解决了下面这些使用原生线程池存在的问题：

- **频繁抛出拒绝策略** ：核心线程过小，阻塞队列过小，最大线程过小
- **线程处理速度下降** ：核心线程过小，阻塞队列过小，最大线程过大
- **任务堆积** ：核心线程过小，阻塞队列过大
- **空闲线程资源浪费** ：核心线程或最大线程过大
- **线程池执行不可知** ：线程池运行过程中无法得知具体的参数信息，包括不限于任务调度及拒绝策略执行次数

## 模块介绍

- `hippo4j-auth`：用户、角色、权限等
- `hippo4j-common`：多个模块公用代码实现
- `hippo4j-config`：提供线程池准实时参数更新功能
- `hippo4j-console`：对接 Web 前端项目
- `hippo4j-discovery`：提供线程池项目实例注册、续约、下线等功能
- `hippo4j-spring-boot-starter`：负责与 Server 端交互的依赖组件
- `hippo4j-example` ：示例工程
- `hippo4j-server` ：聚合 Server 端发布需要的模块
- `hippo4j-tools` ：操作日志等组件代码

## 快速开始

[运行 Hippo4J 自带 Demo 参考文档](https://hippox.cn/pages/793dcb/)

[在线体验地址](http://console.hippox.cn:6691/index.html) 用户名密码：hippo4j / hippo4j

## 联系我

对于这个项目，是否有什么不一样看法，同 [作者](https://hippox.cn/pages/dd137d/) 或者创建 [Issues](https://github.com/acmenlt/dynamic-threadpool/issues) 沟通


## 公众号

如果大家想要实时关注 Hippo4J 最新动态以及干货分享的话，可以关注我的公众号

![](https://user-images.githubusercontent.com/77398366/148769916-0ee3a9c2-c8ed-4ce8-849e-038b4a546679.png)

## Stars 趋势

[![Stargazers over time](https://starchart.cc/acmenlt/dynamic-threadpool.svg)](https://starchart.cc/acmenlt/dynamic-threadpool)


## 鸣谢


Hippo4J 项目基于或参考以下项目:

- [Nacos](https://github.com/alibaba/nacos)
- [Eureka](https://github.com/Netflix/Eureka)
- [mzt-biz-log](https://github.com/mouzt/mzt-biz-log)
- [equator](https://github.com/dadiyang/equator)

感谢 JetBrains 提供的免费开源 License：

<p>
<img src="https://images.gitee.com/uploads/images/2020/0406/220236_f5275c90_5531506.png" alt="图片引用自lets-mica" style="float:left;">
</p>

