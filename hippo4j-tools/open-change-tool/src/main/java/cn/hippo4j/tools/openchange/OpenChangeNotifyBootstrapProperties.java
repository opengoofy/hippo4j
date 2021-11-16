package cn.hippo4j.tools.openchange;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Bootstrap properties.
 *
 * @author chen.ma
 * @date 2021/6/22 09:14
 */
@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = OpenChangeNotifyBootstrapProperties.PREFIX)
public class OpenChangeNotifyBootstrapProperties {

    public static final String PREFIX = "spring.dynamic.thread-pool";

    /**
     * notifyInterval
     */
    private Long notifyInterval;

    /**
     * notifys
     */
    private List<NotifyConfig> notifys;

    @Data
    public static class NotifyConfig {

        /**
         * type
         */
        private String type;

        /**
         * url
         */
        private String url;

        /**
         * token
         */
        private String token;

    }

}
