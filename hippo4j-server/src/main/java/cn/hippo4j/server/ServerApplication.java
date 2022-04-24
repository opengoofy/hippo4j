package cn.hippo4j.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Example application.
 *
 * @author chen.ma
 * @date 2022/01/23 21:06
 */
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = "cn.hippo4j")
@MapperScan(basePackages = {"cn.hippo4j.config.mapper", "cn.hippo4j.auth.mapper"})
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
