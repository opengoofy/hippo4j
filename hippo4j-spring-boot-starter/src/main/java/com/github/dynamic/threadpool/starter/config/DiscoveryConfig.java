package com.github.dynamic.threadpool.starter.config;

import cn.hutool.core.util.StrUtil;
import com.github.dynamic.threadpool.common.model.InstanceInfo;
import com.github.dynamic.threadpool.common.toolkit.ContentUtil;
import com.github.dynamic.threadpool.starter.core.DiscoveryClient;
import com.github.dynamic.threadpool.starter.remote.HttpAgent;
import com.github.dynamic.threadpool.starter.toolkit.inet.InetUtils;
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

    private final ConfigurableEnvironment environment;

    private final BootstrapProperties properties;

    private final InetUtils inetUtils;

    @Bean
    @SneakyThrows
    public InstanceInfo instanceConfig() {
        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setInstanceId(getDefaultInstanceId(environment))
                .setIpApplicationName(getIpApplicationName(environment))
                .setHostName(InetAddress.getLocalHost().getHostAddress())
                .setGroupKey(properties.getItemId() + "+" + properties.getNamespace())
                .setAppName(environment.getProperty("spring.application.name"))
                .setClientBasePath(environment.getProperty("server.servlet.context-path"))
                .setGroupKey(ContentUtil.getGroupKey(properties.getItemId(), properties.getNamespace()));

        String callBackUrl = new StringBuilder().append(instanceInfo.getHostName()).append(":")
                .append(environment.getProperty("server.port")).append(instanceInfo.getClientBasePath())
                .toString();
        instanceInfo.setCallBackUrl(callBackUrl);

        String ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        String port = environment.getProperty("server.port");
        String identification = StrUtil.builder(ip, ":", port).toString();
        instanceInfo.setIdentify(identification);


        return instanceInfo;
    }

    @Bean
    public DiscoveryClient discoveryClient(HttpAgent httpAgent, InstanceInfo instanceInfo) {
        return new DiscoveryClient(httpAgent, instanceInfo);
    }

}
