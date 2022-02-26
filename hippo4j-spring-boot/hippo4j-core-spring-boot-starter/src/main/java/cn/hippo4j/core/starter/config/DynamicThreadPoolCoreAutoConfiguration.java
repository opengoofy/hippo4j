package cn.hippo4j.core.starter.config;

import cn.hippo4j.common.api.NotifyConfigBuilder;
import cn.hippo4j.common.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.notify.AlarmControlHandler;
import cn.hippo4j.common.notify.BaseSendMessageServiceImpl;
import cn.hippo4j.common.notify.HippoSendMessageService;
import cn.hippo4j.common.notify.SendMessageHandler;
import cn.hippo4j.common.notify.platform.DingSendMessageHandler;
import cn.hippo4j.common.notify.platform.LarkSendMessageHandler;
import cn.hippo4j.common.notify.platform.WeChatSendMessageHandler;
import cn.hippo4j.core.config.UtilAutoConfiguration;
import cn.hippo4j.core.executor.ThreadPoolNotifyAlarmHandler;
import cn.hippo4j.core.starter.notify.CoreNotifyConfigBuilder;
import cn.hippo4j.core.starter.refresher.CoreThreadPoolDynamicRefresh;
import cn.hippo4j.core.starter.refresher.NacosCloudRefresherHandler;
import cn.hippo4j.core.starter.refresher.NacosRefresherHandler;
import cn.hippo4j.core.starter.support.DynamicThreadPoolPostProcessor;
import com.alibaba.cloud.nacos.NacosConfigManager;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
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
@EnableConfigurationProperties(BootstrapCoreProperties.class)
@ImportAutoConfiguration({UtilAutoConfiguration.class})
public class DynamicThreadPoolCoreAutoConfiguration {

    private final BootstrapCoreProperties bootstrapCoreProperties;

    private static final String NACOS_KEY = "com.alibaba.cloud.nacos.NacosConfigManager";

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
        return new BaseSendMessageServiceImpl(notifyConfigBuilder, alarmControlHandler);
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
    public ThreadPoolDynamicRefresh coreThreadPoolDynamicRefresh(ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler) {
        return new CoreThreadPoolDynamicRefresh(threadPoolNotifyAlarmHandler);
    }

    @Bean
    public DynamicThreadPoolPostProcessor dynamicThreadPoolPostProcessor(ApplicationContextHolder hippo4JApplicationContextHolder) {
        return new DynamicThreadPoolPostProcessor(bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnMissingClass(NACOS_KEY)
    public NacosRefresherHandler nacosRefresherHandler() {
        return new NacosRefresherHandler();
    }

    @Bean
    @ConditionalOnClass(name = NACOS_KEY)
    public NacosCloudRefresherHandler nacosCloudRefresherHandler(NacosConfigManager nacosConfigManager) {
        return new NacosCloudRefresherHandler(nacosConfigManager);
    }

}
