package com.github.dynamic.threadpool.config.config;

import com.github.dynamic.threadpool.common.config.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Common config.
 *
 * @author chen.ma
 * @date 2021/7/19 21:03
 */
@Configuration
public class CommonConfig {

    @Bean
    public ApplicationContextHolder simpleApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

}
