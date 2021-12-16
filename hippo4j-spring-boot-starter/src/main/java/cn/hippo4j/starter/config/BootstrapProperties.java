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
     * Print dynamic thread pool banner
     */
    private boolean banner = true;

    /**
     * Time interval for client to collect monitoring data. unit: ms
     */
    private Long collectInterval = 5000L;

}
