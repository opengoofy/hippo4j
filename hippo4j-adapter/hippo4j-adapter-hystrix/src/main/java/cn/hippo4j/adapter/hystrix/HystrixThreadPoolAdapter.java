/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.adapter.hystrix;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterScheduler;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.CollectionUtil;
import com.google.common.collect.Maps;
import com.netflix.hystrix.HystrixThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * hystrix thread-pool adapter.
 */
@Slf4j
public class HystrixThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private static final String THREAD_POOL_FIELD = "threadPool";

    private static final String THREAD_POOLS_FIELD = "threadPools";

    private final Map<String, ThreadPoolExecutor> HYSTRIX_CONSUME_EXECUTOR = Maps.newHashMap();

    private ThreadPoolAdapterScheduler threadPoolAdapterScheduler;

    public HystrixThreadPoolAdapter(ThreadPoolAdapterScheduler threadPoolAdapterScheduler) {
        this.threadPoolAdapterScheduler = threadPoolAdapterScheduler;
    }

    @Override
    public String mark() {
        return "Hystrix";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        ThreadPoolAdapterState result = new ThreadPoolAdapterState();
        ThreadPoolExecutor threadPoolExecutor = HYSTRIX_CONSUME_EXECUTOR.get(identify);
        if (threadPoolExecutor != null) {
            result.setThreadPoolKey(identify);
            result.setCoreSize(threadPoolExecutor.getCorePoolSize());
            result.setMaximumSize(threadPoolExecutor.getMaximumPoolSize());
            return result;
        }
        log.warn("[{}] hystrix thread pool not found.", identify);
        return result;
    }

    @Override
    public List<ThreadPoolAdapterState> getThreadPoolStates() {
        List<ThreadPoolAdapterState> threadPoolAdapterStates = new ArrayList<>();
        HYSTRIX_CONSUME_EXECUTOR.forEach((kel, val) -> threadPoolAdapterStates.add(getThreadPoolState(String.valueOf(val))));
        return threadPoolAdapterStates;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        ThreadPoolExecutor threadPoolExecutor = HYSTRIX_CONSUME_EXECUTOR.get(threadPoolKey);
        if (threadPoolExecutor == null) {
            log.warn("[{}] hystrix thread pool not found.", threadPoolKey);
            return false;
        }
        int originalCoreSize = threadPoolExecutor.getCorePoolSize();
        int originalMaximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        threadPoolExecutor.setCorePoolSize(threadPoolAdapterParameter.getCorePoolSize());
        threadPoolExecutor.setMaximumPoolSize(threadPoolAdapterParameter.getMaximumPoolSize());
        log.info("[{}] hystrix thread pool parameter change. coreSize :: {}, maximumSize :: {}",
                threadPoolKey,
                String.format(CHANGE_DELIMITER, originalCoreSize, threadPoolExecutor.getCorePoolSize()),
                String.format(CHANGE_DELIMITER, originalMaximumPoolSize, threadPoolExecutor.getMaximumPoolSize()));
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ScheduledExecutorService scheduler = threadPoolAdapterScheduler.getScheduler();
        int taskIntervalSeconds = threadPoolAdapterScheduler.getTaskIntervalSeconds();
        HystrixThreadPoolRefreshTask hystrixThreadPoolRefreshTask = new HystrixThreadPoolRefreshTask(scheduler, taskIntervalSeconds);
        scheduler.schedule(hystrixThreadPoolRefreshTask, taskIntervalSeconds, TimeUnit.SECONDS);
    }

    public void hystrixThreadPoolRefresh() {
        try {
            Class<HystrixThreadPool.Factory> factoryClass = HystrixThreadPool.Factory.class;
            Field threadPoolsField = factoryClass.getDeclaredField(THREAD_POOLS_FIELD);
            threadPoolsField.setAccessible(true);
            ConcurrentHashMap<String, HystrixThreadPool> threadPools =
                    (ConcurrentHashMap<String, HystrixThreadPool>) threadPoolsField.get(factoryClass);
            if (CollectionUtil.isNotEmpty(threadPools)) {
                for (Map.Entry<String, HystrixThreadPool> stringHystrixThreadPoolEntry : threadPools.entrySet()) {
                    String key = stringHystrixThreadPoolEntry.getKey();
                    HystrixThreadPool value = stringHystrixThreadPoolEntry.getValue();
                    if (value instanceof HystrixThreadPool.HystrixThreadPoolDefault) {
                        HystrixThreadPool.HystrixThreadPoolDefault hystrixThreadPoolDefault =
                                (HystrixThreadPool.HystrixThreadPoolDefault) value;
                        Class<? extends HystrixThreadPool.HystrixThreadPoolDefault> hystrixThreadPoolDefaultClass = hystrixThreadPoolDefault.getClass();
                        Field threadPoolField = hystrixThreadPoolDefaultClass.getDeclaredField(THREAD_POOL_FIELD);
                        threadPoolField.setAccessible(true);
                        ThreadPoolExecutor threadPoolExecutor =
                                (ThreadPoolExecutor) threadPoolField.get(hystrixThreadPoolDefault);
                        HYSTRIX_CONSUME_EXECUTOR.put(key, threadPoolExecutor);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to get Hystrix thread pool.", e);
        }
    }

    class HystrixThreadPoolRefreshTask implements Runnable {

        private ScheduledExecutorService scheduler;

        private int taskIntervalSeconds;

        public HystrixThreadPoolRefreshTask(ScheduledExecutorService scheduler, int taskIntervalSeconds) {
            this.scheduler = scheduler;
            this.taskIntervalSeconds = taskIntervalSeconds;
        }

        @Override
        public void run() {
            try {
                hystrixThreadPoolRefresh();
            } finally {
                if (!scheduler.isShutdown()) {
                    scheduler.schedule(this, taskIntervalSeconds, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
