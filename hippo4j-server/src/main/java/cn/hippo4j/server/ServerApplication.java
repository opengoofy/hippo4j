package cn.hippo4j.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages =
        {
                "cn.hippo4j.config.mapper",
                "cn.hippo4j.auth.mapper"
        })
@SpringBootApplication(scanBasePackages =
        {
                "cn.hippo4j"
        })
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
