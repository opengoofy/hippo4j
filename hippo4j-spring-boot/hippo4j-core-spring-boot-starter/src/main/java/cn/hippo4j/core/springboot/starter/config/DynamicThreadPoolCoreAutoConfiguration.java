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

package cn.hippo4j.core.springboot.starter.config;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.config.UtilAutoConfiguration;
import cn.hippo4j.core.enable.MarkerConfiguration;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.handler.DynamicThreadPoolBannerHandler;
import cn.hippo4j.core.springboot.starter.monitor.DynamicThreadPoolMonitorExecutor;
import cn.hippo4j.core.springboot.starter.notify.CoreNotifyConfigBuilder;
import cn.hippo4j.core.springboot.starter.refresher.event.AdapterExecutorsRefreshListener;
import cn.hippo4j.core.springboot.starter.refresher.event.DynamicThreadPoolRefreshListener;
import cn.hippo4j.core.springboot.starter.refresher.event.PlatformsRefreshListener;
import cn.hippo4j.core.springboot.starter.refresher.event.WebExecutorRefreshListener;
import cn.hippo4j.core.springboot.starter.support.DynamicThreadPoolAdapterRegister;
import cn.hippo4j.core.springboot.starter.support.DynamicThreadPoolConfigService;
import cn.hippo4j.core.springboot.starter.support.DynamicThreadPoolPostProcessor;
import cn.hippo4j.message.api.NotifyConfigBuilder;
import cn.hippo4j.message.config.MessageConfiguration;
import cn.hippo4j.message.service.AlarmControlHandler;
import cn.hippo4j.message.service.Hippo4jBaseSendMessageService;
import cn.hippo4j.message.service.Hippo4jSendMessageService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Dynamic thread-pool core auto configuration.
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@EnableConfigurationProperties(BootstrapCoreProperties.class)
@ImportAutoConfiguration({UtilAutoConfiguration.class, MessageConfiguration.class})
@ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
@Import({
        ConfigHandlerConfiguration.EmbeddedNacos.class, ConfigHandlerConfiguration.EmbeddedNacosCloud.class,
        ConfigHandlerConfiguration.EmbeddedApollo.class, ConfigHandlerConfiguration.EmbeddedZookeeper.class
})
public class DynamicThreadPoolCoreAutoConfiguration {

    private final BootstrapCoreProperties bootstrapCoreProperties;

    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder hippo4JApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public NotifyConfigBuilder notifyConfigBuilder(AlarmControlHandler alarmControlHandler) {
        return new CoreNotifyConfigBuilder(alarmControlHandler, bootstrapCoreProperties);
    }

    @Bean
    public ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler(Hippo4jSendMessageService hippoSendMessageService) {
        return new ThreadPoolNotifyAlarmHandler(hippoSendMessageService);
    }

    @Bean
    public DynamicThreadPoolPostProcessor dynamicThreadPoolPostProcessor(ApplicationContextHolder hippo4JApplicationContextHolder) {
        return new DynamicThreadPoolPostProcessor(bootstrapCoreProperties);
    }

    @Bean
    public DynamicThreadPoolMonitorExecutor hippo4jDynamicThreadPoolMonitorExecutor() {
        return new DynamicThreadPoolMonitorExecutor(bootstrapCoreProperties);
    }

    @Bean
    @SuppressWarnings("all")
    public DynamicThreadPoolRefreshListener hippo4jExecutorsListener(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                                                     CoreNotifyConfigBuilder coreNotifyConfigBuilder,
                                                                     Hippo4jBaseSendMessageService hippoBaseSendMessageService) {
        return new DynamicThreadPoolRefreshListener(threadPoolNotifyAlarmHandler, coreNotifyConfigBuilder, hippoBaseSendMessageService);
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
        return new DynamicThreadPoolAdapterRegister(bootstrapCoreProperties);
    }

    @Bean
    public DynamicThreadPoolBannerHandler threadPoolBannerHandler() {
        return new DynamicThreadPoolBannerHandler(bootstrapCoreProperties);
    }

    @Bean
    public DynamicThreadPoolConfigService dynamicThreadPoolConfigService() {
        return new DynamicThreadPoolConfigService();
    }
}
