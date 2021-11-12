package com.github.dynamic.threadpool.starter.config;

import com.github.dynamic.threadpool.starter.toolkit.inet.InetUtils;
import com.github.dynamic.threadpool.starter.toolkit.inet.InetUtilsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Util auto configuration.
 *
 * @author Spencer Gibb
 * @date 2021/11/12 21:34
 */
@EnableConfigurationProperties(InetUtilsProperties.class)
public class UtilAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public InetUtils inetUtils(InetUtilsProperties properties) {
        return new InetUtils(properties);
    }

}
