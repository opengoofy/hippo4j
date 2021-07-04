<p align="center">
	<strong>ThreadPool, so easy.</strong>
</p>
<p align="center">

<img src="https://img.shields.io/badge/程序员-龙台-blue.svg" />

<a target="_blank" href="http://mp.weixin.qq.com/s?__biz=Mzg4NDU0Mjk5OQ==&mid=100007311&idx=1&sn=d325c1a509d6ee89469a1134ac0a8cf5&chksm=4fb7c6f778c04fe111e9cf52723675b8e8cbbbf9e848741a5d9c20620ff6c778b6613e021a34&scene=18#wechat_redirect">
     <img src="https://img.shields.io/badge/公众号-龙台 blog-yellow.svg" />
</a>

<a target="_blank" href="https://github.com/longtai94/dynamic-thread-pool/blob/develop/LICENSE">
     <img src="https://img.shields.io/badge/⭐-github-blue.svg" />
</a>

<img src="https://img.shields.io/badge/JDK-1.8-violet.svg" />

<img src="https://img.shields.io/badge/release-v1.0.0-green.svg" />

<img src="https://img.shields.io/github/stars/longtai94/dynamic-thread-pool.svg" />

</p>

<br/>

我有一个 IDEA，叫动态线程池监控，主意来源于美团技术公众号 [点击查看美团线程池文章](https://tech.meituan.com/2020/04/02/java-pooling-pratice-in-meituan.html)

<br/>

看了文章后深受感触，再加上最近线上线程池的不可控以及不可逆等问题，想做出一个兼容性、功能性、易上手等特性集于一身的的开源项目。目标还是要有的，虽然过程可能会艰辛

<br/>

目前这个项目是由作者独立开发，时间在下班后、周六天等。具体什么时候能发布 1.0 版本不好说，需要看实际的开发情况

<br/>

根据目前的想法，美团技术文章中支持的特性，DTP（Dynamic Thread Pool）项目都会兼容进去，可能部分会因为作者技术有限，无法兼容

比如：

- 修改阻塞队列长度

- 修改线程池核心线程数、最大线程数、线程存活时长...

- 线程池详细信息监控

- 线程池负载报警

- ...

<br/>

项目不会强依赖某个不通用的中间件，比如配置中心：Nacos、Apollo、Consul... 有可能会将组件全部支持，自定义选择使用

<br/>

## 本地部署

目前动态线程池功能已经完成，大家可以直接把代码拉到本地运行

<br/>

> 项目数据库连接是作者个人 ECS Docker 搭建的 MySQL，防君子不防小人，大家直接使用即可

<br/>

启动 server 模块下 ServerApplication 启动类

<br/>

启动 example 模块下 ExampleApplication 启动类

<br/>

可以通过调用接口修改线程池配置

<br/>

修改请求如下，**在做示例时不要改动 namespace、itemId、tpId**

<br/>

POST http://localhost:6691/v1/cs/configs

<br/>

```json
{
    "namespace": "common",
    "itemId": "message-center",
    "tpId": "message-consume",
    "coreSize": 3,
    "maxSize": 10,
    "queueType":9,
    "capacity":1000,
    "keepAliveTime" : 1000,
    "isAlarm":2,
    "capacityAlarm":80,
    "livenessAlarm":80
}
```

<br/>

接口调用成功后，观察 example 控制台日志输出，日志输出包括不限于此信息即为成功

<br/>

[🚀] Changed thread pool. coreSize :: x, maxSize :: x, queueType :: x, capacity :: x, keepAliveTime :: x
