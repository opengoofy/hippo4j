package cn.hippo4j.core.config;

import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.core.toolkit.inet.InetUtilsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Util auto configuration.
 *
 * @author Spencer Gibb
 * @date 2021/11/12 21:34
 */
@EnableConfigurationProperties(InetUtilsProperties.class)
public class UtilAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public InetUtils hippo4JInetUtils(InetUtilsProperties properties) {
        return new InetUtils(properties);
    }

}
