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

package cn.hippo4j.config.springboot.starter.config;

import cn.hippo4j.common.api.ThreadPoolCheckAlarm;
import cn.hippo4j.common.api.ThreadPoolConfigChange;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.config.springboot.starter.monitor.ThreadPoolMonitorExecutor;
import cn.hippo4j.config.springboot.starter.notify.CoreNotifyConfigBuilder;
import cn.hippo4j.config.springboot.starter.refresher.event.AdapterExecutorsRefreshListener;
import cn.hippo4j.config.springboot.starter.refresher.event.DynamicThreadPoolRefreshListener;
import cn.hippo4j.config.springboot.starter.refresher.event.PlatformsRefreshListener;
import cn.hippo4j.config.springboot.starter.refresher.event.WebExecutorRefreshListener;
import cn.hippo4j.config.springboot.starter.support.DynamicThreadPoolAdapterRegister;
import cn.hippo4j.config.springboot.starter.support.DynamicThreadPoolConfigService;
import cn.hippo4j.config.springboot.starter.support.DynamicThreadPoolPostProcessor;
import cn.hippo4j.core.config.UtilAutoConfiguration;
import cn.hippo4j.core.enable.MarkerConfiguration;
import cn.hippo4j.core.handler.DynamicThreadPoolBannerHandler;
import cn.hippo4j.message.api.NotifyConfigBuilder;
import cn.hippo4j.message.config.MessageConfiguration;
import cn.hippo4j.message.service.AlarmControlHandler;
import cn.hippo4j.message.service.DefaultThreadPoolCheckAlarmHandler;
import cn.hippo4j.message.service.DefaultThreadPoolConfigChangeHandler;
import cn.hippo4j.message.service.Hippo4jBaseSendMessageService;
import cn.hippo4j.message.service.Hippo4jSendMessageService;
import cn.hippo4j.springboot.starter.adapter.web.WebAdapterConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Dynamic thread-pool auto-configuration.
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@ConditionalOnProperty(prefix = BootstrapConfigProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
@EnableConfigurationProperties(BootstrapConfigProperties.class)
@Import(ConfigHandlerConfiguration.class)
@ImportAutoConfiguration({WebAdapterConfiguration.class, UtilAutoConfiguration.class, MessageConfiguration.class})
public class DynamicThreadPoolAutoConfiguration {

    private final BootstrapConfigProperties bootstrapConfigProperties;

    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder hippo4JApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public NotifyConfigBuilder notifyConfigBuilder(AlarmControlHandler alarmControlHandler) {
        return new CoreNotifyConfigBuilder(alarmControlHandler, bootstrapConfigProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolCheckAlarm defaultThreadPoolCheckAlarmHandler(Hippo4jSendMessageService hippo4jSendMessageService) {
        return new DefaultThreadPoolCheckAlarmHandler(hippo4jSendMessageService);
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolConfigChange defaultThreadPoolConfigChangeHandler(Hippo4jSendMessageService hippo4jSendMessageService) {
        return new DefaultThreadPoolConfigChangeHandler(hippo4jSendMessageService);
    }

    @Bean
    public DynamicThreadPoolPostProcessor dynamicThreadPoolPostProcessor(ApplicationContextHolder hippo4JApplicationContextHolder) {
        return new DynamicThreadPoolPostProcessor(bootstrapConfigProperties);
    }

    @Bean
    public ThreadPoolMonitorExecutor hippo4jDynamicThreadPoolMonitorExecutor() {
        return new ThreadPoolMonitorExecutor(bootstrapConfigProperties);
    }

    @Bean
    @SuppressWarnings("all")
    public DynamicThreadPoolRefreshListener hippo4jExecutorsListener(ThreadPoolConfigChange threadPoolConfigChange,
                                                                     CoreNotifyConfigBuilder coreNotifyConfigBuilder,
                                                                     Hippo4jBaseSendMessageService hippoBaseSendMessageService) {
        return new DynamicThreadPoolRefreshListener(threadPoolConfigChange, coreNotifyConfigBuilder, hippoBaseSendMessageService);
    }

    @Bean
    public AdapterExecutorsRefreshListener hippo4jAdapterExecutorsListener() {
        return new AdapterExecutorsRefreshListener();
    }

    @Bean
    public PlatformsRefreshListener hippo4jPlatformsListener() {
        return new PlatformsRefreshListener();
    }

    @Bean
    public WebExecutorRefreshListener hippo4jWebExecutorListener() {
        return new WebExecutorRefreshListener();
    }

    @Bean
    public DynamicThreadPoolAdapterRegister threadPoolAdapterRegister() {
        return new DynamicThreadPoolAdapterRegister(bootstrapConfigProperties);
    }

    @Bean
    public DynamicThreadPoolBannerHandler threadPoolBannerHandler(ObjectProvider<BuildProperties> buildProperties) {
        return new DynamicThreadPoolBannerHandler(bootstrapConfigProperties, buildProperties.getIfAvailable());
    }

    @Bean
    public DynamicThreadPoolConfigService dynamicThreadPoolConfigService() {
        return new DynamicThreadPoolConfigService();
    }
}
