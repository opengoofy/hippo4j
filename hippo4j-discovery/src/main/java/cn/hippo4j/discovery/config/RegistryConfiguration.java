package cn.hippo4j.discovery.config;

import cn.hippo4j.discovery.core.BaseInstanceRegistry;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Registry configuration.
 *
 * @author chen.ma
 * @date 2021/8/12 21:48
 */
@Configuration
@AllArgsConstructor
public class RegistryConfiguration {

    private final BaseInstanceRegistry baseInstanceRegistry;

    @PostConstruct
    public void registryInit() {
        baseInstanceRegistry.postInit();
    }

}
