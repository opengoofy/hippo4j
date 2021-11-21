package cn.hippo4j.example.inittest;

import cn.hippo4j.example.constant.GlobalTestConstant;
import cn.hutool.core.thread.ThreadUtil;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
            DynamicThreadPoolWrapper poolWrapper = GlobalThreadPoolManage.getExecutorService(GlobalTestConstant.MESSAGE_PRODUCE);
            ThreadPoolExecutor poolExecutor = poolWrapper.getExecutor();
            try {
                poolExecutor.execute(() -> ThreadUtil.sleep(10240124));
            } catch (Exception ex) {
                log.error("Throw reject policy.", ex.getMessage());
            }
        }, 3, 1, TimeUnit.SECONDS);
    }

}
