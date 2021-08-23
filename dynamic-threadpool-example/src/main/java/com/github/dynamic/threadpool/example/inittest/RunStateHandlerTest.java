package com.github.dynamic.threadpool.example.inittest;

import com.github.dynamic.threadpool.starter.core.GlobalThreadPoolManage;
import com.github.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.github.dynamic.threadpool.example.inittest.GlobalTestConstant.TEST_THREAD_POOL_ID;

/**
 * Test run time metrics.
 *
 * @author chen.ma
 * @date 2021/8/15 21:00
 */
@Slf4j
@Component
public class RunStateHandlerTest {

    // @PostConstruct
    @SuppressWarnings("all")
    public void runStateHandlerTest() {
        log.info("Test thread pool runtime state interface, The rejection policy will be triggered after 30s...");

        ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
        scheduledThreadPool.scheduleAtFixedRate(() -> {
            DynamicThreadPoolWrap executorService = GlobalThreadPoolManage.getExecutorService(TEST_THREAD_POOL_ID);
            ThreadPoolExecutor pool = executorService.getPool();
            try {
                pool.execute(() -> {
                    log.info("Thread pool name :: {}, Executing incoming blocking...", Thread.currentThread().getName());
                    try {
                        int maxRandom = 10;
                        int temp = 2;
                        Random random = new Random();
                        // Assignment thread pool completedTaskCount
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
