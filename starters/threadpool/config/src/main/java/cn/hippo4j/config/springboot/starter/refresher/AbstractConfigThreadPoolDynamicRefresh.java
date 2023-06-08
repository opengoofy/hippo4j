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

package cn.hippo4j.config.springboot.starter.refresher;

import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.config.springboot.starter.refresher.event.ThreadPoolConfigDynamicRefreshEvent;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import cn.hippo4j.threadpool.dynamic.api.BootstrapPropertiesInterface;
import cn.hippo4j.threadpool.dynamic.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.threadpool.dynamic.mode.config.parser.ConfigParserHandler;
import cn.hippo4j.threadpool.dynamic.mode.config.properties.BootstrapConfigProperties;
import cn.hippo4j.threadpool.dynamic.mode.config.refresher.BootstrapConfigPropertiesBinderAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 * Abstract config thread-pool dynamic refresh.
 */
@Slf4j
public abstract class AbstractConfigThreadPoolDynamicRefresh implements ThreadPoolDynamicRefresh, InitializingBean, ApplicationRunner {

    private final BootstrapConfigPropertiesBinderAdapter bootstrapConfigPropertiesBinderAdapter;
    protected BootstrapPropertiesInterface bootstrapConfigProperties;

    protected final ExecutorService dynamicRefreshExecutorService = ThreadPoolBuilder.builder().singlePool("client.dynamic.refresh").build();

    public AbstractConfigThreadPoolDynamicRefresh() {
        bootstrapConfigProperties = ApplicationContextHolder.getBean(BootstrapPropertiesInterface.class);
        bootstrapConfigPropertiesBinderAdapter = ApplicationContextHolder.getBean(BootstrapConfigPropertiesBinderAdapter.class);
    }

    @Override
    public void dynamicRefresh(String configContent) {
        dynamicRefresh(configContent, new HashMap<>());
    }

    @Override
    public void dynamicRefresh(String configContent, Map<String, Object> newValueChangeMap) {
        try {
            BootstrapConfigProperties actualBootstrapConfigProperties = (BootstrapConfigProperties) bootstrapConfigProperties;
            Map<Object, Object> configInfo = ConfigParserHandler.getInstance().parseConfig(configContent, actualBootstrapConfigProperties.getConfigFileType());
            if (CollectionUtil.isNotEmpty(newValueChangeMap)) {
                Optional.ofNullable(configInfo).ifPresent(each -> each.putAll(newValueChangeMap));
            }
            BootstrapPropertiesInterface binderCoreProperties = bootstrapConfigPropertiesBinderAdapter.bootstrapCorePropertiesBinder(configInfo, bootstrapConfigProperties);
            publishDynamicThreadPoolEvent((BootstrapConfigProperties) binderCoreProperties);
        } catch (Exception ex) {
            log.error("Hippo4j config mode dynamic refresh failed.", ex);
        }
    }

    private void publishDynamicThreadPoolEvent(BootstrapConfigProperties configProperties) {
        ApplicationContextHolder.getInstance().publishEvent(new ThreadPoolConfigDynamicRefreshEvent(this, configProperties));
    }

    @Override
    public void afterPropertiesSet() {
        try {
            registerListener();
        } catch (Exception ex) {
            log.error("Hippo4j failed to initialize register listener.", ex);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            publishDynamicThreadPoolEvent((BootstrapConfigProperties) bootstrapConfigProperties);
        } catch (Exception ex) {
            log.error("Hippo4j failed to initialize update configuration.", ex);
        }
    }
}
