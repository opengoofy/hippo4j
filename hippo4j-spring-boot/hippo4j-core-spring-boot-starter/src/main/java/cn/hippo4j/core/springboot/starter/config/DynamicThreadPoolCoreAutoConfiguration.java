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

import cn.hippo4j.common.api.NotifyConfigBuilder;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.notify.AlarmControlHandler;
import cn.hippo4j.common.notify.HippoBaseSendMessageService;
import cn.hippo4j.common.notify.HippoSendMessageService;
import cn.hippo4j.common.notify.SendMessageHandler;
import cn.hippo4j.common.notify.platform.DingSendMessageHandler;
import cn.hippo4j.common.notify.platform.LarkSendMessageHandler;
import cn.hippo4j.common.notify.platform.WeChatSendMessageHandler;
import cn.hippo4j.core.config.UtilAutoConfiguration;
import cn.hippo4j.core.config.WebThreadPoolConfiguration;
import cn.hippo4j.core.enable.MarkerConfiguration;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.core.springboot.starter.monitor.DynamicThreadPoolMonitorExecutor;
import cn.hippo4j.core.springboot.starter.monitor.LogMonitorHandler;
import cn.hippo4j.core.springboot.starter.monitor.MetricMonitorHandler;
import cn.hippo4j.core.springboot.starter.notify.CoreNotifyConfigBuilder;
import cn.hippo4j.core.springboot.starter.refresher.ApolloRefresherHandler;
import cn.hippo4j.core.springboot.starter.refresher.NacosCloudRefresherHandler;
import cn.hippo4j.core.springboot.starter.refresher.NacosRefresherHandler;
import cn.hippo4j.core.springboot.starter.refresher.ZookeeperRefresherHandler;
import cn.hippo4j.core.springboot.starter.refresher.event.ExecutorsListener;
import cn.hippo4j.core.springboot.starter.refresher.event.PlatformsListener;
import cn.hippo4j.core.springboot.starter.refresher.event.WebExecutorListener;
import cn.hippo4j.core.springboot.starter.support.DynamicThreadPoolPostProcessor;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Dynamic thread-pool auto configuration.
 *
 * @author chen.ma
 * @date 2022/2/25 00:21
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@EnableConfigurationProperties(BootstrapCoreProperties.class)
@ImportAutoConfiguration({UtilAutoConfiguration.class, WebThreadPoolConfiguration.class})
@ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
public class DynamicThreadPoolCoreAutoConfiguration {

    private final BootstrapCoreProperties bootstrapCoreProperties;

    private static final String NACOS_CONFIG_MANAGER_KEY = "com.alibaba.cloud.nacos.NacosConfigManager";

    private static final String NACOS_CONFIG_KEY = "com.alibaba.nacos.api.config";

    private static final String APOLLO_CONFIG_KEY = "com.ctrip.framework.apollo.ConfigService";

    private static final String ZK_CONFIG_KEY = "org.apache.curator.framework.CuratorFramework";

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder hippo4JApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public AlarmControlHandler alarmControlHandler() {
        return new AlarmControlHandler();
    }

    @Bean
    public NotifyConfigBuilder notifyConfigBuilder(AlarmControlHandler alarmControlHandler) {
        return new CoreNotifyConfigBuilder(alarmControlHandler, bootstrapCoreProperties);
    }

    @Bean
    public HippoSendMessageService hippoSendMessageService(NotifyConfigBuilder notifyConfigBuilder,
                                                           AlarmControlHandler alarmControlHandler) {
        return new HippoBaseSendMessageService(notifyConfigBuilder, alarmControlHandler);
    }

    @Bean
    public ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler(HippoSendMessageService hippoSendMessageService) {
        return new ThreadPoolNotifyAlarmHandler(hippoSendMessageService);
    }

    @Bean
    public SendMessageHandler dingSendMessageHandler() {
        return new DingSendMessageHandler();
    }

    @Bean
    public SendMessageHandler larkSendMessageHandler() {
        return new LarkSendMessageHandler();
    }

    @Bean
    public SendMessageHandler weChatSendMessageHandler() {
        return new WeChatSendMessageHandler();
    }

    @Bean
    public DynamicThreadPoolPostProcessor dynamicThreadPoolPostProcessor(ApplicationContextHolder hippo4JApplicationContextHolder) {
        return new DynamicThreadPoolPostProcessor(bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnClass(name = NACOS_CONFIG_KEY)
    @ConditionalOnMissingClass(NACOS_CONFIG_MANAGER_KEY)
    @ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, name = "nacos.data-id")
    public NacosRefresherHandler nacosRefresherHandler() {
        return new NacosRefresherHandler(bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnClass(name = NACOS_CONFIG_MANAGER_KEY)
    @ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, name = "nacos.data-id")
    public NacosCloudRefresherHandler nacosCloudRefresherHandler() {
        return new NacosCloudRefresherHandler(bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnClass(name = APOLLO_CONFIG_KEY)
    @ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, name = "apollo.namespace")
    public ApolloRefresherHandler apolloRefresher() {
        return new ApolloRefresherHandler(bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnClass(name = ZK_CONFIG_KEY)
    @ConditionalOnProperty(prefix = BootstrapCoreProperties.PREFIX, name = "zookeeper.zk-connect-str")
    public ZookeeperRefresherHandler zookeeperRefresher() {
        return new ZookeeperRefresherHandler(bootstrapCoreProperties);
    }

    @Bean
    public DynamicThreadPoolMonitorExecutor hippo4jDynamicThreadPoolMonitorExecutor() {
        return new DynamicThreadPoolMonitorExecutor(bootstrapCoreProperties);
    }

    @Bean
    public LogMonitorHandler hippo4jLogMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        return new LogMonitorHandler(threadPoolRunStateHandler);
    }

    @Bean
    public MetricMonitorHandler hippo4jMetricMonitorHandler(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        return new MetricMonitorHandler(threadPoolRunStateHandler);
    }

    @Bean
    public ExecutorsListener hippo4jExecutorsListener(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler) {
        return new ExecutorsListener(threadPoolNotifyAlarmHandler);
    }

    @Bean
    public PlatformsListener hippo4jPlatformsListener() {
        return new PlatformsListener();
    }

    @Bean
    public WebExecutorListener hippo4jWebExecutorListener() {
        return new WebExecutorListener();
    }
}
