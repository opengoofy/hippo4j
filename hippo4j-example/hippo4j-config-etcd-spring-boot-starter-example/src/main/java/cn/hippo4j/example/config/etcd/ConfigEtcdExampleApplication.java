package cn.hippo4j.example.config.etcd;

import cn.hippo4j.core.enable.EnableDynamicThreadPool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *@author : wh
 *@date : 2022/9/2 19:06
 *@description:
 */
@EnableDynamicThreadPool
@SpringBootApplication(scanBasePackages = "cn.hippo4j.example.config")
public class ConfigEtcdExampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigEtcdExampleApplication.class, args);
	}
}
