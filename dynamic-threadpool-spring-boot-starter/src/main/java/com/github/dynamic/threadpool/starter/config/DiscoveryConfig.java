package com.github.dynamic.threadpool.starter.config;

import com.github.dynamic.threadpool.common.model.InstanceInfo;
import com.github.dynamic.threadpool.starter.core.DiscoveryClient;
import com.github.dynamic.threadpool.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;

import static com.github.dynamic.threadpool.starter.toolkit.CloudCommonIdUtil.getDefaultInstanceId;
import static com.github.dynamic.threadpool.starter.toolkit.CloudCommonIdUtil.getIpApplicationName;

/**
 * Dynamic threadPool discovery config.
 *
 * @author chen.ma
 * @date 2021/8/6 21:35
 */
@AllArgsConstructor
public class DiscoveryConfig {

    private ConfigurableEnvironment environment;

    @Bean
    @SneakyThrows
    public InstanceInfo instanceConfig() {
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setInstanceId(getDefaultInstanceId(environment));
        instanceInfo.setIpApplicationName(getIpApplicationName(environment));
        instanceInfo.setAppName(environment.getProperty("spring.application.name"));
        instanceInfo.setHostName(InetAddress.getLocalHost().getHostAddress());

        return instanceInfo;
    }

    @Bean
    public DiscoveryClient discoveryClient(HttpAgent httpAgent, InstanceInfo instanceInfo) {
        return new DiscoveryClient(httpAgent, instanceInfo);
    }

}
