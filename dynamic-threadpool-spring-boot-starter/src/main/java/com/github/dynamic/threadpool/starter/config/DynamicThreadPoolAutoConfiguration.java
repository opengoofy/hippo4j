package com.github.dynamic.threadpool.starter.config;

import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import com.github.dynamic.threadpool.starter.controller.PoolRunStateController;
import com.github.dynamic.threadpool.starter.core.ConfigService;
import com.github.dynamic.threadpool.starter.core.DynamicThreadPoolPostProcessor;
import com.github.dynamic.threadpool.starter.core.ThreadPoolConfigService;
import com.github.dynamic.threadpool.starter.core.ThreadPoolOperation;
import com.github.dynamic.threadpool.starter.enable.MarkerConfiguration;
import com.github.dynamic.threadpool.starter.handler.DynamicThreadPoolBannerHandler;
import com.github.dynamic.threadpool.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * DynamicTp Auto Configuration.
 *
 * @author chen.ma
 * @date 2021/6/22 09:20
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(BootstrapProperties.class)
@ConditionalOnBean(MarkerConfiguration.Marker.class)
@ImportAutoConfiguration({HttpClientConfig.class, DiscoveryConfig.class})
public class DynamicThreadPoolAutoConfiguration {

    private final BootstrapProperties properties;

    @Bean
    public DynamicThreadPoolBannerHandler threadPoolBannerHandler() {
        return new DynamicThreadPoolBannerHandler(properties);
    }

    @Bean
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @SuppressWarnings("all")
    public ConfigService configService(HttpAgent httpAgent) {
        return new ThreadPoolConfigService(httpAgent);
    }

    @Bean
    public ThreadPoolOperation threadPoolOperation(ConfigService configService) {
        return new ThreadPoolOperation(properties, configService);
    }

    @Bean
    @SuppressWarnings("all")
    public DynamicThreadPoolPostProcessor threadPoolBeanPostProcessor(HttpAgent httpAgent, ThreadPoolOperation threadPoolOperation) {
        return new DynamicThreadPoolPostProcessor(properties, httpAgent, threadPoolOperation);
    }

    @Bean
    public PoolRunStateController poolRunStateController() {
        return new PoolRunStateController();
    }

}


