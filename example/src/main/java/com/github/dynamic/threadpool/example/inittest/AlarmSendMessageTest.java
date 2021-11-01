package com.github.dynamic.threadpool.example.inittest;

import cn.hutool.core.thread.ThreadUtil;
import com.github.dynamic.threadpool.starter.core.GlobalThreadPoolManage;
import com.github.dynamic.threadpool.starter.wrapper.DynamicThreadPoolWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.github.dynamic.threadpool.example.constant.GlobalTestConstant.MESSAGE_PRODUCE;

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
            DynamicThreadPoolWrapper poolWrapper = GlobalThreadPoolManage.getExecutorService(MESSAGE_PRODUCE);
            ThreadPoolExecutor poolExecutor = poolWrapper.getPool();
            try {
                poolExecutor.execute(() -> ThreadUtil.sleep(10240124));
            } catch (Exception ex) {
                log.error("Throw reject policy.", ex.getMessage());
            }
        }, 3, 1, TimeUnit.SECONDS);
    }

}
