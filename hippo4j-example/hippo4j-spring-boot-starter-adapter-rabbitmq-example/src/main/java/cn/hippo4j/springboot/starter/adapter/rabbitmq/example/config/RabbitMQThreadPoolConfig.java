package cn.hippo4j.springboot.starter.adapter.rabbitmq.example.config;

import org.springframework.amqp.rabbit.config.AbstractRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author : wh
 * @date : 2022/5/24 10:02
 * @description:
 */
@Configuration
public class RabbitMQThreadPoolConfig {

    
    @Bean
    public ThreadPoolTaskExecutor rabbitListenerTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(10); // 指定线程的最大数量
        executor.setCorePoolSize(2); // 指定线程池维护线程的最少数量
        executor.setQueueCapacity(20); // 指定等待处理的任务数
        executor.setThreadNamePrefix("RabbitListenerTaskExecutor-");
        return executor;
    }

    @Bean
    public AbstractRabbitListenerContainerFactory<?> defaultRabbitListenerContainerFactory (
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ThreadPoolTaskExecutor rabbitListenerTaskExecutor,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(10);
        factory.setTaskExecutor(rabbitListenerTaskExecutor);
        return factory;
    }


}