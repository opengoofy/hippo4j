package cn.hippo4j.core.starter.config;

import cn.hippo4j.core.config.BootstrapPropertiesInterface;
import cn.hippo4j.core.starter.monitor.DynamicThreadPoolMonitor;
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
     * Collect thread pool runtime indicators.
     */
    private Boolean collect = Boolean.TRUE;

    /**
     * Type of collection thread pool running data. eg: log,metric. Multiple can be used at the same time.
     * Custom SPI support {@link DynamicThreadPoolMonitor}.
     */
    private String collectType;

    /**
     * Delay starting data acquisition task. unit: ms
     */
    private Long initialDelay = 10000L;

    /**
     * Collect interval. unit: ms
     */
    private Long collectInterval = 5000L;

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
     * Whether to enable thread pool running alarm.
     */
    private Boolean alarm = Boolean.TRUE;

    /**
     * Check thread pool running status interval.
     */
    private Integer checkStateInterval;

    /**
     * Active alarm.
     */
    private Integer activeAlarm;

    /**
     * Capacity alarm.
     */
    private Integer capacityAlarm;

    /**
     * Thread pool run alarm interval. unit: s
     */
    private Integer alarmInterval;

    /**
     * Receive.
     */
    private String receive;

    /**
     * Executors.
     */
    private List<ExecutorProperties> executors;

}
