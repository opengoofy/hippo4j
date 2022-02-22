package cn.hippo4j.starter.core;

import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rejected proxy invocation handler.
 *
 * @author chen.ma
 * @date 2022/2/17 19:45
 */
@AllArgsConstructor
public class RejectedProxyInvocationHandler implements InvocationHandler {

    private final Object target;

    private final AtomicInteger rejectCount;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        rejectCount.incrementAndGet();
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            throw ex.getCause();
        }
    }

}
