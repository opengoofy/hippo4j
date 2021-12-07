package cn.hippo4j.example.inittest;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Random;
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
    private ThreadPoolExecutor dynamicThreadPoolExecutor;

    @PostConstruct
    @SuppressWarnings("all")
    public void runStateHandlerTest() {
        log.info("Test thread pool runtime state interface, The rejection policy will be triggered after 30s...");

        new Thread(() -> {
            ThreadUtil.sleep(5000);
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                try {
                    dynamicThreadPoolExecutor.execute(() -> {
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

                log.info("  >>> Number of dynamic thread pool tasks executed :: {}", i);
                ThreadUtil.sleep(500);
            }

        }).start();
    }

}
