package cn.hippo4j.core.executor.support;

import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;

/**
 * Dynamic thread-pool service.
 */
public interface DynamicThreadPoolService {

    /**
     * Registering dynamic thread pools at runtime.
     *
     * @param registerWrapper
     */
    void registerDynamicThreadPool(DynamicThreadPoolRegisterWrapper registerWrapper);
}
