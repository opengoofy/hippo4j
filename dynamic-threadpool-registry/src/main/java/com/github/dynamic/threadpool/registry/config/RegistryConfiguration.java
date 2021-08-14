package com.github.dynamic.threadpool.registry.config;

import com.github.dynamic.threadpool.registry.core.BaseInstanceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Registry Configuration.
 *
 * @author chen.ma
 * @date 2021/8/12 21:48
 */
@Configuration
public class RegistryConfiguration {

    @Autowired
    private BaseInstanceRegistry baseInstanceRegistry;

    @PostConstruct
    public void registryInit() {
        baseInstanceRegistry.postInit();
    }

}
