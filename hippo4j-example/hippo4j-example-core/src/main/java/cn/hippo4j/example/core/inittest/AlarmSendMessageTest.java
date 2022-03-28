package cn.hippo4j.example.core.inittest;

import cn.hippo4j.example.core.constant.GlobalTestConstant;
import cn.hutool.core.thread.ThreadUtil;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
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

    /**
     * 测试报警通知.
     * 如果需要运行此单测, 方法上添加 @PostConstruct
     */
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
