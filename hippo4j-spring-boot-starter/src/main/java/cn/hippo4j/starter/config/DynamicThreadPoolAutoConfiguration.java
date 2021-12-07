package cn.hippo4j.starter.config;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.starter.controller.PoolRunStateController;
import cn.hippo4j.starter.core.ConfigService;
import cn.hippo4j.starter.core.DynamicThreadPoolPostProcessor;
import cn.hippo4j.starter.core.ThreadPoolConfigService;
import cn.hippo4j.starter.core.ThreadPoolOperation;
import cn.hippo4j.starter.enable.MarkerConfiguration;
import cn.hippo4j.starter.handler.DynamicThreadPoolBannerHandler;
import cn.hippo4j.starter.handler.ThreadPoolRunStateHandler;
import cn.hippo4j.starter.monitor.HttpMvcSender;
import cn.hippo4j.starter.monitor.MessageSender;
import cn.hippo4j.starter.monitor.ReportingEventExecutor;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hippo4j.starter.toolkit.IdentifyUtil;
import cn.hippo4j.starter.toolkit.inet.InetUtils;
import cn.hutool.core.util.IdUtil;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * DynamicTp auto configuration.
 *
 * @author chen.ma
 * @date 2021/6/22 09:20
 */
@Configuration
@AllArgsConstructor
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@EnableConfigurationProperties(BootstrapProperties.class)
@ImportAutoConfiguration({HttpClientConfig.class, DiscoveryConfig.class, MessageAlarmConfig.class, UtilAutoConfiguration.class})
public class DynamicThreadPoolAutoConfiguration {

    private final BootstrapProperties properties;

    private final ConfigurableEnvironment environment;

    public static final String CLIENT_IDENTIFICATION_VALUE = IdUtil.simpleUUID();

    @Bean
    public DynamicThreadPoolBannerHandler threadPoolBannerHandler() {
        return new DynamicThreadPoolBannerHandler(properties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder hippo4JApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @SuppressWarnings("all")
    public ConfigService configService(HttpAgent httpAgent, InetUtils hippo4JInetUtils) {
        String identify = IdentifyUtil.generate(environment, hippo4JInetUtils);
        return new ThreadPoolConfigService(httpAgent, identify);
    }

    @Bean
    public ThreadPoolOperation threadPoolOperation(ConfigService configService) {
        return new ThreadPoolOperation(properties, configService);
    }

    @Bean
    @SuppressWarnings("all")
    public DynamicThreadPoolPostProcessor threadPoolBeanPostProcessor(HttpAgent httpAgent, ThreadPoolOperation threadPoolOperation,
                                                                      ApplicationContextHolder hippo4JApplicationContextHolder) {
        return new DynamicThreadPoolPostProcessor(properties, httpAgent, threadPoolOperation);
    }

    @Bean
    public ThreadPoolRunStateHandler threadPoolRunStateHandler() {
        return new ThreadPoolRunStateHandler();
    }

    @Bean
    public PoolRunStateController poolRunStateController(ThreadPoolRunStateHandler threadPoolRunStateHandler) {
        return new PoolRunStateController(threadPoolRunStateHandler);
    }

    @Bean
    @SuppressWarnings("all")
    public HttpMvcSender httpMvcSender(HttpAgent httpAgent) {
        return new HttpMvcSender(httpAgent);
    }

    @Bean
    public ReportingEventExecutor reportingEventExecutor(BootstrapProperties properties, MessageSender messageSender) {
        return new ReportingEventExecutor(properties, messageSender);
    }

}


