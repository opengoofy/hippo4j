package cn.hippo4j.core.springboot.starter.support;

import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.model.register.notify.DynamicThreadPoolRegisterCoreNotifyParameter;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.QueueTypeEnum;
import cn.hippo4j.core.executor.support.RejectedTypeEnum;
import cn.hippo4j.core.executor.support.service.AbstractDynamicThreadPoolService;
import cn.hippo4j.core.springboot.starter.config.ExecutorProperties;
import cn.hippo4j.message.service.ThreadPoolNotifyAlarm;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Dynamic thread-pool config service.
 */
public class DynamicThreadPoolConfigService extends AbstractDynamicThreadPoolService {

    @Override
    public ThreadPoolExecutor registerDynamicThreadPool(DynamicThreadPoolRegisterWrapper registerWrapper) {
        DynamicThreadPoolRegisterParameter registerParameter = registerWrapper.getDynamicThreadPoolRegisterParameter();
        String threadPoolId = registerParameter.getThreadPoolId();
        ThreadPoolExecutor dynamicThreadPoolExecutor = buildDynamicThreadPoolExecutor(registerParameter);
        DynamicThreadPoolWrapper dynamicThreadPoolWrapper = DynamicThreadPoolWrapper.builder()
                .threadPoolId(threadPoolId)
                .executor(dynamicThreadPoolExecutor)
                .build();
        // Register pool.
        GlobalThreadPoolManage.registerPool(threadPoolId, dynamicThreadPoolWrapper);
        ExecutorProperties executorProperties = buildExecutorProperties(registerWrapper);
        // Register properties.
        GlobalCoreThreadPoolManage.register(threadPoolId, executorProperties);
        DynamicThreadPoolRegisterCoreNotifyParameter notifyParameter = registerWrapper.getDynamicThreadPoolRegisterCoreNotifyParameter();
        ThreadPoolNotifyAlarm notifyAlarm = new ThreadPoolNotifyAlarm(true, notifyParameter.getActiveAlarm(), notifyParameter.getCapacityAlarm());
        notifyAlarm.setReceives(notifyParameter.getReceives());
        notifyAlarm.setInterval(notifyParameter.getInterval());
        // Register notify.
        GlobalNotifyAlarmManage.put(threadPoolId, notifyAlarm);
        return dynamicThreadPoolExecutor;
    }

    private ExecutorProperties buildExecutorProperties(DynamicThreadPoolRegisterWrapper registerWrapper) {
        DynamicThreadPoolRegisterParameter registerParameter = registerWrapper.getDynamicThreadPoolRegisterParameter();
        ExecutorProperties executorProperties = ExecutorProperties.builder()
                .corePoolSize(registerParameter.getCorePoolSize())
                .maximumPoolSize(registerParameter.getMaximumPoolSize())
                .allowCoreThreadTimeOut(registerParameter.getAllowCoreThreadTimeOut())
                .keepAliveTime(registerParameter.getKeepAliveTime())
                .blockingQueue(QueueTypeEnum.getBlockingQueueNameByType(registerParameter.getQueueType()))
                .threadNamePrefix(registerParameter.getThreadNamePrefix())
                .rejectedHandler(RejectedTypeEnum.getRejectedNameByType(registerParameter.getRejectedType()))
                .executeTimeOut(registerParameter.getExecuteTimeOut())
                .threadPoolId(registerParameter.getThreadPoolId())
                .build();
        return executorProperties;
    }
}
