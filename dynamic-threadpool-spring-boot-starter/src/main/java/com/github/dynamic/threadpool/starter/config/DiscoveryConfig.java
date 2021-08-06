package com.github.dynamic.threadpool.starter.config;

import com.github.dynamic.threadpool.starter.core.DiscoveryClient;
import com.github.dynamic.threadpool.starter.core.InstanceConfig;
import com.github.dynamic.threadpool.starter.core.InstanceInfo;
import com.github.dynamic.threadpool.starter.remote.HttpAgent;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import static com.github.dynamic.threadpool.starter.toolkit.CloudCommonIdUtil.getDefaultInstanceId;

/**
 * Dynamic Tp Discovery Config.
 *
 * @author chen.ma
 * @date 2021/8/6 21:35
 */
@AllArgsConstructor
public class DiscoveryConfig {

    private ConfigurableEnvironment environment;

    @Bean
    public InstanceConfig instanceConfig() {
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setInstanceId(getDefaultInstanceId(environment));

        String hostNameKey = "eureka.instance.hostname";
        String hostNameVal = environment.containsProperty(hostNameKey) ? environment.getProperty(hostNameKey) : "";
        instanceInfo.setHostName(hostNameVal);

        return instanceInfo;
    }

    @Bean
    public DiscoveryClient discoveryClient(HttpAgent httpAgent, InstanceConfig instanceConfig) {
        return new DiscoveryClient(httpAgent, instanceConfig);
    }

}
