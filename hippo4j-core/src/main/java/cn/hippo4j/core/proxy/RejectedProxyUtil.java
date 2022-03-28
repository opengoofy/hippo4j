package cn.hippo4j.core.proxy;

import java.lang.reflect.Proxy;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rejected proxy util.
 *
 * @author chen.ma
 * @date 2022/2/22 21:56
 */
public class RejectedProxyUtil {

    /**
     * Proxy rejected execution.
     *
     * @param rejectedExecutionHandler
     * @param threadPoolId
     * @param rejectedNum
     * @return
     */
    public static RejectedExecutionHandler createProxy(RejectedExecutionHandler rejectedExecutionHandler, String threadPoolId, AtomicLong rejectedNum) {
        RejectedExecutionHandler rejectedProxy = (RejectedExecutionHandler) Proxy
                .newProxyInstance(
                        rejectedExecutionHandler.getClass().getClassLoader(),
                        new Class[]{RejectedExecutionHandler.class},
                        new RejectedProxyInvocationHandler(rejectedExecutionHandler, threadPoolId, rejectedNum)
                );

        return rejectedProxy;
    }

}
