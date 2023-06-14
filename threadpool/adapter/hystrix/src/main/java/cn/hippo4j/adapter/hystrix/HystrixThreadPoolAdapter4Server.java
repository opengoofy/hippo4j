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
import cn.hippo4j.adapter.base.ThreadPoolAdapterCacheConfig;
import cn.hippo4j.adapter.base.ThreadPoolAdapterRegisterAction;
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.core.config.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * hystrix thread-pool adapter for hippo4j server.
 */
@Slf4j
public class HystrixThreadPoolAdapter4Server extends AbstractHystrixThreadPoolAdapter {

    public HystrixThreadPoolAdapter4Server(ThreadPoolAdapterScheduler threadPoolAdapterScheduler) {
        super(threadPoolAdapterScheduler);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        super.onApplicationEvent(event);
        ScheduledExecutorService scheduler = threadPoolAdapterScheduler.getScheduler();
        int taskIntervalSeconds = threadPoolAdapterScheduler.getTaskIntervalSeconds();
        // Periodically refresh registration.
        ThreadPoolAdapterRegisterAction threadPoolAdapterRegisterAction = ApplicationContextHolder.getBean(ThreadPoolAdapterRegisterAction.class);
        Map<String, ? extends HystrixThreadPoolAdapter4Server> beansOfType = ApplicationContextHolder.getBeansOfType(this.getClass());
        Map<String, ThreadPoolAdapter> map = new HashMap<>(beansOfType);
        ThreadPoolAdapterRegisterTask threadPoolAdapterRegisterTask = new ThreadPoolAdapterRegisterTask(scheduler, taskIntervalSeconds, map, threadPoolAdapterRegisterAction);
        scheduler.schedule(threadPoolAdapterRegisterTask, threadPoolAdapterScheduler.getTaskIntervalSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Thread Pool Adapter Register Task
     */
    static class ThreadPoolAdapterRegisterTask implements Runnable {

        private final ScheduledExecutorService scheduler;

        private final int taskIntervalSeconds;

        Map<String, ThreadPoolAdapter> threadPoolAdapterMap;

        ThreadPoolAdapterRegisterAction threadPoolAdapterRegisterAction;

        private List<ThreadPoolAdapterCacheConfig> cacheConfigList = new ArrayList<>();

        ThreadPoolAdapterRegisterTask(ScheduledExecutorService scheduler, int taskIntervalSeconds,
                                      Map<String, ThreadPoolAdapter> threadPoolAdapterMap,
                                      ThreadPoolAdapterRegisterAction threadPoolAdapterRegisterAction) {
            this.scheduler = scheduler;
            this.taskIntervalSeconds = taskIntervalSeconds;
            this.threadPoolAdapterMap = threadPoolAdapterMap;
            this.threadPoolAdapterRegisterAction = threadPoolAdapterRegisterAction;
        }

        @Override
        public void run() {
            try {
                List<ThreadPoolAdapterCacheConfig> newThreadPoolAdapterCacheConfigs = threadPoolAdapterRegisterAction.getThreadPoolAdapterCacheConfigs(threadPoolAdapterMap);
                boolean registerFlag = compareThreadPoolAdapterCacheConfigs(newThreadPoolAdapterCacheConfigs, cacheConfigList);
                cacheConfigList = newThreadPoolAdapterCacheConfigs;
                if (registerFlag) {
                    threadPoolAdapterRegisterAction.doRegister(cacheConfigList);
                }
            } catch (Exception ex) {
                log.error("Register task error.", ex);
            } finally {
                if (!scheduler.isShutdown()) {
                    scheduler.schedule(this, taskIntervalSeconds, TimeUnit.MILLISECONDS);
                }
            }
        }

        private boolean compareThreadPoolAdapterCacheConfigs(List<ThreadPoolAdapterCacheConfig> newThreadPoolAdapterCacheConfigs,
                                                             List<ThreadPoolAdapterCacheConfig> oldThreadPoolAdapterCacheConfigs) {
            boolean registerFlag = false;
            Map<String, List<ThreadPoolAdapterState>> newThreadPoolAdapterCacheConfigMap =
                    newThreadPoolAdapterCacheConfigs.stream().collect(Collectors.toMap(
                            ThreadPoolAdapterCacheConfig::getMark, ThreadPoolAdapterCacheConfig::getThreadPoolAdapterStates, (k1, k2) -> k2));
            Map<String, List<ThreadPoolAdapterState>> oldThreadPoolAdapterCacheConfigMap =
                    oldThreadPoolAdapterCacheConfigs.stream().collect(Collectors.toMap(
                            ThreadPoolAdapterCacheConfig::getMark, ThreadPoolAdapterCacheConfig::getThreadPoolAdapterStates, (k1, k2) -> k2));
            for (Map.Entry<String, List<ThreadPoolAdapterState>> entry : newThreadPoolAdapterCacheConfigMap.entrySet()) {
                String key = entry.getKey();
                List<ThreadPoolAdapterState> newValue = entry.getValue();
                List<ThreadPoolAdapterState> oldValue = oldThreadPoolAdapterCacheConfigMap.get(key);
                if (oldValue == null) {
                    registerFlag = true;
                    break;
                } else {
                    if (newValue.size() != oldValue.size()) {
                        registerFlag = true;
                        break;
                    }
                }
            }
            return registerFlag;
        }
    }
}
