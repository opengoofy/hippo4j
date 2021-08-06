package com.github.dynamic.threadpool.starter.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bootstrap Properties.
 *
 * @author chen.ma
 * @date 2021/6/22 09:14
 */
@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = BootstrapProperties.PREFIX)
public class BootstrapProperties {

    public static final String PREFIX = "spring.dynamic.thread-pool";

    /**
     * serverAddr
     */
    private String serverAddr;

    /**
     * namespace
     */
    private String namespace;

    /**
     * itemId
     */
    private String itemId;

    /**
     * Enable banner
     */
    private boolean banner = true;

}
