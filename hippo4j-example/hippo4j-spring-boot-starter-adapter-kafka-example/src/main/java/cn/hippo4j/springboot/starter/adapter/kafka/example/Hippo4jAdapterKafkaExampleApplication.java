package cn.hippo4j.springboot.starter.adapter.kafka.example;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDynamicThreadPool
public class Hippo4jAdapterKafkaExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hippo4jAdapterKafkaExampleApplication.class, args);
    }
}
