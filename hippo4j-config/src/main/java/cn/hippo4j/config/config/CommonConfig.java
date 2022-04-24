package cn.hippo4j.config.config;

import cn.hippo4j.common.config.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static cn.hippo4j.common.constant.Constants.AVAILABLE_PROCESSORS;

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

    @Bean
    @Primary
    public ThreadPoolTaskExecutor monitorThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor monitorThreadPool = new ThreadPoolTaskExecutor();
        monitorThreadPool.setThreadNamePrefix("server.monitor.executor.");
        monitorThreadPool.setCorePoolSize(AVAILABLE_PROCESSORS);
        monitorThreadPool.setMaxPoolSize(AVAILABLE_PROCESSORS << 1);
        monitorThreadPool.setQueueCapacity(4096);
        monitorThreadPool.setAllowCoreThreadTimeOut(true);
        monitorThreadPool.setAwaitTerminationMillis(5000);
        return monitorThreadPool;
    }

}
