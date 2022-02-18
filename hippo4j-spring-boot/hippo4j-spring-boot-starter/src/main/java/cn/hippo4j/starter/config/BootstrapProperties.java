package cn.hippo4j.starter.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bootstrap properties.
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
     * Username.
     */
    private String username;

    /**
     * Password.
     */
    private String password;

    /**
     * Server addr
     */
    private String serverAddr;

    /**
     * Namespace
     */
    private String namespace;

    /**
     * Item id
     */
    private String itemId;

    /**
     * Whether to enable dynamic thread pool
     */
    private Boolean enable = true;

    /**
     * Print dynamic thread pool banner
     */
    private Boolean banner = true;

    /**
     * Enable client data collect
     */
    private Boolean enableCollect = true;

    /**
     * Task buffer container capacity
     */
    private Integer taskBufferSize = 4096;

    /**
     * Delay starting data acquisition task. unit: ms
     */
    private Long initialDelay = 10000L;

    /**
     * Time interval for client to collect monitoring data. unit: ms
     */
    private Long collectInterval = 5000L;

    /**
     * JSON serialization type.
     */
    private String jsonSerializeType = "JACKSON";

}
