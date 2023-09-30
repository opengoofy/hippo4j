---
sidebar_position: 2
---

# 线程池监控

Server 模式默认内置线程池运行时采集和监控功能，如果想要使用 Prometheus + Grafana 的方式可以查看以下内容。

## 线程池监控配置

接下来引入 SpringBoot Actuator。Spring 2.x 一般都有版本指定，所以这里不用写版本号。

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

添加动态线程池监控相关配置：

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
  server:
    port: 29999 # 可选配置，如果不配置该 port，直接使用 ${server.port}
  endpoints:
    web:
      exposure:
        include: '*' # 测试使用，开启了所有端点，生产环境不建议 *
spring:
  dynamic:
    thread-pool:
      monitor:
        enable: true # 是否开启采集线程池运行时数据
        collect-interval: 5000 # 采集线程池运行数据频率
        collect-types: server,micrometer # 采集线程池运行数据的类型。eg：server、micrometer。多个可以同时使用，默认 server
        initial-delay: 10000 # 项目启动后延迟多久进行采集
        thread-pool-types: dynamic # 采集线程池的类型。eg：dynamic、web、adapter。可任意配置，默认 dynamic
```

如果使用 `micrometer` 类型的监控指标，需要添加以下依赖。

```xml
<dependency>
    <groupId>cn.hippo4j</groupId>
    <artifactId>hippo4j-spring-boot-starter-monitor-micrometer</artifactId>
    <version>1.4.3-upgrade</version>
</dependency>
```

项目启动，访问 `http://localhost:29999/actuator/prometheus` 出现 `dynamic_thread_pool_` 前缀的指标，即为成功。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220912220401016.png)

## 配置 Prometheus

通过 Docker 启动 Prometheus 服务。

```shell
docker run -d -p 9090:9090 --name prometheus prom/prometheus
```

添加 Prometheus 抽取数据任务。

```shell
# 进入 prometheus 容器内部
docker exec -it prometheus /bin/sh
# 编辑 prometheus 配置文件
vi /etc/prometheus/prometheus.yml
```

scrape_configs 节点下新添加一个 job，如果 Prometheus 是 Docker 方式部署，`{scrape_configs.static_configs.targets}` 需要写本机的 IP。

```yaml
scrape_configs:
  - job_name: 'dynamic-thread-pool-job'
    scrape_interval: 5s
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: [ '127.0.0.1:29999' ]
```

配置成功后 `exit` 退出容器，并进行 Prometheus 容器重启 `docker restart prometheus`。

访问 Prometheus 控制台 `http://localhost:9090/graph` 路径，能够展示相关指标即为配置成功。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220912221237597.png)

## 配置 Grafana

```shell
docker run -d -p 3000:3000 --name=grafana grafana/grafana
```

访问 Grafana 地址，[http://localhost:3000](http://localhost:3000) 用户名密码：`admin`

Grafana 访问 `http://localhost:3000/datasources` 导入 Prometheus 数据源。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220912221646866.png)

> 如果 Prometheus 为 Docker 方式部署，HTTP URL 需要为本地 IP，比如：http://192.168.1.5:9090

关注公众号 `龙台的技术笔记`，回复：`监控`，获取 Hippo4j Grafana DashBoard JSON 配置。

|                                                    公众号                                                    |                                                           回复关键词                                                           |
|:------------------------------------------------------------------------------------------------------------:|:-------------------------------------------------------------------------------------------------------------------------:|
| ![](https://images-machen.oss-cn-beijing.aliyuncs.com/43_65f6020ed111b6bb3808ec338576bd6b.png?x-oss-process=image/resize,h_300,w_400) | ![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220327171957444.png?x-oss-process=image/resize,h_300,w_400) |

获取到 JSON 文件后，通过 `http://localhost:3000/dashboard/import` 将 JSON 文件导入至 Grafana DashBoard。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220912225627272.png)

下拉框内动态选择创建好的 Prometheus 数据源，并点击 `Import`。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220912225700200.png)

即可使用炫酷的 Hippo4j 动态线程池监控 DashBoard。大家伙儿也可以根据个人喜好进行定制 DashBoard，如果觉得有优化点，欢迎和我联系贡献。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220912225813972.png)

如果项目客户端启动多个示例，动态线程池监控效果图如下：

![](https://images-machen.oss-cn-beijing.aliyuncs.com/20220814_hippo4j_monitor.jpg)
