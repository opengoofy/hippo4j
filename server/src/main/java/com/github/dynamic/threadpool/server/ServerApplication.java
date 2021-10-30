package com.github.dynamic.threadpool.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages =
        {
                "com.github.dynamic.threadpool.config.mapper",
                "com.github.dynamic.threadpool.auth.mapper"
        })
@SpringBootApplication(scanBasePackages =
        {
                "com.github.dynamic.threadpool"
        })
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
