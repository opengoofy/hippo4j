package cn.hippo4j.core.starter.config;

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
public class BootstrapCoreProperties {

    public static final String PREFIX = "spring.dynamic.thread-pool";

    /**
     * Enabled banner.
     */
    private Boolean enableBanner;

    /***
     * Enabled collect.
     */
    private Boolean enabledCollect;

    /**
     * Check state interval.
     */
    private String checkStateInterval;

    /**
     * Config file type.
     */
    private String configFileType;

    /**
     * Nacos config.
     */
    private Map<String, String> nacos;

    /**
     * Notify platforms.
     */
    private List<NotifyPlatformProperties> notifyPlatforms;

    /**
     * Executors.
     */
    private List<ExecutorProperties> executors;

}
