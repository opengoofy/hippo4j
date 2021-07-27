package io.dynamic.threadpool.starter.core;

import com.alibaba.fastjson.JSON;
import io.dynamic.threadpool.common.config.ApplicationContextHolder;
import io.dynamic.threadpool.common.constant.Constants;
import io.dynamic.threadpool.common.model.PoolParameterInfo;
import io.dynamic.threadpool.common.web.base.Result;
import io.dynamic.threadpool.starter.common.CommonThreadPool;
import io.dynamic.threadpool.starter.config.DynamicThreadPoolProperties;
import io.dynamic.threadpool.starter.remote.HttpAgent;
import io.dynamic.threadpool.starter.remote.ServerHttpAgent;
import io.dynamic.threadpool.starter.toolkit.thread.QueueTypeEnum;
import io.dynamic.threadpool.starter.toolkit.thread.RejectedTypeEnum;
import io.dynamic.threadpool.starter.toolkit.thread.ThreadPoolBuilder;
import io.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池启动监听
 *
 * @author chen.ma
 * @date 2021/6/20 16:34
 */
@Slf4j
public class ThreadPoolRunListener {

    private final DynamicThreadPoolProperties properties;

    public ThreadPoolRunListener(DynamicThreadPoolProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    @Order(Ordered.LOWEST_PRECEDENCE - 1024)
    public void run() {
        Map<String, DynamicThreadPoolWrap> executorMap = ApplicationContextHolder.getBeansOfType(DynamicThreadPoolWrap.class);
        executorMap.forEach((key, val) -> {
            String tpId = val.getTpId();
            Map<String, String> queryStrMap = new HashMap(3);
            queryStrMap.put("tpId", tpId);
            queryStrMap.put("itemId", properties.getItemId());
            queryStrMap.put("namespace", properties.getNamespace());

            PoolParameterInfo ppi = new PoolParameterInfo();
            HttpAgent httpAgent = new ServerHttpAgent(properties);
            ThreadPoolExecutor poolExecutor = null;
            Result result = null;

            try {
                result = httpAgent.httpGet(Constants.CONFIG_CONTROLLER_PATH, null, queryStrMap, 3000L);
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

                    val.setPool(poolExecutor);
                } else if (val.getPool() == null) {
                    val.setPool(CommonThreadPool.getInstance(tpId));
                }
            } catch (Exception ex) {
                poolExecutor = val.getPool() != null ? val.getPool() : CommonThreadPool.getInstance(tpId);
                val.setPool(poolExecutor);

                log.error("[Init pool] Failed to initialize thread pool configuration. error message :: {}", ex.getMessage());
            }

            GlobalThreadPoolManage.register(val.getTpId(), ppi, val);
        });
    }
}
