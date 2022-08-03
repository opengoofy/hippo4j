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

package cn.hippo4j.core.springboot.starter.refresher.event;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.springboot.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.springboot.starter.config.ExecutorProperties;
import cn.hippo4j.core.springboot.starter.notify.CoreNotifyConfigBuilder;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.service.Hippo4jBaseSendMessageService;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;

import static cn.hippo4j.core.springboot.starter.refresher.event.Hippo4jCoreDynamicRefreshEventOrder.PLATFORMS_LISTENER;

/**
 * Platforms listener.
 */
@Order(PLATFORMS_LISTENER)
public class PlatformsListener implements ApplicationListener<Hippo4jCoreDynamicRefreshEvent> {

    @Override
    public void onApplicationEvent(Hippo4jCoreDynamicRefreshEvent threadPoolDynamicRefreshEvent) {
        BootstrapCoreProperties bindableCoreProperties = threadPoolDynamicRefreshEvent.getBootstrapCoreProperties();
        List<ExecutorProperties> executors = bindableCoreProperties.getExecutors();
        for (ExecutorProperties executor : executors) {
            String threadPoolId = executor.getThreadPoolId();
            DynamicThreadPoolWrapper wrapper = GlobalThreadPoolManage.getExecutorService(threadPoolId);
            if (wrapper != null && !wrapper.isInitFlag()) {
                Hippo4jBaseSendMessageService sendMessageService = ApplicationContextHolder.getBean(Hippo4jBaseSendMessageService.class);
                CoreNotifyConfigBuilder configBuilder = ApplicationContextHolder.getBean(CoreNotifyConfigBuilder.class);
                Map<String, List<NotifyConfigDTO>> notifyConfig = configBuilder.buildSingleNotifyConfig(executor);
                sendMessageService.putPlatform(notifyConfig);
                wrapper.setInitFlag(Boolean.TRUE);
            }
        }
    }
}
