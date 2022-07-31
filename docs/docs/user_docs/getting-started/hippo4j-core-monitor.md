---
sidebar_position: 2
---

# hippo4j core 线程池监控

已完成 hippo4j-core 的 [接入工作](/docs/user_docs/getting-started/hippo4j-core-start) 。

## 安装 Grafana + Prometheus

```shell
docker run -d -p 9090:9090 --name prometheus prom/prometheus
```

```shell
docker run -d -p 3000:3000 --name=grafana grafana/grafana
```

访问 grafana 地址，[http://localhost:3000](http://localhost:3000) 用户名密码：`admin`

## 线程池监控

引入 actuator。spring 2.x 一般都有版本指定，所以这里不用写版本号。

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

添加相关配置。

```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
  server:
    port: 29999 # 自选
  endpoints:
    web:
      exposure:
        include: '*' # 测试使用，开启了所有端点，生产环境不建议 *
spring:
  dynamic:
    thread-pool:
      collect-type: metric
```

Prometheus 配置任务，配置成功后需重启。

```yaml
- job_name: 'dynamic-thread-pool-job'
    scrape_interval: 5s
    metrics_path: '/actuator/prometheus'
    static_configs:
    - targets: ['127.0.0.1:29999'] # 如果是 docker 部署，这里需要写本机的 IP
```

Grafana 导入数据源。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220328231812090.png)

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220328231849537.png)

Grafana DashBoard 配置。

关注公众号 `龙台的技术笔记`，回复：`监控`，获取 DashBoard JSON。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/43_65f6020ed111b6bb3808ec338576bd6b.png)


![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220327171957444.png)

获取到 JSON 文件后，导入至 Grafana。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220327171125638.png)

即可使用 Hippo4j 线程池监控大屏。

![](./img/grafana-monitor.jpg)
