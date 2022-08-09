---
sidebar_position: 2
---

# 常见问题


- <a href="#租户和项目在-hippo4j-中是什么意思">租户和项目在 Hippo4J 中是什么意思</a>
- <a href="#示例项目为什么会有跨域请求">示例项目为什么会有跨域请求</a>
- <a href="#更新代码后运行时服务端sql报错">更新代码后运行时服务端SQL报错</a>
- <a href="#okhttp3-call-timeout-方法不存在">okHttp3 call.timeout() 方法不存在</a>
- <a href="#生产环境如何不启用动态线程池">生产环境如何不启用动态线程池</a>
- <a href="#server-端宕机会影响-client-运行么">Server 端宕机会影响 Client 运行么</a>
- <a href="#hippo4j-的发布方式是怎样的-如何选择正确的版本">Hippo4J 的发布方式是怎样的？如何选择正确的版本</a>
- <a href="#群机器人接受不到通知报警">群机器人接受不到通知报警</a>
- <a href="#设置线程池参数优先级问题">设置线程池参数优先级问题</a>
- <a href="#线程池实例中修改队列容量参数问题">线程池实例中修改队列容量参数问题</a>

## 租户和项目在 Hippo4J 中是什么意思

Hippo4J 按照租户、项目、线程池的维度划分。

举个例子，小编在一家公司的公共组件团队，团队中负责消息、短链接网关等项目。公共组件是租户，消息或短链接就是项目。

## 示例项目为什么会有跨域请求

~~正常大家在部署时，服务端项目和客户端都在同一网络下，进行内网通信，是没有问题的。~~

~~因为示例项目中，服务端部署在外网，而客户端注册到服务端 IP 是内网的，所以不通。~~

~~涉及功能：线程池实例-查看、编辑，容器线程池。~~

1.2.0 版本后，服务端访问客户端已变成，浏览器访问服务端，服务端转发客户端的形式完成调用，跨域问题已解决。

## 更新代码后运行时服务端SQL报错

如果更新代码运行功能出错，大概率是因为项目新增或修改了表结构。如版本升级迭代涉及数据库表变更，会额外提供 SQL 变更文件。

如若第一次使用，初始化 SQL 脚本地址：[hippo4j_manager.sql](https://github.com/longtai-cn/hippo4j/blob/develop/hippo4j-server/conf/hippo4j_manager.sql)。

> 友情提示：每次执行数据库表或数据变更时，一定要保持提前备份的好习惯。

## okHttp3 call.timeout() 方法不存在

请确保 okHttp3 依赖版本号 >= 3.12.0

```xml
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>3.12.0</version>
</dependency>
```

## 生产环境如何不启用动态线程池

测试环境已经引入 Hippo4J，暂时不打算上线生产环境。

生产环境指定配置 `spring.dynamic.thread-pool.enable=false`，测试环境和生产环境配置就会隔离。

## Server 端宕机会影响 Client 运行么

不会。Client 端包含对 Server 端的健康检查机制，Server 端不可用时会停止交互，检查到可用时重新建立连接交互。

## Hippo4J 的发布方式是怎样的？如何选择正确的版本

Hippo4J 发布时可能会涉及到两端发布，分别是 Server 和 Starter。如无特殊说明，**每一次的版本升级将兼容上一版本代码**。

- 如涉及 Server 发布，会在 [发布列表页面](https://github.com/longtai-cn/hippo4j/releases) 创建最新的发行版本；
- 如涉及 Starter 发布，将直接推送 Starter Jar 至中央仓库，Server 包版本不变。

## 群机器人接受不到通知报警

如果是钉钉机器人，需在机器人配置自定义关键字，才可发送成功。如下所示：
   
![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220530200133377.png?x-oss-process=image/resize,h_500,w_800)

如果使用 hippo4j-server，请检查在 hippo4j-server 添加的报警通知记录，是否在客户端项目启动前，因为客户端只有在启动时会去 hippo4j-server 拉取报警通知记录。

重启客户端项目，会重新拉取最新报警推送配置，问题解决。

## 设置线程池参数优先级问题
- 当使用`@DynamicThreadPool`进行修饰的方法中和在管理界面设置中同时存在的话，则管理界面设置的优先级最高；
- 如果连接service端失败的话，使用`@DynamicThreadPool`进行修饰设置的优先级最高。

## 线程池实例中修改队列容量参数问题

在线程池管理中添加时，只有当选择队列类型为`ResizableCapacityLinkedBlockingQueue`时，后续再进行修改容量大小时才会实时的刷新修改成功。