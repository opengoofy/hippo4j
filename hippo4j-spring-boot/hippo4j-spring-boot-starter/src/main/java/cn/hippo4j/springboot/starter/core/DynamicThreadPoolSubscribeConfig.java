package cn.hippo4j.springboot.starter.core;

import cn.hippo4j.common.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.core.executor.support.QueueTypeEnum;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic thread-pool subscribe config.
 */
@RequiredArgsConstructor
public class DynamicThreadPoolSubscribeConfig {

    private final ThreadPoolDynamicRefresh threadPoolDynamicRefresh;

    private final ClientWorker clientWorker;

    private final BootstrapProperties properties;

    private final ExecutorService configRefreshExecutorService = ThreadPoolBuilder.builder()
            .corePoolSize(1)
            .maxPoolNum(2)
            .keepAliveTime(2000)
            .timeUnit(TimeUnit.MILLISECONDS)
            .workQueue(QueueTypeEnum.SYNCHRONOUS_QUEUE)
            .allowCoreThreadTimeOut(true)
            .threadFactory("client.dynamic.threadPool.change.config")
            .rejected(new ThreadPoolExecutor.AbortPolicy())
            .build();

    public void subscribeConfig(String threadPoolId) {
        subscribeConfig(threadPoolId, config -> threadPoolDynamicRefresh.dynamicRefresh(config));
    }

    public void subscribeConfig(String threadPoolId, ThreadPoolSubscribeCallback threadPoolSubscribeCallback) {
        Listener configListener = new Listener() {

            @Override
            public void receiveConfigInfo(String config) {
                threadPoolSubscribeCallback.callback(config);
            }

            @Override
            public Executor getExecutor() {
                return configRefreshExecutorService;
            }
        };
        clientWorker.addTenantListeners(properties.getNamespace(), properties.getItemId(), threadPoolId, Arrays.asList(configListener));
    }
}
