![](https://images-machen.oss-cn-beijing.aliyuncs.com/hippo4j-logo-logoly.png)


<p>
  <a href="https://github.com/longtai-cn/hippo4j" target="_blank">
    <img alt="GitHub" src="https://img.shields.io/github/stars/longtai-cn/hippo4j?label=Stars&style=flat-square&logo=GitHub">
  </a>
  <a href="https://github.com/longtai-cn/hippo4j/blob/develop/LICENSE">
    <img src="https://img.shields.io/github/license/longtai-cn/hippo4j?color=42b883&style=flat-square" alt="LICENSE">
  </a>
  <a title="Hits" target="_blank" href="https://github.com/longtai-cn/hippo4j">
    <img src="https://hits.b3log.org/acmenlt/dynamic-threadpool.svg">
  </a>
</p>

## Hippo4J

Hippo4J 基于 **美团动态线程池** 设计理念开发，针对线程池增强 **动态调参、监控、报警功能**。

通过 Web 控制台对线程池参数进行动态调整，支持 **集群内线程池的差异化配置**。内置线程池参数变更通知，以及 **运行过载报警** 功能（支持多通知平台）。

按照租户、项目、线程池的维度划分，配合系统权限，让不同的开发、管理人员负责自己系统的线程池。

自 1.1.0 版本发布后，Hippo4J 分为两种使用模式，用一张图来说明两者的使用差别。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220319154626314.png)

### hippo4j-core

**轻量级动态线程池管理**，依赖 Apollo、Nacos 等三方配置中心（任选其一）完成线程池参数动态变更，同样包含运行时报警、监控功能。

> 监控功能配置详见：[线程池监控](https://hippo4j.cn/pages/2f67ll)

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-202203271737049821.png)

### hippo4j-server

**部署 hippo4j-server 服务**，通过可视化 Web 界面完成线程池的创建、变更以及查看，不依赖三方中间件。

相比较 hippo4j-core，功能会更强大，但是也引入了一定的复杂性。需要部署一个 Java 服务，以及 MySQL 数据库。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/1644032018254-min.gif)

### 使用总结

|      | hippo4j-core                                 | hippo4j-server                                              |
| ---- | ---------------------------------------------------- | ------------------------------------------------------------ |
| 依赖 | Nacos、Apollo 等配置中心（任选其一） | 部署 Hippo4J Server（内部无依赖中间件） |
| 使用 | 配置中心补充线程池相关参数                 | Hippo4J Server Web 控制台添加线程池记录                                                         |
| 功能 | 包含基础功能：参数动态化、运行时监控、报警等         | 基础功能之外扩展控制台界面、线程池堆栈查看、线程池运行信息实时查看、历史运行信息查看、线程池配置集群个性化等 |

使用建议：根据公司情况选择，如果基本功能可以满足使用，选择 hippo4j-core 使用即可；如果希望更多的功能，可以选择 hippo4j-server。

**两者在进行替换的时候，无需修改业务代码**。



## 解决什么问题

简单来说，Hippo4J 主要为我们解决了下面这些使用原生线程池存在的问题：

- **原生线程池创建时无法合理评估参数问题**。比如功能使用到线程池，遇到突发流量洪峰，频繁拒绝任务。Hippo4J 提供动态修改参数功能，**避免修改线程池参数后重启线上应用**；
- 当线程池运行过程中无法再接受新的任务，此时你想知道 **线程池内线程都在做什么**？Hippo4J 提供查看线程池堆栈功能；
- 某接口频繁超时，内部依赖线程池执行，想要 **查看过去一段时间线程池运行参数情况**。Hippo4J 提供历史数据图表查看功能；
- **原生线程池无任务报警策略**。Hippo4J 内置四种报警策略，分别是：活跃度报警、队列容量报警、拒绝策略报警和运行时间过长报警。

## 报警通知

Hippo4J 已接入钉钉、企业微信以及飞书平台，提供了 **线程池参数变更通知** 和 **运行时报警** 功能。示例如下：

<table>
  <tr>
    <td align="center" style="width: 400px;">
      <a href="https://github.com/longtai-cn">
        <img src="https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211203213443242.png" style="width: 400px;"><br>
        <sub>配置变更</sub>
      </a><br>
    </td>
    <td align="center" style="width: 400px;">
      <a href="https://github.com/longtai-cn">
        <img src="https://images-machen.oss-cn-beijing.aliyuncs.com/image-20211203213512019.png" style="width: 400px;"><br>
        <sub>报警通知</sub>
      </a><br>
    </td>
  </tr>
</table>

## 快速开始

[运行 Hippo4J 自带 Demo 参考文档](https://hippo4j.cn/pages/793dcb/)

[在线体验地址](http://console.hippox.cn:6691/index.html) 用户名密码：hippo4j / hippo4j

## 联系我

对于这个项目，是否有什么不一样看法，同 [作者](https://hippo4j.cn/pages/dd137d/) 或者创建 [Issues](https://github.com/longtai-cn/hippo4j/issues) 沟通。

## 公众号

如果大家想要实时关注 Hippo4J 最新动态以及干货分享的话，可以关注我的公众号。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/43_65f6020ed111b6bb3808ec338576bd6b.png)

## Stars 趋势

[![Stargazers over time](https://starchart.cc/longtai-cn/hippo4j.svg)](https://starchart.cc/longtai-cn/hippo4j)

## 友情链接

- [**JavaGuide**](https://github.com/Snailclimb/JavaGuide)：「Java学习+面试指南」一份涵盖大部分 Java 程序员所需要掌握的核心知识。准备 Java 面试，首选 JavaGuide！
- [**Guide-Rpc-Framework**](https://github.com/Snailclimb/guide-rpc-framework)：A custom RPC framework implemented by Netty+Kyro+Zookeeper.（一款基于 Netty+Kyro+Zookeeper 实现的自定义 RPC 框架-附详细实现过程和相关教程。）
- [**toBeBetterJavaer**](https://github.com/itwanger/toBeBetterJavaer)：Java 程序员进阶之路，据说每一个优秀的 Java 程序员都喜欢她，风趣幽默、通俗易懂。内容包括 Java 基础、Java 并发编程、Java 虚拟机、Java 企业级开发、Java 面试等核心知识点
- [**Austin**](https://github.com/ZhongFuCheng3y/austin)：消息推送平台📝 推送下发【邮件】【短信】【微信服务号】【微信小程序】等消息类型。所使用的技术栈包括：SpringBoot、SpringDataJPA、MySQL、Docker、docker-compose、Kafka、Redis、Apollo、prometheus、Grafana、GrayLog、Flink、Xxl-job、Echarts等等

## 鸣谢

Hippo4J 项目基于或参考以下项目：[**Nacos**](https://github.com/alibaba/nacos)、[**Eureka**](https://github.com/Netflix/Eureka)、[**Mzt-Biz-Log**](https://github.com/mouzt/mzt-biz-log)、[**Equator**](https://github.com/dadiyang/equator)。

感谢 JetBrains 提供的免费开源 License：

<p>
    <img src="https://images.gitee.com/uploads/images/2020/0406/220236_f5275c90_5531506.png" alt="图片引用自lets-mica" style="float:left;">
</p>
