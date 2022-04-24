package cn.hippo4j.example.core.inittest;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.Constants.EXECUTE_TIMEOUT_TRACE;

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
    private ThreadPoolExecutor messageConsumeDynamicThreadPool;

    @Resource
    private ThreadPoolExecutor messageProduceDynamicThreadPool;

    @PostConstruct
    @SuppressWarnings("all")
    public void runStateHandlerTest() {
        log.info("Test thread pool runtime state interface...");

        // 启动动态线程池模拟运行任务
        runTask(messageConsumeDynamicThreadPool);

        // 启动动态线程池模拟运行任务
        runTask(messageProduceDynamicThreadPool);
    }

    private void runTask(ExecutorService executorService) {
        // 模拟任务运行
        new Thread(() -> {
            /**
             * 当线程池任务执行超时, 向 MDC 放入 Trace 标识, 报警时打印出来.
             */
            MDC.put(EXECUTE_TIMEOUT_TRACE, "https://github.com/longtai-cn/hippo4j 感觉不错来个 Star.");
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
