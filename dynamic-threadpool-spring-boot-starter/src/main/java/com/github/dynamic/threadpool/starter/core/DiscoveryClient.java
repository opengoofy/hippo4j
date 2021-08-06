package com.github.dynamic.threadpool.starter.core;

import com.github.dynamic.threadpool.common.web.base.Result;
import com.github.dynamic.threadpool.starter.remote.HttpAgent;
import com.github.dynamic.threadpool.starter.toolkit.thread.ThreadFactoryBuilder;
import com.github.dynamic.threadpool.starter.toolkit.thread.ThreadPoolBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Discovery Client.
 *
 * @author chen.ma
 * @date 2021/7/13 21:51
 */
@Slf4j
public class DiscoveryClient {

    private final ThreadPoolExecutor heartbeatExecutor;

    private final ScheduledExecutorService scheduler;

    private final HttpAgent httpAgent;

    private final InstanceConfig instanceConfig;

    private volatile long lastSuccessfulHeartbeatTimestamp = -1;

    private static final String PREFIX = "DiscoveryClient_";

    private String appPathIdentifier;

    public DiscoveryClient(HttpAgent httpAgent, InstanceConfig instanceConfig) {
        this.httpAgent = httpAgent;
        this.instanceConfig = instanceConfig;
        heartbeatExecutor = ThreadPoolBuilder.builder()
                .poolThreadSize(1, 5)
                .keepAliveTime(0, TimeUnit.SECONDS)
                .workQueue(new SynchronousQueue())
                .threadFactory("DiscoveryClient-HeartbeatExecutor", true)
                .build();

        scheduler = Executors.newScheduledThreadPool(2,
                ThreadFactoryBuilder.builder()
                        .daemon(true)
                        .prefix("DiscoveryClient-Scheduler")
                        .build()
        );

        register();

        // init the schedule tasks
        initScheduledTasks();
    }

    private void initScheduledTasks() {
        scheduler.schedule(new HeartbeatThread(), 30, TimeUnit.SECONDS);
    }

    boolean register() {
        log.info("{}{} :: registering service...", PREFIX, appPathIdentifier);
        String urlPath = "/apps/" + appPathIdentifier;

        Result registerResult = null;
        try {
            registerResult = httpAgent.httpPostByDiscovery(urlPath, instanceConfig);
        } catch (Exception ex) {
            log.warn("{} {} - registration failed :: {}.", PREFIX, appPathIdentifier, ex.getMessage(), ex);
            throw ex;
        }

        if (log.isInfoEnabled()) {
            log.info("{} {} - registration status: {}.", PREFIX, appPathIdentifier, registerResult.getCode());
        }

        return registerResult.isSuccess();
    }

    public class HeartbeatThread implements Runnable {

        @Override
        public void run() {
            if (renew()) {
                lastSuccessfulHeartbeatTimestamp = System.currentTimeMillis();
            }
        }

    }

    boolean renew() {

        return true;
    }

}
