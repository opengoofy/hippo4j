package com.github.dynamic.threadpool.starter.enable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 动态线程池标记配置类
 *
 * @author chen.ma
 * @date 2021/7/8 23:30
 */
@Configuration(proxyBeanMethods = false)
public class DynamicThreadPoolMarkerConfiguration {

    @Bean
    public Marker dynamicThreadPoolMarkerBean() {
        return new Marker();
    }

    public class Marker {

    }
}
