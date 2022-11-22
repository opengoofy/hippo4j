package cn.hippo4j.example.config.consul.controller;

import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RestController
public class TestController {

    @Resource
    private ThreadPoolExecutor messageConsumeDynamicExecutor;

    @GetMapping("/hippo4j-consul/test")
    public int test() {
        int maximumPoolSize = messageConsumeDynamicExecutor.getMaximumPoolSize();
        int corePoolSize = messageConsumeDynamicExecutor.getCorePoolSize();
        log.info("corePoolSize: {}, maximumPoolSize: {}", corePoolSize, maximumPoolSize);
        return corePoolSize + maximumPoolSize;
    }
}
