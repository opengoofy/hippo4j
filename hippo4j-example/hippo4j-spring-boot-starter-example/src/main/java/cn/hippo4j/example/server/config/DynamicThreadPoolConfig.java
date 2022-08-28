package cn.hippo4j.example.server.config;

import cn.hippo4j.core.executor.DynamicThreadPool;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class DynamicThreadPoolConfig {

    @Bean(name = "messagePool")
    @DynamicThreadPool
    public ThreadPoolExecutor messageConsumerDynamicExecutor(){
        String poolId = "message-pool";
        ThreadPoolExecutor messagePool = ThreadPoolBuilder.builder()
                .threadPoolId(poolId)
                .threadFactory(poolId)
                .dynamicPool()
                .build();
        return messagePool;
    }

    @Bean(name = "goodsPool")
    @DynamicThreadPool
    public ThreadPoolExecutor goodsDynamicExecutor(){
        String poolId = "goods-pool";
        ThreadPoolExecutor goodsPool = ThreadPoolBuilder.builder()
                .threadPoolId(poolId)
                .threadFactory(poolId)
                .dynamicPool()
                .build();
        return goodsPool;
    }
}
