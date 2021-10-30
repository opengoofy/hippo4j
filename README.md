<div align=center>
   <img src="https://images-machen.oss-cn-beijing.aliyuncs.com/Dynamic-Thread-Pool-Main.jpeg"  />
</div>

<p align="center">
   <strong> :fire: &nbsp; 动态线程池（DTP）系统，包含 <a href="https://github.com/acmenlt/dynamic-threadpool/tree/develop/dynamic-threadpool-server">Server</a> 端及 SpringBoot Client 端需引入的 <a href="https://github.com/acmenlt/dynamic-threadpool/tree/develop/dynamic-threadpool-spring-boot-starter">Starter</a>.</strong>
</p>
<p align="center">

<img src="https://img.shields.io/badge/Author-龙台-blue.svg" />

<a target="_blank" href="http://mp.weixin.qq.com/s?__biz=Mzg4NDU0Mjk5OQ==&mid=100007373&idx=1&sn=3b375f97a576820e3e540810e720aeb0&chksm=4fb7c6b578c04fa35fab488d8dd6ddd12cfd0ef70290f3b285261fba0750785ea2725a50d508&scene=18#wechat_redirect">
     <img src="https://img.shields.io/badge/公众号-龙台 blog-yellow.svg" />
</a>

<a target="_blank" href="https://github.com/acmenlt/dynamic-threadpool">
     <img src="https://img.shields.io/badge/⭐-github-orange.svg" />
</a>

<a href="https://github.com/acmenlt/dynamic-threadpool/blob/develop/LICENSE">
    <img src="https://img.shields.io/github/license/acmenlt/dynamic-threadpool?color=42b883&style=flat-square" alt="LICENSE">
</a>

<img src="https://img.shields.io/badge/JDK-1.8+-green?logo=appveyor" />

<img src="https://tokei.rs/b1/github/acmenlt/dynamic-threadpool?category=lines" />

<img src="https://img.shields.io/badge/version-v0.4.0-DeepSkyBlue.svg" />

<img src="https://img.shields.io/github/stars/acmenlt/dynamic-threadpool.svg" />

</p>

<br/>

## 为什么写这个项目？

[美团线程池文章](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html "美团线程池文章") 介绍中，因为业务对线程池参数没有合理配置，触发过几起生产事故，进而引发了一系列思考。最终决定封装线程池动态参数调整，扩展线程池监控以及消息报警等功能

在开源平台找了挺多动态线程池项目，从功能性以及健壮性而言，个人感觉不满足企业级应用

因为对动态线程池比较感兴趣，加上想写一个有意义的项目，所以决定自己来造一个轻量级的轮子

想给项目起一个简单易记的名字，类似于 Eureka、Nacos、Redis；后来和朋友商量，决定以动物命名：**Hippo**

![](https://user-images.githubusercontent.com/77398366/138722557-38d638ae-36b4-48ca-8d6b-3bf7a4bc430b.png)


<br/>

## 它解决了什么问题？

线程池在业务系统应该都有使用到，帮助业务流程提升效率以及管理线程，多数场景应用于大量的异步任务处理

虽然线程池提供了我们许多便利，但也并非尽善尽美，比如下面这些问题就无法很好解决

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211023160830084.png)

<br/>

如果线程池的配置涉及到上述问题，那么就有可能需要发布业务系统来解决；如果发布后参数仍不合理，继续发布......

Hippo 很好解决了这个问题，它将业务中所有线程池统一管理，遇到上述问题不需要发布系统就可以替换线程池参数

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211023142726818.png)

<br/>

##  它有什么特性？

应用系统中线程池并不容易管理。参考美团的设计，Hippo 按照租户、项目、线程池的维度划分。再加上系统权限，让不同的开发、管理人员负责自己系统的线程池操作

举个例子，小编在一家公司的公共组件团队，团队中负责消息、短链接网关等项目。公共组件是租户，消息或短链接就是项目

<br/>

