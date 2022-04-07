package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.PoolParameter;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.notify.HippoBaseSendMessageService;
import cn.hippo4j.common.notify.NotifyConfigDTO;
import cn.hippo4j.common.notify.ThreadPoolNotifyAlarm;
import cn.hippo4j.common.notify.request.ChangeParameterNotifyRequest;
import cn.hippo4j.core.executor.web.WebThreadPoolHandlerChoose;
import cn.hippo4j.core.executor.web.WebThreadPoolService;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.executor.manage.GlobalNotifyAlarmManage;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.*;
import cn.hippo4j.core.proxy.RejectedProxyUtil;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.starter.config.ExecutorProperties;
import cn.hippo4j.core.starter.config.WebThreadPoolProperties;
import cn.hippo4j.core.starter.notify.CoreNotifyConfigBuilder;
import cn.hippo4j.core.starter.parser.ConfigParserHandler;
import cn.hippo4j.core.starter.support.GlobalCoreThreadPoolManage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Abstract core thread pool dynamic refresh.
 *
 * @author chen.ma
 * @date 2022/2/26 12:42
 */
@Slf4j
@AllArgsConstructor
public abstract class AbstractCoreThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh, InitializingBean {

    private final ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler;

    protected final BootstrapCoreProperties bootstrapCoreProperties;

    protected final ExecutorService dynamicRefreshExecutorService = ThreadPoolBuilder.builder()
            .threadFactory("client.dynamic.refresh")
            .singlePool()
            .build();

    @Override
    public void dynamicRefresh(String content) {
        Map<Object, Object> configInfo;
        try {
            configInfo = ConfigParserHandler.getInstance().parseConfig(content, bootstrapCoreProperties.getConfigFileType());
        } catch (IOException e) {
            log.error("dynamic-thread-pool parse config file error, content: {}, fileType: {}",
                    content, bootstrapCoreProperties.getConfigFileType(), e);
            return;
        }

        BootstrapCoreProperties bindableCoreProperties = BootstrapCorePropertiesBinderAdapt.bootstrapCorePropertiesBinder(configInfo, bootstrapCoreProperties);

        // web pool
        refreshWebExecutor(bindableCoreProperties);
        // platforms
        refreshPlatforms(bindableCoreProperties);
        // executors
        refreshExecutors(bindableCoreProperties);
    }

    /**
     * register notify alarm manage
     */
    public void registerNotifyAlarmManage() {
        bootstrapCoreProperties.getExecutors().forEach(executorProperties -> {
            ThreadPoolNotifyAlarm threadPoolNotifyAlarm = new ThreadPoolNotifyAlarm(
                    executorProperties.getNotify().getIsAlarm(),
                    executorProperties.getNotify().getCapacityAlarm(),
                    executorProperties.getNotify().getActiveAlarm()
            );

            threadPoolNotifyAlarm.setInterval(executorProperties.getNotify().getInterval());
            threadPoolNotifyAlarm.setReceives(executorProperties.receives());
            GlobalNotifyAlarmManage.put(executorProperties.getThreadPoolId(), threadPoolNotifyAlarm);
        });
    }

    /**
     * Refresh web executor.
     *
     * @param bindableCoreProperties
     */
    private void refreshWebExecutor(BootstrapCoreProperties bindableCoreProperties) {
        boolean isNullFlag = bindableCoreProperties.getJetty() == null
                || bindableCoreProperties.getUndertow() == null
                || bindableCoreProperties.getTomcat() == null;
        if (isNullFlag) {
            return;
        }

        try {
            PoolParameterInfo nowParameter = buildWebPoolParameter(bindableCoreProperties);
            if (nowParameter != null) {
                WebThreadPoolHandlerChoose webThreadPoolHandlerChoose = ApplicationContextHolder.getBean(WebThreadPoolHandlerChoose.class);
                WebThreadPoolService webThreadPoolService = webThreadPoolHandlerChoose.choose();

                PoolParameter beforeParameter = webThreadPoolService.getWebThreadPoolParameter();
                if (!Objects.equals(beforeParameter.getCoreSize(), nowParameter.getCoreSize())
                        || !Objects.equals(beforeParameter.getMaxSize(), nowParameter.getMaxSize())
                        || !Objects.equals(beforeParameter.getKeepAliveTime(), nowParameter.getKeepAliveTime())) {
                    webThreadPoolService.updateWebThreadPool(nowParameter);
                }
            }
        } catch (Exception ex) {
            log.error("Failed to modify web thread pool.", ex);
        }
    }

