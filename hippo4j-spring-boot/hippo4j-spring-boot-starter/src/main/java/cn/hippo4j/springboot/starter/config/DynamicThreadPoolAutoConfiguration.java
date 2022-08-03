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

package cn.hippo4j.springboot.starter.config;

import cn.hippo4j.adapter.base.ThreadPoolAdapterBeanContainer;
import cn.hippo4j.adapter.web.WebThreadPoolHandlerChoose;
import cn.hippo4j.common.api.ThreadDetailState;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.core.config.UtilAutoConfiguration;
import cn.hippo4j.core.enable.MarkerConfiguration;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.handler.DynamicThreadPoolBannerHandler;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.message.api.NotifyConfigBuilder;
import cn.hippo4j.message.config.MessageConfiguration;
import cn.hippo4j.message.service.AlarmControlHandler;
import cn.hippo4j.message.service.Hippo4jSendMessageService;
import cn.hippo4j.springboot.starter.controller.ThreadPoolAdapterController;
import cn.hippo4j.springboot.starter.controller.WebThreadPoolController;
import cn.hippo4j.springboot.starter.controller.WebThreadPoolRunStateController;
import cn.hippo4j.springboot.starter.core.*;
import cn.hippo4j.springboot.starter.event.ApplicationContentPostProcessor;
import cn.hippo4j.springboot.starter.monitor.ReportingEventExecutor;
import cn.hippo4j.springboot.starter.monitor.collect.RunTimeInfoCollector;
import cn.hippo4j.springboot.starter.monitor.send.MessageSender;
import cn.hippo4j.springboot.starter.monitor.send.http.HttpConnectSender;
import cn.hippo4j.springboot.starter.notify.ServerNotifyConfigBuilder;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import cn.hippo4j.springboot.starter.remote.HttpScheduledHealthCheck;
import cn.hippo4j.springboot.starter.remote.ServerHealthCheck;
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
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Dynamic thread-pool auto-configuration.
 *
 * @author chen.ma
 * @date 2021/6/22 09:20
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@EnableConfigurationProperties(BootstrapProperties.class)
@ConditionalOnProperty(prefix = BootstrapProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
@ImportAutoConfiguration({HttpClientConfiguration.class, NettyClientConfiguration.class, DiscoveryConfiguration.class, MessageConfiguration.class, UtilAutoConfiguration.class})
@Import(MonitorHandlerConfiguration.EmbeddedPrometheusMonitor.class)
public class DynamicThreadPoolAutoConfiguration {

    private final BootstrapProperties properties;

    private final ConfigurableEnvironment environment;

    @Bean
    public DynamicThreadPoolBannerHandler threadPoolBannerHandler() {
        return new DynamicThreadPoolBannerHandler(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder hippo4JApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @SuppressWarnings("all")
    public ConfigService configService(HttpAgent httpAgent, InetUtils hippo4JInetUtils, ServerHealthCheck serverHealthCheck) {
        String identify = IdentifyUtil.generate(environment, hippo4JInetUtils);
        return new ThreadPoolConfigService(httpAgent, identify, serverHealthCheck);
    }

    @Bean
    public ThreadPoolOperation threadPoolOperation(ConfigService configService) {
        return new ThreadPoolOperation(properties, configService);
    }

    @Bean
    @SuppressWarnings("all")
    public DynamicThreadPoolPostProcessor threadPoolBeanPostProcessor(HttpAgent httpAgent,
                                                                      ThreadPoolOperation threadPoolOperation,
                                                                      ApplicationContextHolder hippo4JApplicationContextHolder,
                                                                      ServerThreadPoolDynamicRefresh threadPoolDynamicRefresh) {
        return new DynamicThreadPoolPostProcessor(properties, httpAgent, threadPoolOperation, threadPoolDynamicRefresh);
    }

    @Bean
    @ConditionalOnMissingBean(value = ThreadDetailState.class)
    public ThreadDetailState baseThreadDetailStateHandler() {
        return new BaseThreadDetailStateHandler();
    }

    @Bean
    public WebThreadPoolRunStateController poolRunStateController(ThreadPoolRunStateHandler threadPoolRunStateHandler,
                                                                  ThreadDetailState threadDetailState) {
        return new WebThreadPoolRunStateController(threadPoolRunStateHandler, threadDetailState);
    }

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("all")
    public MessageSender messageSender(HttpAgent httpAgent) {
        return new HttpConnectSender(httpAgent);
    }

    @Bean
    public ReportingEventExecutor reportingEventExecutor(BootstrapProperties properties, MessageSender messageSender,
                                                         ServerHealthCheck serverHealthCheck) {
        return new ReportingEventExecutor(properties, messageSender, serverHealthCheck);
    }

    @Bean
    @SuppressWarnings("all")
    public ServerHealthCheck httpScheduledHealthCheck(HttpAgent httpAgent) {
        return new HttpScheduledHealthCheck(httpAgent);
    }

    @Bean
    public RunTimeInfoCollector runTimeInfoCollector() {
        return new RunTimeInfoCollector(properties);
    }

    @Bean
    @SuppressWarnings("all")
    public ThreadPoolAdapterController threadPoolAdapterController(InetUtils hippo4JInetUtils) {
        return new ThreadPoolAdapterController(environment, hippo4JInetUtils);
    }

    @Bean
    public ThreadPoolAdapterBeanContainer threadPoolAdapterBeanContainer() {
        return new ThreadPoolAdapterBeanContainer();
    }

    @Bean
    public ApplicationContentPostProcessor applicationContentPostProcessor() {
        return new ApplicationContentPostProcessor();
    }

    @Bean
    @SuppressWarnings("all")
    public WebThreadPoolController webThreadPoolController(WebThreadPoolHandlerChoose webThreadPoolServiceChoose) {
        return new WebThreadPoolController(webThreadPoolServiceChoose);
    }

    @Bean
    @SuppressWarnings("all")
    public ThreadPoolAdapterRegister threadPoolAdapterRegister(HttpAgent httpAgent, InetUtils hippo4JInetUtils) {
        return new ThreadPoolAdapterRegister(httpAgent, properties, environment, hippo4JInetUtils);
    }

    @Bean
    public NotifyConfigBuilder serverNotifyConfigBuilder(HttpAgent httpAgent,
                                                         BootstrapProperties properties,
                                                         AlarmControlHandler alarmControlHandler) {
        return new ServerNotifyConfigBuilder(httpAgent, properties, alarmControlHandler);
    }

    @Bean
    public ServerThreadPoolDynamicRefresh threadPoolDynamicRefresh(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler) {
        return new ServerThreadPoolDynamicRefresh(threadPoolNotifyAlarmHandler);
    }

    @Bean
    public ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler(Hippo4jSendMessageService hippoSendMessageService) {
        return new ThreadPoolNotifyAlarmHandler(hippoSendMessageService);
    }
}
