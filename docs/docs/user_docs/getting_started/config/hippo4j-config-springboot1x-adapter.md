---
sidebar_position: 5
---

# 适配SpringBoot1x

目前已支持 Nacos、Apollo 配置中心适配 SpringBoot 1.5.x 版本。

```xml
<dependency>
    <groupId>cn.hippo4j</groupId>
    <artifactId>hippo4j-config-spring-boot-1x-starter</artifactId>
    <version>1.4.2</version>
</dependency>
```
 
Nacos SpringBoot 配置如下：

```yaml
spring:
  cloud:
    nacos:
      config:
        ext-config:
          - data-id: hippo4j-nacos.yaml
            group: DEFAULT_GROUP
            refresh: true
        server-addr: 127.0.0.1:8848
  dynamic:
    thread-pool:
      config-file-type: yml
      nacos:
        data-id: hippo4j-nacos.yaml
        group: DEFAULT_GROUP
```

Apollo SpringBoot 配置如下：

```yaml
apollo:
  autoUpdateInjectedSpringProperties: true
  bootstrap:
    eagerLoad:
      enabled: true
    enabled: true
    namespaces: application
  meta: http://127.0.0.1:8080
app:
  id: dynamic-threadpool-example
spring:
  dynamic:
  thread-pool:
    apollo:
      namespace: application
```

动态线程池通用配置如下：

```yaml
management:
  context-path: /actuator
  security:
    enabled: false
server:
  port: 8091
  servlet:
    context-path: /example
spring:
  application:
    name: dynamic-threadpool-example
  dynamic:
    thread-pool:
      banner: true
      check-state-interval: 5
      collect-type: micrometer
      config-file-type: properties
      enable: true
      executors:
      - active-alarm: 80
        alarm: true
        allow-core-thread-time-out: true
        blocking-queue: LinkedBlockingQueue
        capacity-alarm: 80
        core-pool-size: 1
        execute-time-out: 1000
        keep-alive-time: 6691
        maximum-pool-size: 1
        notify:
          interval: 8
          receives: chen.ma
        queue-capacity: 1
        rejected-handler: AbortPolicy
        thread-name-prefix: message-consume
        thread-pool-id: message-consume
      - active-alarm: 80
        alarm: true
        allow-core-thread-time-out: true
        blocking-queue: LinkedBlockingQueue
        capacity-alarm: 80
        core-pool-size: 1
        execute-time-out: 1000
        keep-alive-time: 6691
        maximum-pool-size: 1
        notify:
          interval: 8
          receives: chen.ma
        queue-capacity: 1
        rejected-handler: AbortPolicy
        thread-name-prefix: message-produce
        thread-pool-id: message-produce
      notify-platforms:
      - platform: WECHAT
        token: ac0426a5-c712-474c-9bff-72b8b8f5caff
  profiles:
    active: dev
```

具体 Demo 运行请参考以下示例模块，已验证对应线程池动态变更、报警以及运行时监控功能。

- `/hippo4j-config-nacos-spring-boot-1x-starter-example`
- `hippo4j-example/hippo4j-config-apollo-spring-boot-1x-starter-example`
