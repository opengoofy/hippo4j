---
sidebar_position: 1
---

# hippo4j core 接入

Nacos、Apollo、Zookeeper 配置中心任选其一。

## hippo4j 配置

```xml
<dependency>
    <groupId>cn.hippo4j</groupId>
    <artifactId>hippo4j-core-spring-boot-starter</artifactId>
    <version>1.3.1</version>
</dependency>
```

启动类上添加注解 @EnableDynamicThreadPool。

```java
@SpringBootApplication
@EnableDynamicThreadPool
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
```

SpringBoot 应用配置文件添加：

```yaml
server:
  port: 8090
  servlet:
    context-path: /example

spring:
  profiles:
    active: dev

  dynamic:
    thread-pool:
      enable: true  # 是否开启动态线程池
      banner: true  # 是否打印 banner
      collect: true  # 是否开启线程池数据采集，对接 Prometheus
      notify-platforms:  # 通知报警平台，⚠️ 请替换为自己创建的群机器人
        - platform: 'WECHAT'  # 企业微信
          token: 1d307bfa-815f-4662-a2e5-99415e947bb8
        - platform: 'DING'  # 钉钉
          token: 56417ebba6a27ca352f0de77a2ae9da66d01f39610b5ee8a6033c60ef9071c55
          secret: SEC40943de20b51e993b47e9a55490a168f1c9e00bdb4f0fb15b1d9e4b58f8b05f3  # 加签
        - platform: 'LARK'  # 飞书
          token: 2cbf2808-3839-4c26-a04d-fd201dd51f9e
      nacos:  # nacos apollo 任选其一
        data-id: xxx
        group: xxx
      apollo:
        namespace: xxxx
      config-file-type: yml  # 配置中心文件格式
      # tomcat:
      # jetty:
      undertow: # 三种容器线程池，任选其一
        core-pool-size: 100
        maximum-pool-size: 200
        keep-alive-time: 1000
      # 全局通知配置
      alarm: true  # 是否报警
      check-state-interval: 3000  # 检查线程池状态，是否达到报警条件，单位毫秒
      active-alarm: 80  # 活跃度报警阈值；假设线程池最大线程数 10，当线程数达到 8 发起报警
      capacity-alarm: 80  # 容量报警阈值；假设阻塞队列容量 100，当容量达到 80 发起报警
      alarm-interval: 8  # 报警间隔，同一线程池下同一报警纬度，在 interval 时间内只会报警一次，单位秒
      receive: xxx # 企业微信填写用户 ID（填写其它将无法达到 @ 效果）、钉钉填手机号、飞书填 ou_ 开头唯一 ID
      # 线程池配置
      executors:
        - thread-pool-id: 'message-consume'  # 线程池标识
          core-pool-size: 1  # 核心线程数
          maximum-pool-size: 1  # 最大线程数
          queue-capacity: 1  # 阻塞队列大小
          execute-time-out: 1000  # 执行超时时间，超过此时间发起报警
          blocking-queue: 'LinkedBlockingQueue'  # 阻塞队列名称，参考 QueueTypeEnum，支持 SPI
          rejected-handler: 'AbortPolicy'  # 拒绝策略名称，参考 RejectedPolicies，支持 SPI
          keep-alive-time: 1024  # 线程存活时间，单位秒  
          allow-core-thread-time-out: true  # 是否允许核心线程超时
          thread-name-prefix: 'message-consume'  # 线程名称前缀
          notify:  # 通知配置，线程池中通知配置如果存在，则会覆盖全局通知配置
            is-alarm: true  # 是否报警
            active-alarm: 80  # 活跃度报警阈值；假设线程池最大线程数 10，当线程数达到 8 发起报警
            capacity-alarm: 80  # 容量报警阈值；假设阻塞队列容量 100，当容量达到 80 发起报警
            interval: 8  # 报警间隔，同一线程池下同一报警纬度，在 interval 时间内只会报警一次，单位分钟
            receive: xxx # 企业微信填写用户 ID（填写其它将无法达到 @ 效果）、钉钉填手机号、飞书填 ou_ 开头唯一 ID
        - thread-pool-id: 'message-produce'
          core-pool-size: 1
          maximum-pool-size: 1
          queue-capacity: 1
          execute-time-out: 1000
          blocking-queue: 'LinkedBlockingQueue'
          rejected-handler: 'AbortPolicy'
          keep-alive-time: 1024
          allow-core-thread-time-out: true
          thread-name-prefix: 'message-consume'
          notify:
            is-alarm: true
            active-alarm: 80
            capacity-alarm: 80
            interval: 8
            receive: xxx
```

## ThreadPoolExecutor 适配

添加线程池配置类，通过 `@DynamicThreadPool` 注解修饰。`threadPoolId` 为服务端创建的线程池 ID。

```java
package cn.hippo4j.example;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor messageConsumeDynamicExecutor() {
        String threadPoolId = "message-consume";
        ThreadPoolExecutor messageConsumeDynamicExecutor = ThreadPoolBuilder.builder()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .dynamicPool()
                .build();
        return messageConsumeDynamicExecutor;
    }

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor messageProduceDynamicExecutor() {
        String threadPoolId = "message-produce";
        ThreadPoolExecutor messageProduceDynamicExecutor = ThreadPoolBuilder.builder()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .dynamicPool()
                .build();
        return messageProduceDynamicExecutor;
    }

}
```

通过 ThreadPoolBuilder 构建动态线程池，只有 threadFactory、threadPoolId 为必填项，其它参数会从配置中心拉取。

项目中使用上述定义的动态线程池，如下所示：

```java
@Resource
private ThreadPoolExecutor messageConsumeDynamicExecutor;

messageConsumeDynamicExecutor.execute(() -> xxx);

@Resource
private ThreadPoolExecutor messageProduceDynamicExecutor;

messageProduceDynamicExecutor.execute(() -> xxx);
```

## ThreadPoolTaskExecutor 适配

Spring 针对 JDK 线程池提供了增强版的 `ThreadPoolTaskExecutor`，Hippo4J 对此进行了适配。

```java
package cn.hippo4j.example;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.support.ResizableCapacityLinkedBlockIngQueue;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean
    @DynamicThreadPool
    public ThreadPoolExecutor dynamicThreadPoolExecutor() {
        String threadPoolId = "message-consume";
        ThreadPoolExecutor dynamicExecutor = ThreadPoolBuilder.builder()
                .threadFactory(threadPoolId)
                .threadPoolId(threadPoolId)
                .corePoolSize(5)
                .maxPoolNum(10)
                .workQueue(new ResizableCapacityLinkedBlockIngQueue(1024))
                .rejected(new ThreadPoolExecutor.AbortPolicy())
                .keepAliveTime(6000, TimeUnit.MILLISECONDS)
                // 等待终止毫秒
                .awaitTerminationMillis(5000)
                // 线程任务装饰器
                .taskDecorator((task) -> {
                    String placeholderVal = MDC.get("xxx");
                    return () -> {
                        try {
                            MDC.put("xxx", placeholderVal);
                            task.run();
                        } finally {
                            MDC.clear();
                        }
                    };
                })
                .dynamicPool()
                .build();
        return dynamicExecutor;
    }

}
```
