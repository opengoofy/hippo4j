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

<img src="https://img.shields.io/badge/version-v0.3.0-DeepSkyBlue.svg" />

<img src="https://img.shields.io/github/stars/acmenlt/dynamic-threadpool.svg" />

</p>

<br/>

## 这个项目做什么？

动态线程池（Dynamic-ThreadPool），下面简称 DTP 系统

[美团线程池文章](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html) 介绍中，因为业务对线程池参数没有合理配置，触发过几起生产事故，进而引发了一系列思考。最终决定封装线程池动态参数调整，扩展线程池监控以及消息报警等功能

笔者不喜欢重复造轮子，因为美团并没有开源动态线程池系统，所以选择造一个轻量级的轮子


<br/>

## 它解决了什么问题？

线程池在业务系统应该都有使用到，帮助业务流程提升效率以及管理线程

但是线程池也并非尽善尽美。比如下面这些问题就无法很好解决：

1. 核心线程过小，阻塞队列过小，最大线程过小，**导致接口频繁抛出拒绝策略异常**
2. 核心线程过小，阻塞队列过小，最大线程过大，**导致线程调度开销增大，处理速度下降**
3. 核心线程过小，阻塞队列过大，导致任务堆积，**接口响应或者程序执行时间拉长**
4. 核心线程过大，**导致线程池内空闲线程过多，过多的占用系统资源**，造成资源浪费

如果线程池的配置涉及到上述问题，那么就有可能需要发布业务系统来解决；如果发布后参数仍不合理，继续发布......

DTP 系统很好解决了这个问题，它将业务中所有线程池统一管理，遇到上述问题不需要发布系统就可以替换线程池参数

另外，支持看线程池运行实时状态。如果监控发现线程池负载、队列负载以及拒绝策略指标异常，判断参数是否合理，可以及时排查问题以及修复



<br/>

##  它有什么特性？

应用系统中线程池并不容易管理。为此，DTP 项目按照租户、项目、线程池的维度划分。再加上系统权限，让不同的开发、管理人员负责自己系统的线程池操作

举个例子，笔者在一家公司的公共组件团队，团队中负责消息、短链接网关等项目。公共组件是租户，消息或短链接就是项目

从功能性上来说，DTP 系统是对标美团文章中所介绍的。所以，DTP 系统除去动态修改线程池，还包含实时查看线程池运行时指标、负载报警、配置日志管理等。具体功能如下图

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20210807134946141.png)



笔者并没有接触过美团线程池的具体功能，只是在文章上看了功能介绍，所以在完成路径上会有所差池



<br/>

## 如何运行 Demo？

目前动态线程池功能已经完成，可以直接把代码拉到本地运行。项目中数据库是作者 ECS Docker 搭建，大家直接使用即可

1. 启动 dynamic-threadpool-server 模块下 ServerApplication 应用类
2. 启动 dynamic-threadpool-example 模块下 ExampleApplication 应用类



<br/>

通过接口修改线程池中的配置。HTTP POST 路径：http://localhost:6691/v1/cs/configs ，Body 请求体如下：

```json
{
    "ignore": "tenantId、itemId、tpId 代表唯一线程池，请不要修改",
    "tenantId": "common",
    "itemId": "message-center",
    "tpId": "message-produce",
    "coreSize": 10,
    "maxSize": 15,
    "queueType": 9,
    "capacity": 100,
    "keepAliveTime": 10,
    "rejectedType": 7,
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

现阶段仅集成钉钉消息推送，后续会持续接入企业微信、邮箱、飞书、短信等通知渠道

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
        <sub>告警发送</sub>
      </a><br>
    </td>
  </tr>
</table>

<br/>

项目代码功能还在持续开发，初定发布 1.0.0 RELEASE 完成以下功能。部署了 Server 服务，只需要引入 Starter 模块到业务系统中，即可完成动态修改、监控、报警等特性，敬请期待


<br/>

## 查看源码能收获什么？

DTP 项目还没有发布 Release 版本，小伙伴可以阅读框架源码，查看框架中好的设计理念或者编码技巧

在项目开发过程中，借鉴了 Nacos、Eureka、Seata、ShardingSphere 等中间件项目的优雅设计

- 配置“实时”刷新：参考 Nacos 1.x 配置中心的长轮询模型，Server 端修改线程池配置，Client 端“实时”刷新
- Client 实例集群注册 Server：参考 Eureka 1.x 节点注册、续约、下线等设计，完成 Server 端查看 Client 端的应用实例情况，以此实现线程池运行状态的实时查看
- ShardingSphere 对 SPI 机制的封装，自定义加载、创建、注册等逻辑，自定义 ServiceLoader 异常信息
- ......


<br/>

从框架本身的编码来说，可能对大家有帮助的技术点

- 设计模式的应用：SPI 设计、模版方法模式、Builder 模式、工厂模式、策略模式、责任链模式等
- Spring 后置处理器（BeanPostProcessor），注册 Client 端线程池以及订阅 Server 端配置变更
- 线程池监控报警，触发报警的维度：线程池活跃度、阻塞队列容量、拒绝策略执行等
- ......

<br/>


## 最后

笔者是个有代码洁癖的程序员，项目中的代码开发完全遵守阿里巴巴代码规约，也推荐大家使用，培养好的编码习惯

如果小伙伴看到这里，觉得项目功能规划、代码设计还不错的话，辛苦点个 🚀 Star ，方便后续查看，祝好。
