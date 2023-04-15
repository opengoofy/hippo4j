---
title: 更新日志
sidebar_position: 5
---

## 1.5.0 (Apr 15, 2023)

这是一个大的功能增强版本，建议按照当前版本升级。具体信息可查看 Release 标签地址：[1.5.0](https://github.com/opengoofy/hippo4j/milestone/14?closed=1)

**Use Change**

- [Config] 配置属性中容器线程池配置命名改为 `spring.dynamic.thread-pool.web`。

- [Server] 为了使语义更加明确，当前版本中线程池执行超时时间的最低可设置为-1，表示不设置超时。需要注意的是，如果客户端使用的是低于1.5.0的版本，可能会存在不兼容的情况。

- [Common] 为了使语义更加明确，废弃 `ThreadPoolBuilder#maxPoolNum`，新增加 `ThreadPoolBuilder#maximumPoolSize`，原 API 还可以使用，将在后续版本删除。

**Feature**

- [#1124 ] Config 模式支持 Web 容器配置修改通知 @yanrongzhen
- [#1148 ] 优化执行时间超时 executeTimeOut 为空或 <= 0 的场景 @sususama
- [#1128 ] 控制台前端国际化开发 @Svamei @ PleasePerfunctory
- [#1068 ] 默认线程池配置填充至线程池实例 @magestacks
- [#1055 ] 适配第三方自定义线程池，支持外部SPI方式扩展线程池类型 @lingfengcoder
- [#547 ] 支持 Consul 配置中心动态调整参数 @road2master
- [#624 ] 添加 Dubbox 线程池监控及动态变更 @iwangjie
- [#977 ] 增加 ROLE_MANAGE 角色，主要负责线程池变更审核 @magestacks
- [#821 ] 开发用户修改密码功能 @wulangcode

**Refactor**

- [#1092 ] 关于扩展点 SPI 方案的重构 @yanrongzhen
- [#1025 ] 优化BlockingQueueTypeEnum实现 @lianyiwuming
- [#1015 ] 重构Web线程池配置文件命名 @yanrongzhen
- [#1071 ] ThreadPoolBuilder提供全新的最大线程池API @jjiey
- [#1058 ] 客户端引入多个 Web 容器依赖导致 WebThreadPoolService 报错  @Createsequence
- [#844 ] 移除 alibaba-dingtalk-service-sdk @baymax55

**Bug**

- [#1085 ] DynamicThreadPoolRegisterParameter#threadFactory序列化异常 @magestacks
- [#1163 ] Adapter 适配三方线程池启动顺序错误 @magestacks
- [#1134 ] Web容器启动时刷新最新配置失败 @yanrongzhen
- [#1129 ] Prometheus 不能正常采集 Hystrix 线程池 queue.capacity 数据 @jjiey
- [#1121 ] 线程池实例包含已关闭客户端节点 @zjHe
- [#1106 ] springBoot 1.5.x适配Web容器线程池调整 @yanrongzhen
- [#1114 ] ZipkinExecutorAdapter 可能出现空指针异常 @yult97
- [#1074 ] Hystrix Adapter 支持 hippo4j config @jjiey
- [#1040 ]线程池参数默认配置与文档描述不符 @wulangcode
-  [#1002 ] SpringBoot Nacos 动态刷新错误使用 SpringCloud Nacos 类 @BigXin0109
- [#990 ] TTL 装饰的线程池无法执行等待任务完成插件 @Createsequence
- [#966 ] Alibaba Dubbo 无法获取线程池运行状态 @iwangjie

**Optimize**

- [#1159 ] 优化飞书和企业微信失败处理逻辑 @ITXiaoShiTou
- [#1156 ] 线程池通知管理调整查询租户&项目&线程池默认10条 @tudou0806
- [#1069 ] 线程池执行超时时间最小值支持 -1 @Ronan1024
- [#1111 ] 核心阻塞队列枚举 BlockingQueueTypeEnum.createBlockingQueue(String blockingQueueName, Integer capacity)抛出NPE问题修复 @LiXuemin
- [#1072 ] 提供公用方法安全设置 corePoolSize 和 maxPoolSize @jjiey
- [#1051 ] 前端国际化登录页面中文错误 @xiaobaisdz
- [#1001 ] 容器停止时，若线程池在等待关闭时，有任务触发超时告警会抛异常警类 @Createsequence
- [#1024 ] 客户端与 Hippo4j revision 冲突 @baymax55
- [#720 ] 修复 Dubbo 错误的日志打印 @baymax55
- [#992 ] 优化 Hippo4j Banner 打印时可能存在的空指针 @wulangcode
- [#976 ] 注册线程池默认指定过期时间和线程工厂 @magestacks
- [#964 ] 长轮询时配置未变更返回304状态码适配低版本客户端 @wulangcode
- [#942 ] 修改 MyBatis-Plus 数据库方言配置 @li-xiao-shuang
- [#959 ] ThreadPoolPluginRegisterPostProcessor 打印过多 WARN 日志 @Createsequence
- [#960 ] ThreadPoolPluginRegisterPostProcessor 实现 BeanPostProcessor 默认方法 postProcessBeforeInitialization @Createsequence


## 1.4.3 (Nov 06, 2022)

这是一个功能增强版本，修复了少量 BUG。建议按照当前版本升级。具体信息可查看 Release 标签地址：[1.4.3](https://github.com/opengoofy/hippo4j/milestone/13?closed=1)

**Use Change**

- 重构线程池监控，配置层级和命名改变
- 如果使用钉钉报警，关键字【警报】修改为【告警】

**Feature**

- 重构 Spring 后置处理器创建动态线程池逻辑
- 官网开启多版本化功能
- 官网支持国际化，en-US
- 适配线程池延迟初始化 @wulangcode
- 添加 Codecov 相关代码覆盖率指标
- 项目优雅关闭时停止运行状态采集

**Refactor**

- DynamicThreadPoolExecutor 重构，增加插件扩展逻辑 @Createsequence
- 重构线程池监控，新增容器和三方框架线程池监控
- 重构服务端包目录，聚合 hippo4j-server 相关 module

**Bug**

- dubbo 线程池无法获取运行信息 @iwangjie
- 线程池检查活跃度报警取值错误 @maxisvest
- 动态线程池修改多次后队列提示信息丢失
- docker部署 mysql启动报错H2驱动
- docker-startup.sh的mysql配置多个“-” @Malcolmli
- 动态注册线程池队列容量赋值错误
- 飞书超时类型告警不存在 Trace 信息时发送错误 @mageeric

**Optimize**

- 修改报警文案，【警报】修改为【告警】 @wulangcode
- 自动选择H2数据库的存储路径 @iwangjie
- 服务端在客户端后面启动，依旧支持长轮训 @wulangcode
- 配置未发生变更时，长轮询返回 304 @wulangcode
- discovery服务Lease类中判断过期时间需要多等一个duration @w-jirong
- 优化 ThreadPoolBuilder#maxPoolNum 核心线程不得大于最大线程 @wulangcode
- hippo4j console ui 迁移至本项目
- 查询 Web 线程池列表添加框架标识
- 优化 H2 初始化逻辑

## 1.4.2 (Oct 18, 2022)

这是一个功能增强版本，修复了少量 BUG。建议按照当前版本升级。具体信息可查看 Release 标签地址：[1.4.2](https://github.com/opengoofy/hippo4j/milestone/12?closed=1)

**Feature**

- 强制指定客户端注册的 ip + port
- 支持 spring-cloud-tencent Polaris 线程池动态更新 @weihubeats
- 服务启动时加载 MySQL、H2 数据库初始化语句
- Adapter 初始化覆盖核心参数 @pizihao
- Server 端新增是否开启认证模式 @baymax55

**Refactor**

- 替换底层网络工具类 OkHttp @yanrongzhen
- 全局移除 commons-lang3 工具包依赖 @yanrongzhen
- 去除三方工具类依赖 @pizihao
- 全局移除 Guava 工具包依赖 @road2master
- DockerFile 基于 H2 数据库重新构建 @BigXin0109

**Bug**

- Dubbo 2.7.15 无法获取线程池引用 @iwangjie
- 动态线程池报警参数颠倒 @jinlingmei

**Optimize**

- 线程池实例运行数据采集，如果线程池id不存在，且长度超长，会报异常 @Gdk666
- 项目中动态线程池数量为空时，存在 CPU 空转情况
- 客户端注册服务端失败，输出服务端返回信息 @wulangcode
- 调整数据库项目 id 和线程池 id 字段长度
- 增加代码检查工具 maven-checkstyle-plugin
- 调整控制台监控图表颜色展示

## 1.4.1 (Sep 12, 2022)

这是一个功能增强版本，修复了若干 BUG。建议按照当前版本升级。具体信息可查看 Release 标签地址：[1.4.1](https://github.com/opengoofy/hippo4j/milestone/11?closed=1)

**Feature**

- 支持 H2 数据库 @weihubeats
- 动态线程池配置变更时，支持单个、多个或全部节点变 @pizihao
- 增加线程池活跃度和容量报警可选择关闭
- @DynamicThreadPool 线程池不存在则创建 @shanjianq
- 支持 ETCD 配置中心动态调整参数 @weihubeats
- 创建动态线程池支持 spring 线程池 @BigXin0109
- 线程池实例变更增加执行超时时间
- 线程池相关查询页面增加阻塞队列属性
- 定义动态线程池时，抽象默认配置
- 提供 ExecutorContext 封装上下文细节 @road2master
- Docker 制作服务端镜像，帮助开发者快速启动 @BigXin0109
- RabbitMQ 适配器增加多个 MQ 数据源 @weihubeats

**Bug**

- 动态线程池设置关闭时启动报错 @dousp
- ExecutorTtlWrapper 类型的 Executor 不生效 @BigXin0109
- Undertow 获取 WebServer 类型参数异常 @shining-stars-lk
- 修复线程池核心、最大线程数校验限制
- ByteConvertUtil#getPrintSize 单位转换错误 @onesimplecoder
- 创建线程池单选框选择错误
- ReflectUtil#getFieldsDirectly missing fields @BigXin0109
- 本地代码中设置的 capacity 无效 @BigXin0109
- 服务端线程池超时时间存在拆箱空指针异常 @oreoft
- 未读取服务端返回执行超时时间属性
- ResizableCapacityLinkedBlockingQueue#put 当前元素数量大于 capacity 未阻塞

**Optimize**

- 长轮询任务判断逻辑优化 @shining-stars-lk
- 线程池存在实例不允许删除线程池 @shanjianq
- 优化租户、项目列表展示排版
- 通知报警模块项目和线程池下拉查询排序修改
- 动态线程池拒绝策略触发，以异步的方式报警
- 优化框架中线程池工厂产生的线程名称 @road2master

## 1.4.0 (Aug 16, 2022)

`hippo4j server` 兼容历史低版本，`hippo4j config` 中部分属性名进行了调整，请参考 [hippo4j config 快速开始](https://hippo4j.cn/docs/user_docs/getting-started/hippo4j-core-start)。

注意事项：
1. 如果是对已运行 hippo4j server 升级，执行 `/conf/sql-upgrade` 目录下对应的升级脚本。
2. 需客户端在 1.4.0 及以上版本才可在 hippo4j server 设置线程执行超时时间属性。

**Feature**

- 添加 Alibaba Dubbo 线程池监控及动态变更
- hippo4j server 支持任务执行超时时间动态修改
- 阿里 TTL 框架线程池适配
- 添加动态线程池自动注册功能
- 订阅回调线程池参数变更
- 动态线程池监控增加 SPI 自定义功能
- hippo4j server 支持多种线程池监控方式，例如 Prometheus
- 通知相关参数添加动态变更功能

**Bug**

- 线程池变更：executeTimeOut 变更极端情况下会出现异常
- 用户登录时候，如果输入了不存在的用户名，后台报空指针异常
- 修复了对 spring-boot 服务中 tomcat 线程池的兼容问题
- 排除 Tomcat Jar 使用 Undertow 启动报错

**Optimize**

- hippo4j-core-spring-boot-starter 模块修改名称为 hippo4j-config-spring-boot-starter
- 拆分容器线程池子页面：Tomcat、Undertow、Jetty
- 服务端访问客户端时对 URL 转码
- MyBatisPlus 修改全局填充方法优化
- 控制台线程池列表下拉框默认正序
- 控制台线程池实例菜单，对于非可修改容量队列外，不允许修改队列容量
- 动态线程池控制台功能变更
- 租户和项目列表分页查询按照创建时间倒序展示
- 线程池监控页面图表 UI 优化
- 设置 maven-gpg-plugin 插件默认不执行
- 前端控制台相关搜索条件添加必填提示
- hippo4j 消息通知 & 报警抽象优化
- 配置中心未配置线程池启动报错
- 控制台线程池报警 UI 以及功能优化
- Web、框架线程池编辑弹框 UI 优化
- 线程池添加、编辑页面 UI 优化
- 线程池运行详情页前端 UI 优化

**Refactor**

- 删除自定义日志组件
- 线程池监控功能重构
- hippo4j core 配置中心生效判断重构
- 配置变更通知 & 报警通知重构
- Web 容器线程池适配迁移 hippo4j-adapter

## 1.3.1 (July 17, 2022)

注：这是一个兼容历史版本的小范围升级。

**Feature**

- 控制台新增线程池功能设置为 Admin 权限
- 添加 Hystrix 线程池监控及动态变更
- 添加 Netty 上传动态线程池监控数据方式
- 添加 GitHub Actions CI 流程
- 添加 Spring Kafka 示例项目
- Tomcat 版本号 >= 9.0.55 线程池适配

**Refactor**

- 更多线程池拆分子目录页面

**Optimize**

- hippo4j core 添加 banner 打印
- 优化可变更容量阻塞队列名称

**BUG**

- Apollo 配置修改延迟了一个版本
- Spring Boot 环境下使用 hippo4j-core 接入，配置中心使用 nacos；启动时提示 ConfigService not found

查看 1.3.1 版本发布：https://github.com/mabaiwan/hippo4j/milestone/9

## 1.3.0 (June 06, 2022)

1.3.0 发布 **适配三方框架的基础框架**。

目前已完成 **Dubbo、RabbitMQ、RocketMQ、RocketMQSpringCloudStream** 的线程池适配，后续还会接入 **Kafka、Hystrix** 等框架或中间件的线程池适配。

注：这是一个兼容历史版本的重大升级。

**Feature**

- 添加 RabbitMQ 线程池监控及动态变更
- 添加 RocketMQ 线程池监控及动态变更
- 添加 Dubbo 线程池监控及动态变更
- 添加 SpringCloud Stream RocketMQ 消费线程池监控及动态变更

**Refactor**

- 重构容器线程池查询及修改功能
- 优化配置中心触发监听后，所执行的数据变更逻辑

**Optimize**

- 前端控制台删除无用组件
- 服务端页面字段未显示中文
- 控制台 UI 优化
- 修改线程池实例后实时刷新列表参数
- 容器线程池编辑仅限 Admin 权限
- SpringBoot Starter 变更包路径

**BUG**

- 修复 SpringBoot Nacos 动态刷新不生效
- 报警配置 alarm=false 不配置通知报警平台和接收人报错

## 1.2.1 (May 07, 2022)

**BugFix**

- apollo 动态配置不生效
- 修复 hippo4j-core 后置处理器创建线程池问题
- 重构 hippo4j-core spring 后置处理器逻辑
- 优化ThreadPoolNotifyAlarmHandler下的空指针异常
- 修复线程池核心、最大线程数变更问题
- startup.cmd 未正常读取 conf 配置文件

**Optimize**

- 配置文件中字段歧义
- 修改代码中历史网址
- InstanceInfo 的 groupKey 参数重复设置
- ConfigFileTypeEnum 枚举字段添加注释
- 线程资源通过线程池创建，不允许自行显示创建线程
- Guava 版本升级至 30.0-jre 及以上版本
- SystemClock 替换 System.currentTimeMillis()
- 添加代码格式化插件 Spotless
- 修改线程池文案

## 1.2.0 (Mar 13, 2022)

**Feature**

- hippo4j-core线程池资源对接 Prometheus 监控
- hippo4j-core 支持 Zookeeper
- hippo4j-core 支持 Apollo

**Optimize**

- 适配非 Web SpringBoot 项目使用 Hippo4j
- 优化报警通知
- 修复在 JDK 小版本中的兼容性问题

**BugFix**

- server 端查看容器线程池，参数为 null
- 重构线程池查看及容器线程池查看等交互
- 修复引入 hippo4j-spring-boot-starter 后，运行单元测试报错
- 修复可能出现的空指针异常

## 1.1.0 (Mar 13, 2022)

Hippo4j 线程池框架 1.1.0 RELEASE 版本，添加了 Hippo4j-Core（依赖配置中心的动态线程池）.

**Feature**

- 删除 DynamicThreadPoolExecutor 内代码实现，仅通过线程池扩展点进行扩展
- 通过动态代理实现线程池拒绝策略执行次数统计
- 抽象通知报警消息模块
- 抽象 hippo4j 核心组件，不依赖 server 端即可完成动态调参、监控、报警等功能
- 前端删除线程池按钮添加 Admin 权限
- 添加线程池任务运行超长报警
- 容器线程池支持 Undertow
- 容器线程池支持 Jetty
- 重构服务端异常体系

**Optimize**

- 前端项目 Token 失效跳转登录页
- 优化 Server 启动脚本日志输出
- 优化前端按钮权限控制粒度
- 优化线程池报警推送文案
- 前端弹框样式优化
- 适配低版本 SpringBoot Bind
- 优化消息通知模块

**BugFix**

- Duplicate entry 'xxx' for key 'uk_configinfo_datagrouptenant'

## 1.0.0 (Feb 01, 2022)

**Feature**

- 线程池运行堆栈查看
- 扩展 Web 容器线程池动态调参、监控

**Optimize**

- 删除高版本 SpringBoot Api
- ListableBeanFactory#findAnnotationOnBean SpringBoot 低版本适配
- 优化客户端关闭时调用服务端钩子函数
- 线程池实例参数弹框添加实例 ID 和线程池状态
- 补充线程池替换 Hippo4j 文档
- 1.5.x springboot 引入hippo4j-spring-boot-starter配置项，bean初始化失败
- 优化线程池参数编辑合理性校验
- BaseInstanceRegistry 读写锁重构

**BugFix**

- 本地项目线程池实例缓存无法精确清理
- 线程池实例页面多实例不同 Active 展示错误
- 创建动态线程池逻辑判断修复
- 创建动态线程池增强参数未设置
- 控制消息推送报警频率的方法有并发安全的问题
- tomcat线程池上下文获取失败
