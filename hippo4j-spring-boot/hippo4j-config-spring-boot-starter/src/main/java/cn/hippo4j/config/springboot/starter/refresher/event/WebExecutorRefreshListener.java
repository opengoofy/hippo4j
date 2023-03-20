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

import cn.hippo4j.adapter.web.WebThreadPoolHandlerChoose;
import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.common.api.NotifyRequest;
import cn.hippo4j.common.api.ThreadPoolConfigChange;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import cn.hippo4j.config.springboot.starter.config.WebThreadPoolProperties;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.util.Objects;
import java.util.Optional;

import static cn.hippo4j.config.springboot.starter.refresher.event.Hippo4jConfigDynamicRefreshEventOrder.WEB_EXECUTOR_LISTENER;

/**
 * Web executor refresh listener.
 */
@Slf4j
@Order(WEB_EXECUTOR_LISTENER)
@SuppressWarnings("all")
public class WebExecutorRefreshListener extends AbstractRefreshListener<WebThreadPoolProperties> {

    private final ThreadPoolConfigChange configChange;

    public WebExecutorRefreshListener(ThreadPoolConfigChange configChange) {
        this.configChange = configChange;
    }

    @Override
    public String getNodes(WebThreadPoolProperties properties) {
        return properties.getNodes();
    }

    @Override
    public void onApplicationEvent(Hippo4jConfigDynamicRefreshEvent threadPoolDynamicRefreshEvent) {
        BootstrapConfigProperties bindableCoreProperties = threadPoolDynamicRefreshEvent.getBootstrapConfigProperties();
        if (bindableCoreProperties.getWeb() == null) {
            return;
        }
        try {
            ThreadPoolParameterInfo nowParameter = buildWebPoolParameter(bindableCoreProperties);
            if (nowParameter != null) {
                WebThreadPoolHandlerChoose webThreadPoolHandlerChoose = ApplicationContextHolder.getBean(WebThreadPoolHandlerChoose.class);
                WebThreadPoolService webThreadPoolService = webThreadPoolHandlerChoose.choose();
                ThreadPoolParameter beforeParameter = webThreadPoolService.getWebThreadPoolParameter();

                // Prevent NPE exceptions from being thrown when certain parameters are not configured.
                if (nowParameter.getCoreSize() == null) {
                    nowParameter.setCoreSize(beforeParameter.getCoreSize());
                }
                if (nowParameter.getMaxSize() == null) {
                    nowParameter.setMaxSize(beforeParameter.getMaxSize());
                }
                if (nowParameter.getKeepAliveTime() == null) {
                    nowParameter.setKeepAliveTime(beforeParameter.getKeepAliveTime());
                }
                if (!Objects.equals(beforeParameter.getCoreSize(), nowParameter.getCoreSize())
                        || !Objects.equals(beforeParameter.getMaxSize(), nowParameter.getMaxSize())
                        || !Objects.equals(beforeParameter.getKeepAliveTime(), nowParameter.getKeepAliveTime())) {
                    webThreadPoolService.updateWebThreadPool(nowParameter);
                    configChange.sendPoolConfigChange(buildChangeRequest(beforeParameter, nowParameter));
                }
            }
        } catch (Exception ex) {
            log.error("Failed to modify web thread pool.", ex);
        }
    }

    /**
     * Constructing a request for thread pool parameter change notification
     * @param before
     * @param now
     * @return
     */
    private ChangeParameterNotifyRequest buildChangeRequest(ThreadPoolParameter before, ThreadPoolParameter now) {
        return ChangeParameterNotifyRequest.builder()
                .beforeCorePoolSize(before.getCoreSize())
                .nowCorePoolSize(now.getCoreSize())
                .beforeMaximumPoolSize(before.getMaxSize())
                .nowMaximumPoolSize(now.getMaxSize())
                .beforeKeepAliveTime(before.getKeepAliveTime())
                .nowKeepAliveTime(now.getKeepAliveTime()).build();
    }

    private ThreadPoolParameterInfo buildWebPoolParameter(BootstrapConfigProperties bindableCoreProperties) {
        ThreadPoolParameterInfo threadPoolParameterInfo = null;
        WebThreadPoolProperties webThreadPoolProperties = bindableCoreProperties.getWeb();

        if (webThreadPoolProperties != null && webThreadPoolProperties.getEnable() && match(webThreadPoolProperties)) {
            threadPoolParameterInfo = ThreadPoolParameterInfo.builder()
                    .coreSize(webThreadPoolProperties.getCorePoolSize())
                    .maximumPoolSize(webThreadPoolProperties.getMaximumPoolSize())
                    .keepAliveTime(webThreadPoolProperties.getKeepAliveTime())
                    .build();
        }
        return threadPoolParameterInfo;
    }
}