| 模块                                   | 模块名称           | 注释                                     |
| -------------------------------------- | ------------------ | ---------------------------------------- |
| dynamic-threadpool-auth              | 用户权限           | -                                        |
| dynamic-threadpool-common              | 公共模块           | -                                        |
| dynamic-threadpool-config              | 配置中心           | 提供线程池准实时更新功能                 |
| dynamic-threadpool-console             | 控制台             | 对接前端项目                             |
| dynamic-threadpool-discovery           | 注册中心           | 提供线程池项目实例注册、续约、下线等功能 |
| dynamic-threadpool-spring-boot-starter | SpringBoot Starter | -                                        |
| dynamic-threadpool-example             | 示例项目           | -                                        |
| dynamic-threadpool-server              | 服务端             | Server 集成各组件                        |
| dynamic-threadpool-tools               | 抽象工具类         | 操作日志等组件                  |


<br/>

Hippo 除去动态修改线程池，还包含实时查看线程池运行时指标、负载报警、配置日志管理等


![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211023101844619.png)

<br/>

## 如何运行 Demo？

目前动态线程池功能已经完成，可以直接把代码拉到本地运行。导入 [Hippo 初始化 SQL 语句](https://github.com/acmenlt/dynamic-threadpool/blob/develop/server/src/main/resources/hippo_manager.sql)

1. 启动 `dynamic-threadpool-server` 模块下 ServerApplication 应用类
2. 启动 `dynamic-threadpool-example` 模块下 ExampleApplication 应用类

<br/>

通过接口修改线程池中的配置。HTTP POST 路径：http://localhost:6691/v1/cs/configs ，Body 请求体如下：

```json
{
    "ignore": "tenantId、itemId、tpId 代表唯一线程池，请不要修改",
    "tenantId": "prescription",
    "itemId": "dynamic-threadpool-example",
    "tpId": "message-produce",
    "coreSize": 10,
    "maxSize": 15,
    "queueType": 9,
    "capacity": 100,
    "keepAliveTime": 10,
    "rejectedType": 3,
    "isAlarm": 0,
    "capacityAlarm": 81,
    "livenessAlarm": 82
}
```

<br/>

接口调用成功后，观察 dynamic-threadpool-example 控制台日志输出，日志输出包括不限于此信息即为成功

```tex
[🔥 MESSAGE-PRODUCE] Changed thread pool. coreSize :: [11=>10], maxSize :: [15=>15], queueType :: [9=>9]
capacity :: [100=>100], keepAliveTime :: [10000=>10000], rejectedType :: [7=>7]
```

<br/>

现阶段已集成钉钉消息推送，后续会持续接入企业微信、邮箱、飞书、短信等通知渠道。可以通过添加钉钉群体验消息推送，群号：31764717

<table>
  <tr>
    <td align="center" style="width: 200px;">
      <a href="https://github.com/acmenlt">
        <img src="https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211013122816688.png" style="width: 400px;"><br>
        <sub>配置变更</sub>
      </a><br>
    </td>
    <td align="center" style="width: 200px;">
      <a href="https://github.com/acmenlt">
        <img src="https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211013113649068.png" style="width: 400px;"><br>
        <sub>报警通知</sub>
      </a><br>
    </td>
  </tr>
</table>

<br/>

项目代码功能还在持续开发，初定发布 1.0.0 RELEASE 完成以下功能。部署了 Server 服务，只需要引入 Starter 组件到业务系统中，即可完成动态修改、监控、报警等特性

<br/>

## 查看源码能收获什么？

目前还没有发布 Release 版本，小伙伴可以阅读框架源码，查看框架中好的设计理念或者编码技巧

在项目开发过程中，借鉴了 Nacos、Eureka、Seata、ShardingSphere 等中间件项目的优雅设计

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211023143632685.png)

<br/>


## Github Stars 趋势

如果小伙伴查看源码设计有所收获，辛苦点个 🚀 Star ，方便后续查看

[![Stargazers over time](https://starchart.cc/acmenlt/dynamic-threadpool.svg)](https://starchart.cc/acmenlt/dynamic-threadpool) 

 <br/>


## 最后

小编是个有代码洁癖的程序员，项目中的代码开发完全遵守阿里巴巴代码规约，也推荐大家使用，培养好的编码习惯

对于这个项目，是否有什么不一样看法，欢迎在 Issue 一起沟通交流；或者添加小编微信进交流群


![](https://user-images.githubusercontent.com/77398366/138920260-e9dd1268-797f-4d42-9abb-62353d08ea6a.png)
