package cn.hippo4j.core.starter.refresher;

import cn.hippo4j.common.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.common.notify.request.ChangeParameterNotifyRequest;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.*;
import cn.hippo4j.core.proxy.RejectedProxyUtil;
import cn.hippo4j.core.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.starter.config.ExecutorProperties;
import cn.hippo4j.core.starter.support.GlobalCoreThreadPoolManage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static cn.hippo4j.core.starter.config.BootstrapCoreProperties.PREFIX;

/**
 * Abstract core thread pool dynamic refresh.
 *
 * @author chen.ma
 * @date 2022/2/26 12:42
 */
@Slf4j
@AllArgsConstructor
public abstract class AbstractCoreThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh {

    private final ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler;

    private final ConfigParserHandler configParserHandler;

    protected final BootstrapCoreProperties bootstrapCoreProperties;

    protected final ExecutorService dynamicRefreshExecutorService = ThreadPoolBuilder.builder()
            .threadFactory("client.dynamic.refresh")
            .singlePool()
            .build();

    @Override
    public void dynamicRefresh(String content) {
        Map<Object, Object> configInfo = configParserHandler.parseConfig(content, bootstrapCoreProperties.getConfigFileType());

        ConfigurationPropertySource sources = new MapConfigurationPropertySource(configInfo);
        Binder binder = new Binder(sources);
        BootstrapCoreProperties bindableCoreProperties = binder.bind(PREFIX, Bindable.ofInstance(bootstrapCoreProperties)).get();

        List<ExecutorProperties> executors = bindableCoreProperties.getExecutors();
        for (ExecutorProperties properties : executors) {
            String threadPoolId = properties.getThreadPoolId();
            if (!checkConsistency(threadPoolId, properties)) {
                continue;
            }

            dynamicRefreshPool(threadPoolId, properties);

            ExecutorProperties beforeProperties = GlobalCoreThreadPoolManage.getProperties(properties.getThreadPoolId());
            ChangeParameterNotifyRequest changeRequest = new ChangeParameterNotifyRequest();
            changeRequest.setBeforeCorePoolSize(beforeProperties.getCorePoolSize());
            changeRequest.setBeforeMaximumPoolSize(beforeProperties.getMaximumPoolSize());
            changeRequest.setBeforeAllowsCoreThreadTimeOut(beforeProperties.getAllowCoreThreadTimeOut());
            changeRequest.setBeforeKeepAliveTime(beforeProperties.getKeepAliveTime());
            changeRequest.setBlockingQueueName(beforeProperties.getBlockingQueue());
            changeRequest.setBeforeQueueCapacity(beforeProperties.getQueueCapacity());
            changeRequest.setBeforeRejectedName(beforeProperties.getRejectedHandler());
            changeRequest.setThreadPoolId(beforeProperties.getThreadPoolId());

            changeRequest.setNowCorePoolSize(properties.getCorePoolSize());
            changeRequest.setNowMaximumPoolSize(properties.getMaximumPoolSize());
            changeRequest.setNowAllowsCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
            changeRequest.setNowKeepAliveTime(properties.getKeepAliveTime());
            changeRequest.setNowQueueCapacity(properties.getQueueCapacity());
            changeRequest.setNowRejectedName(properties.getRejectedHandler());

            GlobalCoreThreadPoolManage.refresh(threadPoolId, properties);
            log.info(
                    "[🔥 {}] Changed thread pool. " +
                            "\n    coreSize :: [{}]" +
                            "\n    maxSize :: [{}]" +
                            "\n    queueType :: [{}]" +
                            "\n    capacity :: [{}]" +
                            "\n    keepAliveTime :: [{}]" +
                            "\n    rejectedType :: [{}]" +
                            "\n    allowCoreThreadTimeOut :: [{}]",
                    threadPoolId.toUpperCase(),
                    String.format("%s => %s", beforeProperties.getCorePoolSize(), properties.getCorePoolSize()),
                    String.format("%s => %s", beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize()),
                    String.format("%s => %s", beforeProperties.getBlockingQueue(), properties.getBlockingQueue()),
                    String.format("%s => %s", beforeProperties.getQueueCapacity(), properties.getQueueCapacity()),
                    String.format("%s => %s", beforeProperties.getKeepAliveTime(), properties.getKeepAliveTime()),
                    String.format("%s => %s", beforeProperties.getRejectedHandler(), properties.getRejectedHandler()),
                    String.format("%s => %s", beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut())
            );

            try {
                threadPoolNotifyAlarmHandler.sendPoolConfigChange(changeRequest);
            } catch (Throwable ex) {
                log.error("Failed to send change notice. Message :: {}", ex.getMessage());
            }
        }
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
        if (!Objects.equals(beforeProperties.getCorePoolSize(), properties.getCorePoolSize())) {
            executor.setCorePoolSize(properties.getCorePoolSize());
        }

        if (!Objects.equals(beforeProperties.getMaximumPoolSize(), properties.getMaximumPoolSize())) {
            executor.setMaximumPoolSize(properties.getMaximumPoolSize());
        }

        if (!Objects.equals(beforeProperties.getAllowCoreThreadTimeOut(), properties.getAllowCoreThreadTimeOut())) {
            executor.allowCoreThreadTimeOut(properties.getAllowCoreThreadTimeOut());
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

}
