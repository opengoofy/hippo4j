package io.dynamic.threadpool.starter.config;

import io.dynamic.threadpool.common.config.CommonConfiguration;
import io.dynamic.threadpool.starter.core.ThreadPoolConfigAdapter;
import io.dynamic.threadpool.starter.controller.PoolRunStateController;
import io.dynamic.threadpool.starter.core.ConfigService;
import io.dynamic.threadpool.starter.core.ThreadPoolConfigService;
import io.dynamic.threadpool.starter.enable.DynamicThreadPoolMarkerConfiguration;
import io.dynamic.threadpool.starter.listener.ThreadPoolRunListener;
import io.dynamic.threadpool.starter.core.ThreadPoolOperation;
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
@ImportAutoConfiguration({OkHttpClientConfig.class, CommonConfiguration.class})
public class DynamicThreadPoolAutoConfiguration {

    private final DynamicThreadPoolProperties properties;

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
