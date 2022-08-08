package cn.hippo4j.core.executor.support;

import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Dynamic thread-pool service.
 */
public interface DynamicThreadPoolService {

    /**
     * Registering dynamic thread pools at runtime.
     *
     * @param registerWrapper
     * @return
     */
    ThreadPoolExecutor registerDynamicThreadPool(DynamicThreadPoolRegisterWrapper registerWrapper);
}
