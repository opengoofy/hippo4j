package cn.hippo4j.starter.handler;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadPool status handler.
 *
 * @author chen.ma
 * @date 2022/1/18 20:54
 */
@Slf4j
public class ThreadPoolStatusHandler {

    private static final String RUNNING = "Running";

    private static final String TERMINATED = "Terminated";

    private static final String SHUTTING_DOWN = "Shutting down";

    private static final AtomicBoolean EXCEPTION_FLAG = new AtomicBoolean(Boolean.TRUE);

    /**
     * Get thread pool state.
     *
     * @param executor
     * @return
     */
    public static String getThreadPoolState(ThreadPoolExecutor executor) {
        if (EXCEPTION_FLAG.get()) {
            try {
                Method runStateLessThan = ReflectUtil.getMethodByName(ThreadPoolExecutor.class, "runStateLessThan");
                cn.hippo4j.common.toolkit.ReflectUtil.setAccessible(runStateLessThan);

                AtomicInteger ctl = (AtomicInteger) ReflectUtil.getFieldValue(executor, "ctl");
                int shutdown = (int) ReflectUtil.getFieldValue(executor, "SHUTDOWN");
                boolean runStateLessThanBool = ReflectUtil.invoke(executor, runStateLessThan, ctl.get(), shutdown);
                if (runStateLessThanBool) {
                    return RUNNING;
                }

                Method runStateAtLeast = ReflectUtil.getMethodByName(ThreadPoolExecutor.class, "runStateAtLeast");
                cn.hippo4j.common.toolkit.ReflectUtil.setAccessible(runStateAtLeast);
                int terminated = (int) ReflectUtil.getFieldValue(executor, "TERMINATED");
                String resultStatus = ReflectUtil.invoke(executor, runStateAtLeast, ctl.get(), terminated) ? TERMINATED : SHUTTING_DOWN;
                return resultStatus;
            } catch (Exception ex) {
                log.error("Failed to get thread pool status.", ex);

                EXCEPTION_FLAG.set(Boolean.FALSE);
            }
        }

        return "UNKNOWN";
    }

}
