package com.github.dynamic.threadpool.starter.core;

import com.alibaba.fastjson.JSON;
import com.github.dynamic.threadpool.common.constant.Constants;
import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.starter.common.CommonThreadPool;
import com.github.dynamic.threadpool.starter.config.BootstrapProperties;
import com.github.dynamic.threadpool.starter.remote.HttpAgent;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;
import com.github.dynamic.threadpool.starter.toolkit.thread.QueueTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.RejectedTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.ThreadPoolBuilder;
import com.github.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic threadPool post processor.
 *
 * @author chen.ma
 * @date 2021/8/2 20:40
 */
@Slf4j
public final class DynamicThreadPoolPostProcessor implements BeanPostProcessor {

    private final BootstrapProperties properties;

    private final ThreadPoolOperation threadPoolOperation;

    private final HttpAgent httpAgent;

    public DynamicThreadPoolPostProcessor(BootstrapProperties properties, HttpAgent httpAgent,
                                          ThreadPoolOperation threadPoolOperation) {
        this.properties = properties;
        this.httpAgent = httpAgent;
        this.threadPoolOperation = threadPoolOperation;
    }

    private final ExecutorService executorService = ThreadPoolBuilder.builder()
            .poolThreadSize(2, 4)
            .keepAliveTime(0L, TimeUnit.MILLISECONDS)
            .workQueue(QueueTypeEnum.ARRAY_BLOCKING_QUEUE, 1)
            .threadFactory("dynamic-threadPool-config")
            .rejected(new ThreadPoolExecutor.DiscardOldestPolicy())
            .build();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof DynamicThreadPoolWrap)) {
            return bean;
        }
        DynamicThreadPoolWrap dynamicThreadPoolWrap = (DynamicThreadPoolWrap) bean;

        /**
         * 根据 TpId 向 Server 端发起请求, 查询是否有远程配置
         * Server 端无配置使用默认 ${@link CommonThreadPool#getInstance(String)}
         */
        fillPoolAndRegister(dynamicThreadPoolWrap);

        /**
         * 订阅 Server 端配置, 远端没有配置的线程池不参与订阅
         */
        subscribeConfig(dynamicThreadPoolWrap);

        return bean;
    }

    /**
     * Fill the thread pool and register.
     *
     * @param dynamicThreadPoolWrap
     */
    private void fillPoolAndRegister(DynamicThreadPoolWrap dynamicThreadPoolWrap) {
        String tpId = dynamicThreadPoolWrap.getTpId();
        Map<String, String> queryStrMap = new HashMap(3);
        queryStrMap.put("tpId", tpId);
        queryStrMap.put("itemId", properties.getItemId());
        queryStrMap.put("namespace", properties.getNamespace());

        Result result = null;
        boolean isSubscribe = false;
        CustomThreadPoolExecutor poolExecutor = null;
        PoolParameterInfo ppi = new PoolParameterInfo();

        try {
            result = httpAgent.httpGetByConfig(Constants.CONFIG_CONTROLLER_PATH, null, queryStrMap, 3000L);
            if (result.isSuccess() && result.getData() != null && (ppi = JSON.toJavaObject((JSON) result.getData(), PoolParameterInfo.class)) != null) {
                // 使用相关参数创建线程池
                BlockingQueue workQueue = QueueTypeEnum.createBlockingQueue(ppi.getQueueType(), ppi.getCapacity());
                poolExecutor = (CustomThreadPoolExecutor) ThreadPoolBuilder.builder()
                        .isCustomPool(true)
                        .workQueue(workQueue)
                        .threadPoolId(tpId)
                        .threadFactory(tpId)
                        .poolThreadSize(ppi.getCoreSize(), ppi.getMaxSize())
                        .keepAliveTime(ppi.getKeepAliveTime(), TimeUnit.SECONDS)
                        .rejected(RejectedTypeEnum.createPolicy(ppi.getRejectedType()))
                        .alarmConfig(ppi.getIsAlarm(), ppi.getCapacityAlarm(), ppi.getLivenessAlarm())
                        .build();

                dynamicThreadPoolWrap.setPool(poolExecutor);
                isSubscribe = true;
            }
        } catch (Exception ex) {
            poolExecutor = dynamicThreadPoolWrap.getPool() != null ? dynamicThreadPoolWrap.getPool() : CommonThreadPool.getInstance(tpId);
            dynamicThreadPoolWrap.setPool(poolExecutor);

            log.error("[Init pool] Failed to initialize thread pool configuration. error message :: {}", ex.getMessage());
        } finally {
            if (Objects.isNull(dynamicThreadPoolWrap.getPool())) {
                dynamicThreadPoolWrap.setPool(CommonThreadPool.getInstance(tpId));
            }

            // 设置是否订阅远端线程池配置
            dynamicThreadPoolWrap.setSubscribeFlag(isSubscribe);
        }

        GlobalThreadPoolManage.register(dynamicThreadPoolWrap.getTpId(), ppi, dynamicThreadPoolWrap);
    }

    private void subscribeConfig(DynamicThreadPoolWrap dynamicThreadPoolWrap) {
        if (dynamicThreadPoolWrap.isSubscribeFlag()) {
            threadPoolOperation.subscribeConfig(dynamicThreadPoolWrap.getTpId(), executorService, config -> ThreadPoolDynamicRefresh.refreshDynamicPool(config));
        }
    }

}
