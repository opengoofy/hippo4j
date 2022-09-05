---
sidebar_position: 4
---

# 服务端配置

`hippo4j.core.clean-history-data-enable`

是否开启线程池历史数据清洗，默认开启。

`hippo4j.core.clean-history-data-period`

线程池历史数据保留时间，默认值：30，单位分钟。

服务端会保留这个配置时间的数据，超过这个时间则会被清理。比如按照默认值 30 分钟来说，12:00 收集到的数据，12:30 就会被清理删除。

`hippo4j.core.monitor.report-type`

客户端监控上报服务端类型，可选值：http、netty，默认 http。服务端开启 netty 配置后，需要在客户端对应开启才可生效。用来应对大量动态线程池监控场景。
