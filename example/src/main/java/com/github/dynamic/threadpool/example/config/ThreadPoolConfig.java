package com.github.dynamic.threadpool.example.config;

import com.github.dynamic.threadpool.starter.core.GlobalThreadPoolManage;
import com.github.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 *
 * @author chen.ma
 * @date 2021/6/20 17:16
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    private String messageConsumePrefix = "message-consume";

    private String messageProducePrefix = "message-produce";

    private String customPoolPrefix = "custom-pool";

    @Bean
    public DynamicThreadPoolWrap messageCenterConsumeThreadPool() {
        return new DynamicThreadPoolWrap(messageConsumePrefix);
    }

    @Bean
    public DynamicThreadPoolWrap messageCenterProduceThreadPool() {
        return new DynamicThreadPoolWrap(messageProducePrefix);
    }

    @Bean
    public DynamicThreadPoolWrap customPool() {
        return new DynamicThreadPoolWrap(customPoolPrefix);
    }

    // @PostConstruct
    @SuppressWarnings("all")
    public void testExecuteTask() {
        log.info("测试线程池运行时状态接口, 30s 后开始触发拒绝策略...");

        ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
        scheduledThreadPool.scheduleAtFixedRate(() -> {
            DynamicThreadPoolWrap executorService = GlobalThreadPoolManage.getExecutorService(customPoolPrefix);
            ThreadPoolExecutor pool = executorService.getPool();
            try {
                pool.execute(() -> {
                    log.info("线程池名称 :: {}, 正在执行即将进入阻塞...", Thread.currentThread().getName());
                    try {
                        int maxRandom = 10;
                        int temp = 2;
                        Random random = new Random();
                        // 这里为了赋值线程池 completedTaskCount
                        if (random.nextInt(maxRandom) % temp == 0) {
                            Thread.sleep(10241024);
                        } else {
                            Thread.sleep(3000);
                        }
                    } catch (InterruptedException e) {
                        // ignore
                    }
                });
            } catch (Exception ex) {
                // ignore
            }

        }, 5, 2, TimeUnit.SECONDS);
    }
}
