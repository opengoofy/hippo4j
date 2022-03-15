package cn.hippo4j.example.core.zookeeper;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Redick01
 * @date 2022/3/14 20:40
 */
@EnableDynamicThreadPool
@SpringBootApplication(scanBasePackages = "cn.hippo4j.example.core")
public class Hippo4jCoreZookeeperExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hippo4jCoreZookeeperExampleApplication.class, args);
    }
}
