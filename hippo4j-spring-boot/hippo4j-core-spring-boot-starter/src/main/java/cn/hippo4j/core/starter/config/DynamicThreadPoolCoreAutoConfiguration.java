package cn.hippo4j.core.starter.config;

import cn.hippo4j.common.api.NotifyConfigBuilder;
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
import cn.hippo4j.core.starter.refresher.NacosCloudRefresherHandler;
import cn.hippo4j.core.starter.refresher.NacosRefresherHandler;
import cn.hippo4j.core.starter.refresher.config.ConfigParser;
import cn.hippo4j.core.starter.refresher.config.impl.PropConfigParser;
import cn.hippo4j.core.starter.refresher.config.impl.YmlConfigParser;
import cn.hippo4j.core.starter.support.DynamicThreadPoolPostProcessor;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.List;

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

    private static final String NACOS_CONFIG_MANAGER_KEY = "com.alibaba.cloud.nacos.NacosConfigManager";

    private static final String NACOS_CONFIG_KEY = "com.alibaba.nacos.api.config";

    private final List<String> yamlList = Lists.newArrayList("yaml", "yml");
    private final List<String> propList = Lists.newArrayList("properties");

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
    public DynamicThreadPoolPostProcessor dynamicThreadPoolPostProcessor(ApplicationContextHolder hippo4JApplicationContextHolder) {
        return new DynamicThreadPoolPostProcessor(bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnClass(name = NACOS_CONFIG_KEY)
    @ConditionalOnMissingClass(NACOS_CONFIG_MANAGER_KEY)
    public NacosRefresherHandler nacosRefresherHandler(ConfigService configService,
                                                       ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                                       ConfigParser configParser,
                                                       BootstrapCoreProperties bootstrapCoreProperties) {
        return new NacosRefresherHandler(configService, threadPoolNotifyAlarmHandler, configParser, bootstrapCoreProperties);
    }

    @Bean
    @ConditionalOnClass(name = NACOS_CONFIG_MANAGER_KEY)
    public NacosCloudRefresherHandler nacosCloudRefresherHandler(NacosConfigManager nacosConfigManager,
                                                                 ThreadPoolNotifyAlarmHandler threadPoolNotifyAlarmHandler,
                                                                 ConfigParser configParser,
                                                                 BootstrapCoreProperties bootstrapCoreProperties) {
        return new NacosCloudRefresherHandler(nacosConfigManager, threadPoolNotifyAlarmHandler, configParser, bootstrapCoreProperties);
    }

    @Bean
    public ConfigParser configParserHandler() {
        // return new ConfigParserHandler();
        String configFileType = bootstrapCoreProperties.getConfigFileType();
        if (yamlList.contains(configFileType)) {
            return new YmlConfigParser();
        }
        if (propList.contains(configFileType)) {
            return new PropConfigParser();
        }

        throw new UnsupportedOperationException("暂不支持的配置文件类型: " + configFileType);
    }

}
