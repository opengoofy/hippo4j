---
sidebar_position: 3
---

# 个性化配置

以下所述特性自 hippo4j-config v1.4.2 及以上版本提供，由 hippo4j 核心开发者 [@pizihao](https://github.com/pizihao) 完成相应功能开发。

## 需求背景

**1）容器及三方框架线程池自定义启用**

最初设计容器线程池和三方框架线程池的动态变更是和启动无关的。也就是说，启动时不会根据配置文件中相关参数去修改两者对应的线程池配置。

这么设计的初衷是因为，不想让 hippo4j 过多的去介入框架原有的功能。因为容器和三方框架都支持线程池参数的自定义。

也就造成，可能你在配置中心配置了对应的容器和三方框架线程池参数，启动时是无效的。但当修改配置文件任一配置，容器和三方框架线程池配置将生效。

为了更好的用户体验，决定加入启用标识来控制：是否在项目初始化启动时，对容器和三方框架线程池参数进行修改。

**2）客户端集群个性化配置**

大家都知道，hippo4j-config 是依赖配置中心做线程池配置动态变更。这种模式有一种缺点：改动配置文件后，所有客户端都会变更。

有些小伙伴希望 hippo4j-config 能够像 hippo4j-server 一样，能够针对单独的客户端进行配置变更。

## 容器及三方框架线程池自定义启用

容器及三方框架线程池添加启用配置，为了保持统一，动态线程池配置中也有该参数配置。配置项默认开启。

```yaml
spring:
  dynamic:
    thread-pool:
      tomcat:
        enable: true
      executors:
        - thread-pool-id: message-consume
          enable: false
      adapter-executors:
        - threadPoolKey: 'input'
          enable: true
```

## 客户端集群个性化配置

分别在动态线程池、容器线程池以及三方框架线程池配置下增加 `nodes` 配置节点，通过该配置可匹配需要变更的节点。

```yaml
spring:
  dynamic:
    thread-pool:
      tomcat:
        nodes: 192.168.1.5:*,192.168.1.6:8080
      executors:
      - thread-pool-id: message-consume
        nodes: 192.168.1.5:*
      adapter-executors:
        - threadPoolKey: 'input'
          nodes: 192.168.1.5:*
```

来一段代码方法中的注释，大家就基本明白如何使用了。

```java
/**
 * Matching nodes<br>
 * nodes is ip + port.Get 'nodes' in the new Properties,Compare this with the ip + port of Application.<br>
 * support prefix pattern matching. e.g: <br>
 * <ul>
 *     <li>192.168.1.5:* -- Matches all ports of 192.168.1.5</li>
 *     <li>192.168.1.*:2009 -- Matches 2009 port of 192.168.1.*</li>
 *     <li>* -- all</li>
 *     <li>empty -- all</li>
 * </ul>
 * The format of ip + port is ip : port.
 */
```

`nodes` 可与 `enable` 同时使用。如此，基于配置中心的动态线程池实现方式，将能够更方便的支持个性化需求。
