package io.dynamic.threadpool.starter.config;

import io.dynamic.threadpool.common.config.ApplicationContextHolder;
import io.dynamic.threadpool.starter.controller.PoolRunStateController;
import io.dynamic.threadpool.starter.core.ConfigService;
import io.dynamic.threadpool.starter.core.ThreadPoolBeanPostProcessor;
import io.dynamic.threadpool.starter.core.ThreadPoolConfigService;
import io.dynamic.threadpool.starter.core.ThreadPoolOperation;
import io.dynamic.threadpool.starter.enable.DynamicThreadPoolMarkerConfiguration;
import io.dynamic.threadpool.starter.handler.ThreadPoolBannerHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态线程池自动装配类
 *
 * @author chen.ma
 * @date 2021/6/22 09:20
 */
@Slf4j
@Configuration
@AllArgsConstructor
@ImportAutoConfiguration(OkHttpClientConfig.class)
@EnableConfigurationProperties(DynamicThreadPoolProperties.class)
@ConditionalOnBean(DynamicThreadPoolMarkerConfiguration.Marker.class)
public class DynamicThreadPoolAutoConfiguration {

    private final DynamicThreadPoolProperties properties;

    @Bean
    public ThreadPoolBannerHandler threadPoolBannerHandler() {
        return new ThreadPoolBannerHandler(properties);
    }

    @Bean
    public ApplicationContextHolder simpleApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public ConfigService configService(ApplicationContextHolder holder) {
        return new ThreadPoolConfigService(properties);
    }

    @Bean
    public ThreadPoolOperation threadPoolOperation(ConfigService configService) {
        return new ThreadPoolOperation(properties, configService);
    }

    @Bean
    public ThreadPoolBeanPostProcessor threadPoolBeanPostProcessor(ThreadPoolOperation threadPoolOperation) {
        return new ThreadPoolBeanPostProcessor(properties, threadPoolOperation);
    }

    @Bean
    public PoolRunStateController poolRunStateController() {
        return new PoolRunStateController();
    }

}
