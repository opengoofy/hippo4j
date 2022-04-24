package cn.hippo4j.core.starter.config;

import lombok.Data;

/**
 * Web thread pool properties.
 *
 * @author chen.ma
 * @date 2022/3/11 19:23
 */
@Data
public class WebThreadPoolProperties {

    /**
     * Core pool size
     */
    private Integer corePoolSize;

    /**
     * Maximum pool size
     */
    private Integer maximumPoolSize;

    /**
     * Keep alive time
     */
    private Integer keepAliveTime;

}
