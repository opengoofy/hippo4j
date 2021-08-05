package com.github.dynamic.threadpool.starter.core;

import com.alibaba.fastjson.JSON;
import com.github.dynamic.threadpool.common.constant.Constants;
import com.github.dynamic.threadpool.common.model.PoolParameterInfo;
import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.starter.common.CommonThreadPool;
import com.github.dynamic.threadpool.starter.config.DynamicThreadPoolProperties;
import com.github.dynamic.threadpool.starter.remote.HttpAgent;
import com.github.dynamic.threadpool.starter.toolkit.thread.QueueTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.RejectedTypeEnum;
import com.github.dynamic.threadpool.starter.toolkit.thread.ThreadPoolBuilder;
import com.github.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池包装后置处理器
 *
 * @author chen.ma
 * @date 2021/8/2 20:40
 */
@Slf4j
public final class ThreadPoolBeanPostProcessor implements BeanPostProcessor {

    private final DynamicThreadPoolProperties properties;

    private final ThreadPoolOperation threadPoolOperation;

    private final HttpAgent httpAgent;

    public ThreadPoolBeanPostProcessor(DynamicThreadPoolProperties properties, HttpAgent httpAgent,
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
         * 根据 TpId 向 Server 端发起请求，查询是否有远程配置
         * Server 端无配置使用默认 ${@link CommonThreadPool#getInstance(String)}
         */
        fillPoolAndRegister(dynamicThreadPoolWrap);

        /**
         * 订阅 Server 端配置
         */
        subscribeConfig(dynamicThreadPoolWrap);

        return bean;
    }

    private void fillPoolAndRegister(DynamicThreadPoolWrap dynamicThreadPoolWrap) {
        String tpId = dynamicThreadPoolWrap.getTpId();
        Map<String, String> queryStrMap = new HashMap(3);
        queryStrMap.put("tpId", tpId);
        queryStrMap.put("itemId", properties.getItemId());
        queryStrMap.put("namespace", properties.getNamespace());

        PoolParameterInfo ppi = new PoolParameterInfo();
        ThreadPoolExecutor poolExecutor = null;
        Result result = null;

        try {
            result = httpAgent.httpGetByConfig(Constants.CONFIG_CONTROLLER_PATH, null, queryStrMap, 3000L);
            if (result.isSuccess() && result.getData() != null && (ppi = JSON.toJavaObject((JSON) result.getData(), PoolParameterInfo.class)) != null) {
                // 使用相关参数创建线程池
                BlockingQueue workQueue = QueueTypeEnum.createBlockingQueue(ppi.getQueueType(), ppi.getCapacity());
                poolExecutor = ThreadPoolBuilder.builder()
                        .isCustomPool(true)
                        .poolThreadSize(ppi.getCoreSize(), ppi.getMaxSize())
                        .keepAliveTime(ppi.getKeepAliveTime(), TimeUnit.SECONDS)
                        .workQueue(workQueue)
                        .threadFactory(tpId)
                        .rejected(RejectedTypeEnum.createPolicy(ppi.getRejectedType()))
                        .build();

                dynamicThreadPoolWrap.setPool(poolExecutor);
            } else if (dynamicThreadPoolWrap.getPool() == null) {
                dynamicThreadPoolWrap.setPool(CommonThreadPool.getInstance(tpId));
            }
        } catch (Exception ex) {
            poolExecutor = dynamicThreadPoolWrap.getPool() != null ? dynamicThreadPoolWrap.getPool() : CommonThreadPool.getInstance(tpId);
            dynamicThreadPoolWrap.setPool(poolExecutor);

            log.error("[Init pool] Failed to initialize thread pool configuration. error message :: {}", ex.getMessage());
        }

        GlobalThreadPoolManage.register(dynamicThreadPoolWrap.getTpId(), ppi, dynamicThreadPoolWrap);
    }

    private void subscribeConfig(DynamicThreadPoolWrap dynamicThreadPoolWrap) {
        threadPoolOperation.subscribeConfig(dynamicThreadPoolWrap.getTpId(), executorService, config -> ThreadPoolDynamicRefresh.refreshDynamicPool(config));
    }

}
