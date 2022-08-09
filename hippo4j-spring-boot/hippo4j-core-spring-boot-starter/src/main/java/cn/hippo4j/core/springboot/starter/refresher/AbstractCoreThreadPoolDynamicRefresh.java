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

package cn.hippo4j.core.springboot.starter.refresher;

import cn.hippo4j.common.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.core.springboot.starter.config.BootstrapCoreProperties;
import cn.hippo4j.core.springboot.starter.parser.ConfigParserHandler;
import cn.hippo4j.core.springboot.starter.refresher.event.Hippo4jCoreDynamicRefreshEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * Abstract core thread-pool dynamic refresh.
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractCoreThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh, InitializingBean {

    protected final BootstrapCoreProperties bootstrapCoreProperties;

    protected final ExecutorService dynamicRefreshExecutorService = ThreadPoolBuilder.builder().singlePool("client.dynamic.refresh").build();

    public AbstractCoreThreadPoolDynamicRefresh() {
        bootstrapCoreProperties = ApplicationContextHolder.getBean(BootstrapCoreProperties.class);
    }

    @Override
    public void dynamicRefresh(String configContent) {
        dynamicRefresh(configContent, null);
    }

    @Override
    public void dynamicRefresh(String configContent, Map<String, Object> newValueChangeMap) {
        try {
            Map<Object, Object> configInfo = ConfigParserHandler.getInstance().parseConfig(configContent, bootstrapCoreProperties.getConfigFileType());
            if (CollectionUtil.isNotEmpty(newValueChangeMap)) {
                Optional.ofNullable(configInfo).ifPresent(each -> each.putAll(newValueChangeMap));
            }
            BootstrapCoreProperties bindableCoreProperties = BootstrapCorePropertiesBinderAdapt.bootstrapCorePropertiesBinder(configInfo, bootstrapCoreProperties);
            ApplicationContextHolder.getInstance().publishEvent(new Hippo4jCoreDynamicRefreshEvent(this, bindableCoreProperties));
        } catch (Exception ex) {
            log.error("Hippo-4J core dynamic refresh failed.", ex);
        }
    }
}
