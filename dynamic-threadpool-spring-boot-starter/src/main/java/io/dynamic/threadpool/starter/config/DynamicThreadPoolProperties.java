package io.dynamic.threadpool.starter.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 动态线程池配置
 *
 * @author chen.ma
 * @date 2021/6/22 09:14
 */
@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = DynamicThreadPoolProperties.PREFIX)
public class DynamicThreadPoolProperties {

    public static final String PREFIX = "spring.threadpool.dynamic";

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 项目 Id
     */
    private String itemId;

}
