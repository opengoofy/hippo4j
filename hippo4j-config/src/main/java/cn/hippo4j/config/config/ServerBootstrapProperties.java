package cn.hippo4j.config.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Server bootstrap properties.
 *
 * @author chen.ma
 * @date 2021/12/22 08:01
 */
@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = ServerBootstrapProperties.PREFIX)
public class ServerBootstrapProperties {

    public final static String PREFIX = "hippo4j.core";

    /**
     * Whether to start the background task of cleaning up thread pool history data.
     */
    private Boolean cleanHistoryDataEnable = Boolean.TRUE;

    /**
     * Regularly clean up the historical running data of thread pool. unit: minute.
     */
    private Integer cleanHistoryDataPeriod = 30;

}
