---
sidebar_position: 6
---

# 三方框架线程池适配

Hippo4j 目前已支持的三方框架线程池列表：

- Dubbo
- Hystrix
- RabbitMQ
- RocketMQ
- AlibabaDubbo
- RocketMQSpringCloudStream
- RabbitMQSpringCloudStream

引入 Hippo4j Server 或 Core 的 Maven Jar 坐标后，还需要引入对应的框架适配 Jar：

```xml
<dependency>
    <groupId>cn.hippo4j</groupId>
    <!-- Dubbo -->
    <artifactId>hippo4j-spring-boot-starter-adapter-dubbo</artifactId>
    <!-- Alibaba Dubbo -->
    <artifactId>hippo4j-spring-boot-starter-adapter-alibaba-dubbo</artifactId>
    <!-- Hystrix -->
    <artifactId>hippo4j-spring-boot-starter-adapter-hystrix</artifactId>
    <!-- RabbitMQ -->
    <artifactId>hippo4j-spring-boot-starter-adapter-rabbitmq</artifactId>
    <!-- RocketMQ -->
    <artifactId>hippo4j-spring-boot-starter-adapter-rocketmq</artifactId>
    <!-- SpringCloud Stream RocketMQ -->
    <artifactId>hippo4j-spring-boot-starter-adapter-spring-cloud-stream-rocketmq</artifactId>
    <!-- SpringCloud Stream RabbitMQ -->
    <artifactId>hippo4j-spring-boot-starter-adapter-spring-cloud-stream-rabbitmq</artifactId>
    <version>1.4.2</version>
</dependency>
```

如果想省事，仅需引入一个全量包，框架底层会根据条件判断加载具体线程池适配器。

```xml
<dependency>
    <groupId>cn.hippo4j</groupId>
    <artifactId>hippo4j-spring-boot-starter-adapter-all</artifactId>
    <version>1.4.2</version>
</dependency>
```

## Hippo4j Server

Hippo4j Server 仅需要引入上述 Jar 包，即可在 Hippo4j Server 的控制台进行查看及修改三方框架线程池。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220531194810047.png)

## Hippo4j Config

Hippo4j Config 除了依赖上述适配 Jar 包外，还需要在配置中心添加以下配置项。

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
