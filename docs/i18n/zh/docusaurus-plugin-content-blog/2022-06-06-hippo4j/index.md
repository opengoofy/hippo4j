---
slug: Hippo-4J发布1.3.0版本
title: Hippo-4J发布1.3.0版本
authors: [xiaomage]
tags: [hippo4j, release, 1.3.0]
---

大家好，我是 **小马哥**。

Hippo4j 距离上一个版本 1.2.1 已经过去一个月的时间。在此期间，由 **8 位贡献者** 提交了 **170+ commits**，正式发布 **1.3.0** 版本。

注：这是一个 **兼容历史版本** 的重大升级。

## HIPPO-4J 1.3.0

### Feature

1. 添加 RabbitMQ 线程池监控及动态变更
2. 添加 RocketMQ 线程池监控及动态变更
3. 添加 Dubbo 线程池监控及动态变更
4. 添加 SpringCloud Stream RocketMQ 消费线程池监控及动态变更

### Refactor

1. 重构容器线程池查询及修改功能
2. 优化配置中心触发监听后，所执行的数据变更逻辑

### Optimize

1. 前端控制台删除无用组件
2. 服务端页面字段未显示中文
3. 控制台 UI 优化
4. 修改线程池实例后实时刷新列表参数
5. 容器线程池编辑仅限 Admin 权限
6. SpringBoot Starter 变更包路径

### BUG

1. 修复 SpringBoot Nacos 动态刷新不生效
2. 报警配置 alarm=false 不配置通知报警平台和接收人报错

## 三方框架线程池适配

Hippo4j 1.3.0 最大的功能发布就是开发出了 **适配三方框架的基础框架**。

目前已完成 **Dubbo、RabbitMQ、RocketMQ、RocketMQSpringCloudStream** 的线程池适配，后续还会接入 **Kafka、Hystrix** 等框架或中间件的线程池适配。

### 引入适配三方框架 Jar 包

引入 Hippo4j server 或 core 的 maven jar 坐标后，还需要引入对应的框架适配 jar：

```xml
<dependency>
    <groupId>cn.hippo4j</groupId>
    <!-- Dubbo -->
    <artifactId>hippo4j-spring-boot-starter-adapter-dubbo</artifactId>
    <!-- RabbitMQ -->
    <artifactId>hippo4j-spring-boot-starter-adapter-rabbitmq</artifactId>
    <!-- RocketMQ -->
    <artifactId>hippo4j-spring-boot-starter-adapter-rocketmq</artifactId>
    <!-- SpringCloud Stream RocketMQ -->
    <artifactId>hippo4j-spring-boot-starter-adapter-spring-cloud-stream-rocketmq</artifactId>
    <version>1.3.0</version>
</dependency>
```

如果想觉得引入多个 jar 包繁琐，可以仅需引入一个全量包，Hippo4j 框架底层会根据各中间件的条件，判断加载具体线程池适配器。

```xml
<dependency>
    <groupId>cn.hippo4j</groupId>
    <artifactId>hippo4j-spring-boot-starter-adapter-all</artifactId>
    <version>1.3.0</version>
</dependency>
```

### HIPPO-4J Server

Hippo4j server 引入上述适配 jar 包后，即可在 Hippo4j server 的控制台进行查看及修改三方框架线程池。

![图1 线程池适配列表](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220531194810047.png)



点击编辑即可修改该 Java 应用对应的框架底层线程池。

![图2 修改三方线程池](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220605152549732.png)



点击 **全部修改** 按钮可以修改当前组下所有应用实例的线程池配置。

修改成功后，应用控制台打印以下日志，即为修改成功。

```java
[input] RocketMQ consumption thread pool parameter change. coreSize :: 1 => 10, maximumSize :: 1 => 10
```

### HIPPO-4J Core

Hippo4j core 除了依赖上述适配 Jar 包外，还需要在配置中心添加以下配置项。

```yaml
spring:
  dynamic:
    thread-pool:
      # 省略其它配置
      adapter-executors:
        # threadPoolKey 代表线程池标识
        - threadPoolKey: 'input'
          # mark 为三方线程池框架类型，参见文初已支持框架集合
          mark: 'RocketMQSpringCloudStream'
          corePoolSize: 10
          maximumPoolSize: 10
```

## Gitee GVP

Hippo4j 获得了一些宝贵的荣誉，这属于每一位对 Hippo4j 做出过贡献的成员。

![图3 GVP 证书](https://images-machen.oss-cn-beijing.aliyuncs.com/170607238-7308c9be-1d63-46a6-852c-eef2e4cf7405.jpeg)



感谢所有为 Hippo4j 做出贡献的开发者！

https://github.com/opengoofy/hippo4j/graphs/contributors

![图4 Hippo4j 开发者](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220605151136276.png)



## 最后总结

开源不易，如果各位小伙伴看了 Hippo4j 框架后有所收获，希望能帮忙在 Github、Gitee 点个 star，谢谢。

**Github**：https://github.com/opengoofy/hippo4j

**Gitee**：https://gitee.com/mabaiwancn/hippo4j

目前已有 **10+** 公司在生产环境使用 Hippo4j，如果贵公司使用了 Hippo4j，请在下方 Issue 登记，谢谢。

**Issue**：https://github.com/opengoofy/hippo4j/issues/13

登记使用不会对公司有任何影响，仅为了扩大 Hippo4j 影响力，帮助它能走得更远。
