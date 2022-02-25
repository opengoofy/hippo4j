package cn.hippo4j.example.core.inittest;

import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Test run time metrics.
 *
 * @author chen.ma
 * @date 2021/8/15 21:00
 */
@Slf4j
@Component
public class RunStateHandlerTest {

    @Resource
    private DynamicThreadPoolWrapper messageCenterDynamicThreadPool;

    @Resource
    private ThreadPoolExecutor dynamicThreadPoolExecutor;

    @PostConstruct
    @SuppressWarnings("all")
    public void runStateHandlerTest() {
        log.info("Test thread pool runtime state interface...");

        // 启动动态线程池模拟运行任务
        runTask(messageCenterDynamicThreadPool.getExecutor());

        // 启动动态线程池模拟运行任务
        runTask(dynamicThreadPoolExecutor);
    }

    private void runTask(ExecutorService executorService) {
        new Thread(() -> {
            ThreadUtil.sleep(5000);
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                try {
                    executorService.execute(() -> {
                        try {
                            int maxRandom = 10;
                            int temp = 2;
                            Random random = new Random();
                            // Assignment thread pool completedTaskCount
                            if (random.nextInt(maxRandom) % temp == 0) {
                                Thread.sleep(1000);
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

                ThreadUtil.sleep(500);
            }

        }).start();
    }

}
