---
sidebar_position: 1
---

# 拒绝策略自定义

Hippo4j 通过 SPI 的方式对拒绝策略进行扩展，可以让用户在 Hippo4j 中完成自定义拒绝策略实现。

## Hippo4j Server 拒绝策略扩展

自定义拒绝策略，实现 `CustomRejectedExecutionHandler` 接口，示例如下：

```java
public class ErrorLogRejectedExecutionHandler implements CustomRejectedExecutionHandler {

    @Override
    public Integer getType() {
        return 12;
    }

    @Override
    public RejectedExecutionHandler generateRejected() {
        return new CustomErrorLogRejectedExecutionHandler();
    }

    public static class CustomErrorLogRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("线程池抛出拒绝策略");
        }
    }
}
```

创建 `src/main/resources/META-INF/services` 目录，创建 SPI 自定义拒绝策略文件 `cn.hippo4j.common.executor.support.CustomRejectedExecutionHandler`。

`cn.hippo4j.common.executor.support.CustomRejectedExecutionHandler` 文件内仅放一行自定义拒绝策略全限定名即可，示例：

```text
cn.hippo4j.example.core.handler.ErrorLogRejectedExecutionHandler
```

创建、修改线程池页面选择 `CustomRejectedPolicy（自定义 SPI 策略）`。

![](https://images-machen.oss-cn-beijing.aliyuncs.com/image-20220813173907814.png)

拒绝策略触发时，完成上述代码效果，仅打印异常日志提示。

```text
2022-08-01 21:27:49.515 ERROR 48928 --- [ateHandler.test] r$CustomErrorLogRejectedExecutionHandler : 线程池抛出拒绝策略
```

:::note
具体参考 `hippo4j-example/hippo4j-spring-boot-starter-example` 模块。
:::
