package com.github.dynamic.threadpool.example.config;

import com.github.dynamic.threadpool.starter.wrap.DynamicThreadPoolWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Thread pool config.
 *
 * @author chen.ma
 * @date 2021/6/20 17:16
 */
@Slf4j
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

    @Bean
    public DynamicThreadPoolWrap customPool() {
        return new DynamicThreadPoolWrap("custom-pool");
    }

}
