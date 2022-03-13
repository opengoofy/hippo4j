package cn.hippo4j.core.starter.config;

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
import cn.hippo4j.core.starter.notify.CoreNotifyConfigBuilder;
import cn.hippo4j.core.starter.refresher.ApolloRefresherHandler;
import cn.hippo4j.core.starter.refresher.NacosCloudRefresherHandler;
import cn.hippo4j.core.starter.refresher.NacosRefresherHandler;
import cn.hippo4j.core.starter.support.DynamicThreadPoolPostProcessor;
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
 * Dynamic thread pool auto configuration.
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
    public NacosRefresherHandler nacosRefresherHandler(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                                       BootstrapCoreProperties bootstrapCoreProperties) {
        return new NacosRefresherHandler(threadPoolNotifyAlarmHandler, bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnClass(name = NACOS_CONFIG_MANAGER_KEY)
    public NacosCloudRefresherHandler nacosCloudRefresherHandler(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                                                 BootstrapCoreProperties bootstrapCoreProperties) {
        return new NacosCloudRefresherHandler(threadPoolNotifyAlarmHandler, bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnClass(name = APOLLO_CONFIG_KEY)
    public ApolloRefresherHandler apolloRefresher(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                                  BootstrapCoreProperties bootstrapCoreProperties) {
        return new ApolloRefresherHandler(threadPoolNotifyAlarmHandler, bootstrapCoreProperties);
    }

}
