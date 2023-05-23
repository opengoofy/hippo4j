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

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.common.model.executor.ExecutorProperties;
import cn.hippo4j.config.springboot.starter.notify.ConfigModeNotifyConfigBuilder;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.service.Hippo4jBaseSendMessageService;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;

import static cn.hippo4j.config.springboot.starter.refresher.event.Hippo4jConfigDynamicRefreshEventOrder.PLATFORMS_LISTENER;

/**
 * Platforms refresh listener.
 */
@Order(PLATFORMS_LISTENER)
public class PlatformsRefreshListener extends AbstractRefreshListener<ExecutorProperties> {

    @Override
    public void onApplicationEvent(Hippo4jConfigDynamicRefreshEvent threadPoolDynamicRefreshEvent) {
        BootstrapConfigProperties bindableConfigProperties = threadPoolDynamicRefreshEvent.getBootstrapConfigProperties();
        List<ExecutorProperties> executors = bindableConfigProperties.getExecutors();
        for (ExecutorProperties executorProperties : executors) {
            String threadPoolId = executorProperties.getThreadPoolId();
            DynamicThreadPoolWrapper wrapper = GlobalThreadPoolManage.getExecutorService(threadPoolId);
            if (wrapper != null && !wrapper.isInitFlag()) {
                Hippo4jBaseSendMessageService sendMessageService = ApplicationContextHolder.getBean(Hippo4jBaseSendMessageService.class);
                ConfigModeNotifyConfigBuilder configBuilder = ApplicationContextHolder.getBean(ConfigModeNotifyConfigBuilder.class);
                Map<String, List<NotifyConfigDTO>> notifyConfig = configBuilder.buildSingleNotifyConfig(executorProperties);
                sendMessageService.putPlatform(notifyConfig);
                wrapper.setInitFlag(Boolean.TRUE);
            }
        }
    }
}
