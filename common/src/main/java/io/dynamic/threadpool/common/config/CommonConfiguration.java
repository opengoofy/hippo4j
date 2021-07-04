package io.dynamic.threadpool.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公共配置
 *
 * @author chen.ma
 * @date 2021/6/20 18:52
 */
@Configuration
public class CommonConfiguration {

    @Bean
    public ApplicationContextHolder simpleApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

}
