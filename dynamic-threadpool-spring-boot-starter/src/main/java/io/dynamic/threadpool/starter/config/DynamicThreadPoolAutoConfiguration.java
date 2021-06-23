package io.dynamic.threadpool.starter.config;

import io.dynamic.threadpool.starter.adapter.ThreadPoolConfigAdapter;
import io.dynamic.threadpool.starter.core.ConfigService;
import io.dynamic.threadpool.starter.core.ThreadPoolConfigService;
import io.dynamic.threadpool.starter.listener.ThreadPoolRunListener;
import io.dynamic.threadpool.starter.operation.ThreadPoolOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = DynamicThreadPoolProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
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
}
