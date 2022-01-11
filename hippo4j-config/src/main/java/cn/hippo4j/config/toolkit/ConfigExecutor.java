package cn.hippo4j.config.toolkit;

import cn.hippo4j.common.executor.ExecutorFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.Constants.DEFAULT_GROUP;

/**
 * Config executor.
 *
 * @author chen.ma
 * @date 2021/6/23 18:33
 */
public class ConfigExecutor {

    private static final ScheduledExecutorService LONG_POLLING_EXECUTOR = ExecutorFactory.Managed
            .newSingleScheduledExecutorService(DEFAULT_GROUP, r -> new Thread(r, "server.long.polling"));

    public static void executeLongPolling(Runnable runnable) {
        LONG_POLLING_EXECUTOR.execute(runnable);
    }

    public static ScheduledFuture<?> scheduleLongPolling(Runnable runnable, long period, TimeUnit unit) {
        return LONG_POLLING_EXECUTOR.schedule(runnable, period, unit);
    }

    public static void scheduleLongPolling(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        LONG_POLLING_EXECUTOR.scheduleWithFixedDelay(runnable, initialDelay, period, unit);
    }

}
