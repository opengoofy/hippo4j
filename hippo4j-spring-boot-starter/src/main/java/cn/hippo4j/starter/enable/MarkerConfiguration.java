package cn.hippo4j.starter.enable;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Marker configuration.
 *
 * @author chen.ma
 * @date 2021/7/8 23:30
 */
@Configuration
public class MarkerConfiguration {

    @Bean
    public Marker dynamicThreadPoolMarkerBean() {
        return new Marker();
    }

    public class Marker {

    }

}
