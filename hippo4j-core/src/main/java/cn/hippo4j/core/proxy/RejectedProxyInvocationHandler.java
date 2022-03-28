package cn.hippo4j.core.proxy;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rejected proxy invocation handler.
 *
 * @author chen.ma
 * @date 2022/2/17 19:45
 */
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

    private final Object target;

    private final String threadPoolId;

    private final AtomicLong rejectCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        rejectCount.incrementAndGet();

        if (ApplicationContextHolder.getInstance() != null) {
            ThreadPoolNotifyAlarmHandler alarmHandler = ApplicationContextHolder.getBean(ThreadPoolNotifyAlarmHandler.class);
            alarmHandler.checkPoolRejectedAlarm(threadPoolId);
        }

        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

}
