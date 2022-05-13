package cn.hippo4j.springboot.starter.adapter.springcloud.stream.rocketmq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring cloud stream rocketMQ auto configuration.
 */
@Configuration
public class SpringCloudStreamRocketMQAutoConfiguration {

    @Bean
    public SpringCloudStreamRocketMQThreadPoolAdapter springCloudStreamRocketMQThreadPoolAdapter() {
        return new SpringCloudStreamRocketMQThreadPoolAdapter();
    }
}
