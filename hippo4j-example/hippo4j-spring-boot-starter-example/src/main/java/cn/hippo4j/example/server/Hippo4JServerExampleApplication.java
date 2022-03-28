package cn.hippo4j.example.server;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDynamicThreadPool
@SpringBootApplication(scanBasePackages = "cn.hippo4j.example.core")
public class Hippo4JServerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hippo4JServerExampleApplication.class, args);
    }

}
