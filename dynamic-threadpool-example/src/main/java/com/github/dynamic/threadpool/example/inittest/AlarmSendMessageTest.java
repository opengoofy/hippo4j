package com.github.dynamic.threadpool.example.inittest;

import cn.hutool.core.thread.ThreadUtil;
import com.github.dynamic.threadpool.starter.core.GlobalThreadPoolManage;
import com.github.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.github.dynamic.threadpool.example.inittest.GlobalTestConstant.TEST_THREAD_POOL_ID;

/**
 * Test alarm send message.
 *
 * @author chen.ma
 * @date 2021/8/15 21:03
 */
@Slf4j
@Component
public class AlarmSendMessageTest {

    // @PostConstruct
    @SuppressWarnings("all")
    public void alarmSendMessageTest() {
        ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
        scheduledThreadPool.scheduleWithFixedDelay(() -> {
            DynamicThreadPoolWrap executorService = GlobalThreadPoolManage.getExecutorService(TEST_THREAD_POOL_ID);
            ThreadPoolExecutor poolExecutor = executorService.getPool();
            try {
                poolExecutor.execute(() -> ThreadUtil.sleep(10240124));
            } catch (Exception ex) {
                log.error("Throw reject policy.", ex.getMessage());
            }
        }, 3, 1, TimeUnit.SECONDS);
    }

}
