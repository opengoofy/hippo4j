package cn.hippo4j.example.core.apollo;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDynamicThreadPool
@SpringBootApplication(scanBasePackages = "cn.hippo4j.example.core")
public class Hippo4jCoreApolloExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hippo4jCoreApolloExampleApplication.class, args);
    }

}
