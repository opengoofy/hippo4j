---
sidebar_position: 3
---

# 接入流程

部署服务端，参考 [部署手册](/docs/user_docs/ops/hippo4j-server-deploy)。

服务端创建 [租户、项目](/community/faq#租户和项目在-hippo4j-中是什么意思) 和线程池记录。

需要注意，项目 ID 需要与配置文件 `{application.name}` 保持一致。

:::note
租户、项目、线程池 ID 如果由多个词组成，建议以 - 进行分割。比如：message-center。
:::

## Hippo4j 配置

SpringBoot Pom 引入 Hippo4j Starter Jar。

```xml
<dependency>
    <groupId>cn.hippo4j</groupId>
    <artifactId>hippo4j-spring-boot-starter</artifactId>
    <version>1.4.2</version>
</dependency>
```

启动类上添加注解 `@EnableDynamicThreadPool`。

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
spring:
  profiles:
    active: dev
  application:
    # 服务端创建的项目 id 需要与 application.name 保持一致
    name: dynamic-threadpool-example
  dynamic:
    thread-pool:
      # 服务端地址
      server-addr: http://localhost:6691
      # 用户名
      username: admin
      # 密码
      password: 123456
      # 租户 id, 对应 tenant 表
      namespace: prescription
      # 项目 id, 对应 item 表
      item-id: ${spring.application.name}
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

通过 ThreadPoolBuilder 构建动态线程池，只有 threadFactory、threadPoolId 为必填项，其它参数会从 hippo4j-server 服务拉取。

:::note
创建线程池时建议填充实际的参数。如果在连接 Hippo4j Server 端失败时，会使用填充配置创建线程池。
:::

项目中使用上述定义的动态线程池，如下所示：

```java
@Resource
private ThreadPoolExecutor messageConsumeDynamicExecutor;

messageConsumeDynamicExecutor.execute(() -> xxx);

@Resource
private ThreadPoolExecutor messageProduceDynamicExecutor;

messageProduceDynamicExecutor.execute(() -> xxx);
```

