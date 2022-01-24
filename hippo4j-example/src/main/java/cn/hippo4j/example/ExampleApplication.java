package cn.hippo4j.example;

import cn.hippo4j.starter.enable.EnableDynamicThreadPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Example application.
 *
 * @author chen.ma
 * @date 2022/01/23 21:06
 */
@SpringBootApplication
@EnableDynamicThreadPool
public class ExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }

}
