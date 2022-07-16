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

package cn.hippo4j.springboot.starter.core;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterCacheConfig;
import cn.hippo4j.adapter.base.ThreadPoolAdapterScheduler;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.web.base.Result;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import cn.hippo4j.springboot.starter.toolkit.CloudCommonIdUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;
import static cn.hippo4j.common.constant.Constants.REGISTER_ADAPTER_PATH;

/**
 * Thread-pool adapter register.
 */
@Slf4j
@AllArgsConstructor
@RequiredArgsConstructor
public class ThreadPoolAdapterRegister implements ApplicationRunner {

    private final HttpAgent httpAgent;

    private final BootstrapProperties properties;

    private final ConfigurableEnvironment environment;

    private final InetUtils hippo4JInetUtils;

    private final ThreadPoolAdapterScheduler threadPoolAdapterScheduler;

    private List<ThreadPoolAdapterCacheConfig> cacheConfigList = Lists.newArrayList();



    @Override
    public void run(ApplicationArguments args) throws Exception {

        ScheduledExecutorService scheduler = threadPoolAdapterScheduler.getScheduler();
        int taskIntervalSeconds = threadPoolAdapterScheduler.getTaskIntervalSeconds();
        ThreadPoolAdapterRegisterTask threadPoolAdapterRegisterTask = new ThreadPoolAdapterRegisterTask(scheduler, taskIntervalSeconds);
        scheduler.schedule(threadPoolAdapterRegisterTask, threadPoolAdapterScheduler.getTaskIntervalSeconds(), TimeUnit.SECONDS);
    }

    public List<ThreadPoolAdapterCacheConfig> getThreadPoolAdapterCacheConfigs(){
        Map<String, ThreadPoolAdapter> threadPoolAdapterMap = ApplicationContextHolder.getBeansOfType(ThreadPoolAdapter.class);
        List<ThreadPoolAdapterCacheConfig> cacheConfigList = Lists.newArrayList();
        threadPoolAdapterMap.forEach((key, val) -> {
            List<ThreadPoolAdapterState> threadPoolStates = val.getThreadPoolStates();
            if (CollectionUtil.isEmpty(threadPoolStates)) {
                return;
            }
            ThreadPoolAdapterCacheConfig cacheConfig = new ThreadPoolAdapterCacheConfig();
            cacheConfig.setMark(val.mark());
            String tenantItemKey = properties.getNamespace() + IDENTIFY_SLICER_SYMBOL + properties.getItemId();
            cacheConfig.setTenantItemKey(tenantItemKey);
            cacheConfig.setClientIdentify(IdentifyUtil.getIdentify());
            String clientAddress = CloudCommonIdUtil.getClientIpPort(environment, hippo4JInetUtils);
            cacheConfig.setClientAddress(clientAddress);
            cacheConfig.setThreadPoolAdapterStates(threadPoolStates);
            cacheConfigList.add(cacheConfig);
        });
        return cacheConfigList;
    }

    public void doRegister(List<ThreadPoolAdapterCacheConfig> cacheConfigList){
        if (CollectionUtil.isNotEmpty(cacheConfigList)) {
            try {
                Result result = httpAgent.httpPost(REGISTER_ADAPTER_PATH, cacheConfigList);
                if (!result.isSuccess()) {
                    log.warn("Failed to register third-party thread pool data.");
                }
            } catch (Throwable ex) {
                log.error("Failed to register third-party thread pool data.", ex);
            }
        }
    }

    public void register() {
        List<ThreadPoolAdapterCacheConfig> threadPoolAdapterCacheConfigs = getThreadPoolAdapterCacheConfigs();
        doRegister(threadPoolAdapterCacheConfigs);
    }

    class ThreadPoolAdapterRegisterTask implements Runnable{

        private ScheduledExecutorService scheduler;

        private int taskIntervalSeconds;

        public ThreadPoolAdapterRegisterTask(ScheduledExecutorService scheduler, int taskIntervalSeconds){
            this.scheduler = scheduler;
            this.taskIntervalSeconds = taskIntervalSeconds;
        }

        @Override
        public void run() {
            try {
                List<ThreadPoolAdapterCacheConfig> newThreadPoolAdapterCacheConfigs = getThreadPoolAdapterCacheConfigs();

                boolean registerFlag = compareThreadPoolAdapterCacheConfigs(newThreadPoolAdapterCacheConfigs, cacheConfigList);

                cacheConfigList = newThreadPoolAdapterCacheConfigs;

                if (registerFlag) {
                    doRegister(cacheConfigList);
                }
            }catch (Exception e){
                log.error("Register Task Error",e);
            }finally {
                if (!scheduler.isShutdown()) {
                    scheduler.schedule(this, taskIntervalSeconds, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    private boolean compareThreadPoolAdapterCacheConfigs(List<ThreadPoolAdapterCacheConfig> newThreadPoolAdapterCacheConfigs,
                                                         List<ThreadPoolAdapterCacheConfig> oldThreadPoolAdapterCacheConfigs){
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
            }else {
                if (newValue.size() != oldValue.size()) {
                    registerFlag = true;
                    break;
                }
            }
        }
        return registerFlag;
    }
}
