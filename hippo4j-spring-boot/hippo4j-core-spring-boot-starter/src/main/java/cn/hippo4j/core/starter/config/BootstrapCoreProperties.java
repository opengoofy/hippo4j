package cn.hippo4j.core.starter.config;

import cn.hippo4j.core.config.BootstrapPropertiesInterface;
import cn.hippo4j.core.starter.parser.ConfigFileTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

/**
 * Bootstrap properties.
 *
 * @author chen.ma
 * @date 2022/2/25 00:35
 */
@Getter
@Setter
@ConfigurationProperties(prefix = BootstrapCoreProperties.PREFIX)
public class BootstrapCoreProperties implements BootstrapPropertiesInterface {

    public static final String PREFIX = "spring.dynamic.thread-pool";

    /**
     * Enable dynamic thread pool.
     */
    private Boolean enable = Boolean.TRUE;

    /**
     * Enabled banner.
     */
    private Boolean banner = Boolean.TRUE;

    /***
     * Enabled collect.
     */
    private Boolean collect = Boolean.TRUE;

    /**
     * Check state interval.
     */
    private Integer checkStateInterval;

    /**
     * Config file type.
     */
    private ConfigFileTypeEnum configFileType;

    /**
     * Nacos config.
     */
    private Map<String, String> nacos;

    /**
     * Apollo config.
     */
    private Map<String, String> apollo;

    /**
     * Zookeeper config.
     */
    private Map<String, String> zookeeper;

    /**
     * Tomcat thread pool config.
     */
    private WebThreadPoolProperties tomcat;

    /**
     * Undertow thread pool config.
     */
    private WebThreadPoolProperties undertow;

    /**
     * Jetty thread pool config.
     * KeepAliveTime is not supported temporarily.
     */
    private WebThreadPoolProperties jetty;

    /**
     * Notify platforms.
     */
    private List<NotifyPlatformProperties> notifyPlatforms;

    /**
     * Executors.
     */
    private List<ExecutorProperties> executors;

}
