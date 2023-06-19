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
import cn.hippo4j.common.api.ThreadPoolConfigChange;
import cn.hippo4j.common.model.ThreadPoolParameter;
import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.WebExecutorProperties;
import cn.hippo4j.threadpool.message.core.request.WebChangeParameterNotifyRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.util.Objects;

import static cn.hippo4j.config.springboot.starter.refresher.event.ThreadPoolConfigDynamicRefreshEventOrder.WEB_EXECUTOR_LISTENER;

/**
 * Web executor refresh listener.
 */
@Slf4j
@Order(WEB_EXECUTOR_LISTENER)
@SuppressWarnings("rawtypes")
public class WebExecutorRefreshListener extends AbstractRefreshListener<WebExecutorProperties> {

    private final ThreadPoolConfigChange configChange;

    public WebExecutorRefreshListener(ThreadPoolConfigChange configChange) {
        this.configChange = configChange;
    }

    @Override
    public String getNodes(WebExecutorProperties properties) {
        return properties.getNodes();
    }

    @Override
    public void onApplicationEvent(ThreadPoolConfigDynamicRefreshEvent threadPoolDynamicRefreshEvent) {
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
                    configChange.sendPoolConfigChange(buildChangeRequest(beforeParameter, nowParameter, webThreadPoolService));
                }
            }
        } catch (Exception ex) {
            log.error("Failed to modify web thread pool.", ex);
        }
    }

    /**
     * Constructing a request for web thread pool parameter change notification
     *
     * @param before
     * @param now
     * @return
     */
    private WebChangeParameterNotifyRequest buildChangeRequest(ThreadPoolParameter before, ThreadPoolParameter now,
                                                               WebThreadPoolService webThreadPoolService) {
        WebChangeParameterNotifyRequest changeNotifyRequest = WebChangeParameterNotifyRequest.builder()
                .beforeCorePoolSize(before.getCoreSize())
                .nowCorePoolSize(now.getCoreSize())
                .beforeMaximumPoolSize(before.getMaxSize())
                .nowMaximumPoolSize(now.getMaxSize())
                .beforeKeepAliveTime(before.getKeepAliveTime())
                .nowKeepAliveTime(now.getKeepAliveTime()).build();
        changeNotifyRequest.setThreadPoolId(webThreadPoolService.getWebContainerType().getName());
        return changeNotifyRequest;
    }

    private ThreadPoolParameterInfo buildWebPoolParameter(BootstrapConfigProperties bindableCoreProperties) {
        ThreadPoolParameterInfo threadPoolParameterInfo = null;
        WebExecutorProperties webThreadPoolProperties = bindableCoreProperties.getWeb();

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