    /**
     * Refresh platform.
     *
     * @param bindableCoreProperties
     */
    private void refreshPlatforms(BootstrapCoreProperties bindableCoreProperties) {
        List<ExecutorProperties> executors = bindableCoreProperties.getExecutors();
        for (ExecutorProperties executor : executors) {
            String threadPoolId = executor.getThreadPoolId();
            DynamicThreadPoolWrapper wrapper = GlobalThreadPoolManage.getExecutorService(threadPoolId);
            if (!wrapper.isInitFlag()) {
                HippoBaseSendMessageService sendMessageService = ApplicationContextHolder.getBean(HippoBaseSendMessageService.class);
                CoreNotifyConfigBuilder configBuilder = ApplicationContextHolder.getBean(CoreNotifyConfigBuilder.class);
                Map<String, List<NotifyConfigDTO>> notifyConfig = configBuilder.buildSingleNotifyConfig(executor);
                sendMessageService.putPlatform(notifyConfig);

                wrapper.setInitFlag(Boolean.TRUE);
            }
        }
    }

    /**
     * Refresh executors.
     *
     * @param bindableCoreProperties
     */
    private void refreshExecutors(BootstrapCoreProperties bindableCoreProperties) {
        List<ExecutorProperties> executors = bindableCoreProperties.getExecutors();
        for (ExecutorProperties properties : executors) {
            String threadPoolId = properties.getThreadPoolId();
            if (!checkConsistency(threadPoolId, properties)) {
                continue;
            }
            // refresh executor pool
            dynamicRefreshPool(threadPoolId, properties);
            // old properties
            ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());
            // refresh executor properties
            GlobalCoreThreadPoolManage.refresh(threadPoolId, properties);
            log.info(
                    "[{}] Changed thread pool. " +
                            "\n    coreSize :: [{}]" +
                            "\n    maxSize :: [{}]" +
                            "\n    queueType :: [{}]" +
                            "\n    capacity :: [{}]" +
                            "\n    keepAliveTime :: [{}]" +
                            "\n    executeTimeOut :: [{}]" +
                            "\n    rejectedType :: [{}]" +
                            "\n    allowCoreThreadTimeOut :: [{}]",
                    threadPoolId.toUpperCase(),
                    String.format("%s => %s", beforeProperties.getCorePoolSize(), properties.getCorePoolSize()),
                    String.format("%s => %s", beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize()),
                    String.format("%s => %s", beforeProperties.getBlockingQueue(), properties.getBlockingQueue()),
                    String.format("%s => %s", beforeProperties.getQueueCapacity(), properties.getQueueCapacity()),
                    String.format("%s => %s", beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime()),
                    String.format("%s => %s", beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut()),
                    String.format("%s => %s", beforeProperties.getRejectedHandler(), properties.getRejectedHandler()),
                    String.format("%s => %s", beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut())
            );

            try {
                threadPoolNotifyAlarmHandler.sendPoolConfigChange(newChangeRequest(beforeProperties,properties));
            } catch (Throwable ex) {
                log.error("Failed to send change notice. Message :: {}", ex.getMessage());
            }
        }
    }

    /**
     * construct ChangeParameterNotifyRequest instance
     *
     * @param beforeProperties old properties
     * @param properties       new properties
     * @return instance
     */
    private ChangeParameterNotifyRequest newChangeRequest(ExecutorProperties beforeProperties, ExecutorProperties properties) {
        ChangeParameterNotifyRequest changeRequest = new ChangeParameterNotifyRequest();
        changeRequest.setBeforeCorePoolSize(beforeProperties.getCorePoolSize());
        changeRequest.setBeforeMaximumPoolSize(beforeProperties.getMaximumPoolSize());
        changeRequest.setBeforeAllowsCoreThreadTimeOut(beforeProperties.getAllowCoreThreadTimeOut());
        changeRequest.setBeforeKeepAliveTime(beforeProperties.getKeepAliveTime());
        changeRequest.setBlockingQueueName(beforeProperties.getBlockingQueue());
        changeRequest.setBeforeQueueCapacity(beforeProperties.getQueueCapacity());
        changeRequest.setBeforeRejectedName(beforeProperties.getRejectedHandler());
        changeRequest.setBeforeExecuteTimeOut(beforeProperties.getExecuteTimeOut());
        changeRequest.setThreadPoolId(beforeProperties.getThreadPoolId());
        changeRequest.setNowCorePoolSize(properties.getCorePoolSize());
        changeRequest.setNowMaximumPoolSize(properties.getMaximumPoolSize());
        changeRequest.setNowAllowsCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
        changeRequest.setNowKeepAliveTime(properties.getKeepAliveTime());
        changeRequest.setNowQueueCapacity(properties.getQueueCapacity());
        changeRequest.setNowRejectedName(properties.getRejectedHandler());
        changeRequest.setNowExecuteTimeOut(properties.getExecuteTimeOut());
        return changeRequest;
    }

    /**
     * Check consistency.
     *
     * @param threadPoolId
     * @param properties
     */
    private boolean checkConsistency(String threadPoolId, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());
        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();

        boolean result = !Objects.equals(beforeProperties.getCorePoolSize(), properties.getCorePoolSize())
                || !Objects.equals(beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize())
                || !Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut())
                || !Objects.equals(beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut())
                || !Objects.equals(beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime())
                || !Objects.equals(beforeProperties.getRejectedHandler(), properties.getRejectedHandler())
                ||
                (
                        !Objects.equals(beforeProperties.getQueueCapacity(), properties.getQueueCapacity())
                                && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.name, executor.getQueue().getClass().getSimpleName())
                );

        return result;
    }

    /**
     * Dynamic refresh pool.
     *
     * @param threadPoolId
     * @param properties
     */
    private void dynamicRefreshPool(String threadPoolId, ExecutorProperties properties) {
        ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());

        ThreadPoolExecutor executor = GlobalThreadPoolManage.getExecutorService(threadPoolId).getExecutor();

        if (!Objects.equals(beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize())) {
            executor.setMaximumPoolSize(properties.getMaximumPoolSize());
        }

        if (!Objects.equals(beforeProperties.getCorePoolSize(), properties.getCorePoolSize())) {
            executor.setCorePoolSize(properties.getCorePoolSize());
        }

        if (!Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut())) {
            executor.allowCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
        }

        if (!Objects.equals(beforeProperties.getExecuteTimeOut(), properties.getExecuteTimeOut())) {
            if (executor instanceof AbstractDynamicExecutorSupport) {
                ((DynamicThreadPoolExecutor) executor).setExecuteTimeOut(properties.getExecuteTimeOut());
            }
        }

        if (!Objects.equals(beforeProperties.getRejectedHandler(), properties.getRejectedHandler())) {
            RejectedExecutionHandler rejectedExecutionHandler = RejectedTypeEnum.createPolicy(properties.getRejectedHandler());
            if (executor instanceof AbstractDynamicExecutorSupport) {
                DynamicThreadPoolExecutor dynamicExecutor = (DynamicThreadPoolExecutor) executor;
                dynamicExecutor.setRedundancyHandler(rejectedExecutionHandler);
                AtomicLong rejectCount = dynamicExecutor.getRejectCount();
                rejectedExecutionHandler = RejectedProxyUtil.createProxy(rejectedExecutionHandler, threadPoolId, rejectCount);
            }

            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }

        if (!Objects.equals(beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime())) {
            executor.setKeepAliveTime(properties.getKeepAliveTime(), TimeUnit.SECONDS);
        }

        if (!Objects.equals(beforeProperties.getQueueCapacity(), properties.getQueueCapacity())
                && Objects.equals(QueueTypeEnum.RESIZABLE_LINKED_BLOCKING_QUEUE.name, executor.getQueue().getClass().getSimpleName())) {
            if (executor.getQueue() instanceof ResizableCapacityLinkedBlockIngQueue) {
                ResizableCapacityLinkedBlockIngQueue queue = (ResizableCapacityLinkedBlockIngQueue) executor.getQueue();
                queue.setCapacity(properties.getQueueCapacity());
            } else {
                log.warn("The queue length cannot be modified. Queue type mismatch. Current queue type :: {}", executor.getQueue().getClass().getSimpleName());
            }
        }
    }

    /**
     * Build web pool parameter.
     *
     * @param bindableCoreProperties
     * @return
     */
    private PoolParameterInfo buildWebPoolParameter(BootstrapCoreProperties bindableCoreProperties) {
        PoolParameterInfo parameterInfo = null;
        WebThreadPoolProperties poolProperties = null;
        if (bindableCoreProperties.getTomcat() != null) {
            poolProperties = bindableCoreProperties.getTomcat();
        } else if (bindableCoreProperties.getUndertow() != null) {
            poolProperties = bindableCoreProperties.getUndertow();
        } else if (bindableCoreProperties.getJetty() != null) {
            poolProperties = bindableCoreProperties.getJetty();
        }

        if (poolProperties != null) {
            parameterInfo = new PoolParameterInfo();
            parameterInfo.setCoreSize(poolProperties.getCorePoolSize());
            parameterInfo.setMaxSize(poolProperties.getMaximumPoolSize());
            parameterInfo.setKeepAliveTime(poolProperties.getKeepAliveTime());
        }

        return parameterInfo;
    }

}
