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
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.ThreadPoolExecutorUtil;
import com.netflix.hystrix.HystrixThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.common.constant.ChangeThreadPoolConstants.CHANGE_DELIMITER;

/**
 * abstract hystrix thread-pool adapter
 */
@Slf4j
public abstract class AbstractHystrixThreadPoolAdapter implements ThreadPoolAdapter, ApplicationListener<ApplicationStartedEvent> {

    private static final String THREAD_POOL_FIELD = "threadPool";

    private static final String THREAD_POOLS_FIELD = "threadPools";

    private final Map<String, ThreadPoolExecutor> hystrixConsumeExecutor = new HashMap<>();

    protected final ThreadPoolAdapterScheduler threadPoolAdapterScheduler;

    protected AbstractHystrixThreadPoolAdapter(ThreadPoolAdapterScheduler threadPoolAdapterScheduler) {
        this.threadPoolAdapterScheduler = threadPoolAdapterScheduler;
    }

    @Override
    public String mark() {
        return "Hystrix";
    }

    @Override
    public ThreadPoolAdapterState getThreadPoolState(String identify) {
        ThreadPoolAdapterState result = new ThreadPoolAdapterState();
        ThreadPoolExecutor threadPoolExecutor = hystrixConsumeExecutor.get(identify);
        if (threadPoolExecutor != null) {
            BlockingQueue<Runnable> blockingQueue = threadPoolExecutor.getQueue();
            result.setThreadPoolKey(identify);
            result.setCoreSize(threadPoolExecutor.getCorePoolSize());
            result.setMaximumSize(threadPoolExecutor.getMaximumPoolSize());
            result.setBlockingQueueCapacity(blockingQueue.size() + blockingQueue.remainingCapacity());
            return result;
        }
        log.warn("[{}] Hystrix thread pool not found.", identify);
        return result;
    }

    @Override
    public List<ThreadPoolAdapterState> getThreadPoolStates() {
        List<ThreadPoolAdapterState> threadPoolAdapterStates = new ArrayList<>();
        hystrixConsumeExecutor.forEach((kel, val) -> threadPoolAdapterStates.add(getThreadPoolState(kel)));
        return threadPoolAdapterStates;
    }

    @Override
    public boolean updateThreadPool(ThreadPoolAdapterParameter threadPoolAdapterParameter) {
        String threadPoolKey = threadPoolAdapterParameter.getThreadPoolKey();
        ThreadPoolExecutor threadPoolExecutor = hystrixConsumeExecutor.get(threadPoolKey);
        if (threadPoolExecutor == null) {
            log.warn("[{}] Hystrix thread pool not found.", threadPoolKey);
            return false;
        }
        int originalCoreSize = threadPoolExecutor.getCorePoolSize();
        int originalMaximumPoolSize = threadPoolExecutor.getMaximumPoolSize();
        ThreadPoolExecutorUtil.safeSetPoolSize(threadPoolExecutor, threadPoolAdapterParameter.getCorePoolSize(), threadPoolAdapterParameter.getMaximumPoolSize());
        log.info("[{}] Hystrix thread pool parameter change. coreSize: {}, maximumSize: {}",
                threadPoolKey,
                String.format(CHANGE_DELIMITER, originalCoreSize, threadPoolExecutor.getCorePoolSize()),
                String.format(CHANGE_DELIMITER, originalMaximumPoolSize, threadPoolExecutor.getMaximumPoolSize()));
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ScheduledExecutorService scheduler = threadPoolAdapterScheduler.getScheduler();
        int taskIntervalSeconds = threadPoolAdapterScheduler.getTaskIntervalSeconds();
        // Periodically update the Hystrix thread pool.
        HystrixThreadPoolRefreshTask hystrixThreadPoolRefreshTask = new HystrixThreadPoolRefreshTask(scheduler, taskIntervalSeconds);
        scheduler.schedule(hystrixThreadPoolRefreshTask, taskIntervalSeconds, TimeUnit.SECONDS);
    }

    /**
     * hystrix thread-pool refresh task
     */
    class HystrixThreadPoolRefreshTask implements Runnable {

        private final ScheduledExecutorService scheduler;

        private final int taskIntervalSeconds;

        HystrixThreadPoolRefreshTask(ScheduledExecutorService scheduler, int taskIntervalSeconds) {
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

        private void hystrixThreadPoolRefresh() {
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
                            hystrixConsumeExecutor.put(key, threadPoolExecutor);
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("Failed to get Hystrix thread pool.", ex);
            }
        }
    }
}
