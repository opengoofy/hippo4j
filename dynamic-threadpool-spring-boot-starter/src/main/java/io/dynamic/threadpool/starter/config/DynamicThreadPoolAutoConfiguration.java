package io.dynamic.threadpool.starter.config;

import io.dynamic.threadpool.common.config.ApplicationContextHolder;
import io.dynamic.threadpool.starter.controller.PoolRunStateController;
import io.dynamic.threadpool.starter.core.*;
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
@EnableConfigurationProperties(DynamicThreadPoolProperties.class)
@ConditionalOnBean(DynamicThreadPoolMarkerConfiguration.Marker.class)
@ImportAutoConfiguration(OkHttpClientConfig.class)
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
    public ConfigService configService() {
        return new ThreadPoolConfigService(properties);
    }

    @Bean
    public ThreadPoolRunListener threadPoolRunListener() {
        return new ThreadPoolRunListener(properties);
    }

    @Bean
    public ThreadPoolConfigAdapter threadPoolConfigAdapter() {
        return new ThreadPoolConfigAdapter();
    }

    @Bean
    public ThreadPoolOperation threadPoolOperation() {
        return new ThreadPoolOperation(properties);
    }

    @Bean
    public PoolRunStateController poolRunStateController() {
        return new PoolRunStateController();
    }
}
