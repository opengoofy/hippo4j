package com.github.dynamic.threadpool.starter.config;

import com.github.dynamic.threadpool.starter.alarm.AlarmConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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

    /**
     * alarms
     */
    private List<AlarmConfig> alarms;

}
