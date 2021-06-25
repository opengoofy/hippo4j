package io.dynamic.threadpool.example.config;

import io.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池配置
 *
 * @author chen.ma
 * @date 2021/6/20 17:16
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public DynamicThreadPoolWrap messageCenterConsumeThreadPool() {
        return new DynamicThreadPoolWrap("message-consume");
    }

    @Bean
    public DynamicThreadPoolWrap messageCenterProduceThreadPool() {
        return new DynamicThreadPoolWrap("message-produce");
    }

}
