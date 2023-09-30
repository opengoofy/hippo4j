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

package cn.hippo4j.config.springboot.starter.refresher.event;

import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.config.springboot.starter.notify.ConfigModeNotifyConfigBuilder;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.core.service.ThreadPoolBaseSendMessageService;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;

import static cn.hippo4j.config.springboot.starter.refresher.event.ThreadPoolConfigDynamicRefreshEventOrder.PLATFORMS_LISTENER;

/**
 * Platforms refresh listener.
 */
@Order(PLATFORMS_LISTENER)
public class PlatformsRefreshListener extends AbstractRefreshListener<ExecutorProperties> {

    @Override
    public void onApplicationEvent(ThreadPoolConfigDynamicRefreshEvent threadPoolDynamicRefreshEvent) {
        BootstrapConfigProperties bindableConfigProperties = threadPoolDynamicRefreshEvent.getBootstrapConfigProperties();
        List<ExecutorProperties> executors = bindableConfigProperties.getExecutors();
        for (ExecutorProperties executorProperties : executors) {
            String threadPoolId = executorProperties.getThreadPoolId();
            ThreadPoolExecutorHolder executorHolder = ThreadPoolExecutorRegistry.getHolder(threadPoolId);
            ThreadPoolBaseSendMessageService sendMessageService = ApplicationContextHolder.getBean(ThreadPoolBaseSendMessageService.class);
            /**
             * TODO The logic here should be:
             *
             * 1. When the application starts, the thread pool parameters are not configured in the configuration center
             * 2. After the application starts, put the relevant configuration into the configuration center
             * 3. Use whether there is a thread pool notification as a judgment condition
             */
            List<NotifyConfigDTO> notifyConfigList = sendMessageService.getNotifyConfigs().get(threadPoolId);
            if (executorHolder != null && CollectionUtil.isEmpty(notifyConfigList)) {
                ConfigModeNotifyConfigBuilder configBuilder = ApplicationContextHolder.getBean(ConfigModeNotifyConfigBuilder.class);
                Map<String, List<NotifyConfigDTO>> notifyConfig = configBuilder.buildSingleNotifyConfig(executorProperties);
                sendMessageService.putPlatform(notifyConfig);
            }
        }
    }
}
