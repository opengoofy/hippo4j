package com.github.dynamic.threadpool.starter.config;

import com.github.dynamic.threadpool.starter.core.ThreadPoolBeanPostProcessor;
import com.github.dynamic.threadpool.starter.core.ThreadPoolOperation;
import com.github.dynamic.threadpool.starter.enable.DynamicThreadPoolMarkerConfiguration;
import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import com.github.dynamic.threadpool.starter.controller.PoolRunStateController;
import com.github.dynamic.threadpool.starter.core.ConfigService;
import com.github.dynamic.threadpool.starter.core.ThreadPoolConfigService;
import com.github.dynamic.threadpool.starter.handler.ThreadPoolBannerHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

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
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @DependsOn("applicationContextHolder")
    public ConfigService configService() {
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
