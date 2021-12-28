package cn.hippo4j.starter.config;

import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.toolkit.ContentUtil;
import cn.hippo4j.starter.core.DiscoveryClient;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hippo4j.starter.toolkit.IdentifyUtil;
import cn.hippo4j.starter.toolkit.inet.InetUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;

import static cn.hippo4j.starter.toolkit.CloudCommonIdUtil.getDefaultInstanceId;
import static cn.hippo4j.starter.toolkit.CloudCommonIdUtil.getIpApplicationName;

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

    private final InetUtils hippo4JInetUtils;

    @Bean
    @SneakyThrows
    public InstanceInfo instanceConfig() {
        String namespace = properties.getNamespace();
        String itemId = properties.getItemId();
        String applicationName = environment.getProperty("spring.application.name");

        InstanceInfo instanceInfo = new InstanceInfo();
        instanceInfo.setInstanceId(getDefaultInstanceId(environment, hippo4JInetUtils))
                .setIpApplicationName(getIpApplicationName(environment, hippo4JInetUtils))
                .setHostName(InetAddress.getLocalHost().getHostAddress())
                .setGroupKey(itemId + "+" + namespace)
                .setAppName(applicationName)
                .setClientBasePath(environment.getProperty("server.servlet.context-path"))
                .setGroupKey(ContentUtil.getGroupKey(itemId, namespace));

        String callBackUrl = new StringBuilder().append(instanceInfo.getHostName()).append(":")
                .append(environment.getProperty("server.port")).append(instanceInfo.getClientBasePath())
                .toString();
        instanceInfo.setCallBackUrl(callBackUrl);

        String identify = IdentifyUtil.generate(environment, hippo4JInetUtils);
        instanceInfo.setIdentify(identify);

        return instanceInfo;
    }

    @Bean
    public DiscoveryClient hippo4JDiscoveryClient(HttpAgent httpAgent, InstanceInfo instanceInfo) {
        return new DiscoveryClient(httpAgent, instanceInfo);
    }

}
