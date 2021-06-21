package io.dynamic.threadpool.example.config;

import io.dtp.starter.wrap.DynamicThreadPoolWrap;
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
        return new DynamicThreadPoolWrap("common", "message", "message-consume");
    }

}
