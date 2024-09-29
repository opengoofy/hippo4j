package cn.hippo4j.example.server.adapter.dubbo.springboot3.consumer;

import cn.hippo4j.example.server.adapter.dubbo.springboot3.DemoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class DubboThreadTestService {


    @DubboReference(timeout = 2000, version = "1.0.0")
    private DemoService demoService;

    @PostConstruct
    public void init() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() ->
                executorService.execute(() -> {
                    try {
                        String result = demoService.sayHello("world");
                        log.info("返回信息：{}", result);
                    } catch (Exception e) {
                        log.error("dubbo接口抛出错误：", e);
                    }
                }), 500, 500, TimeUnit.MILLISECONDS);
    }
}
