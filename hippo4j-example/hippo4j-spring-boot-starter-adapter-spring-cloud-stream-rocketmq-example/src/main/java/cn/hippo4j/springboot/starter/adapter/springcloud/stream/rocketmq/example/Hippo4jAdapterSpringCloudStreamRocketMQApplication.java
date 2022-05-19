package cn.hippo4j.springboot.starter.adapter.springcloud.stream.rocketmq.example;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

@Slf4j
@EnableDynamicThreadPool
@EnableBinding({Source.class, MySink.class})
@SpringBootApplication(scanBasePackages = "cn.hippo4j.example.core")
public class Hippo4jAdapterSpringCloudStreamRocketMQApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hippo4jAdapterSpringCloudStreamRocketMQApplication.class, args);
    }
}
