package cn.hippo4j.core.executor.support.service;

import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.core.executor.support.QueueTypeEnum;
import cn.hippo4j.core.executor.support.RejectedTypeEnum;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Abstract dynamic thread-pool service.
 */
public abstract class AbstractDynamicThreadPoolService implements DynamicThreadPoolService {

    /**
     * Build dynamic thread-pool executor.
     *
     * @param registerParameter
     * @return
     */
    public ThreadPoolExecutor buildDynamicThreadPoolExecutor(DynamicThreadPoolRegisterParameter registerParameter) {
        ThreadPoolExecutor dynamicThreadPoolExecutor = ThreadPoolBuilder.builder()
                .threadPoolId(registerParameter.getThreadPoolId())
                .corePoolSize(registerParameter.getCorePoolSize())
                .maxPoolNum(registerParameter.getMaximumPoolSize())
                .workQueue(QueueTypeEnum.createBlockingQueue(registerParameter.getQueueType(), registerParameter.getCapacity()))
                .threadFactory(registerParameter.getThreadNamePrefix())
                .keepAliveTime(registerParameter.getKeepAliveTime())
                .rejected(RejectedTypeEnum.createPolicy(registerParameter.getRejectedType()))
                .dynamicPool()
                .build();
        return dynamicThreadPoolExecutor;
    }
}
