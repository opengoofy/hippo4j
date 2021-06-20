package io.dtp.starter.config;

import io.dtp.starter.core.ThreadPoolRunListener;
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
    public ApplicationContextHolder applicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    public ThreadPoolRunListener threadPoolRunListener() {
        return new ThreadPoolRunListener();
    }
}
