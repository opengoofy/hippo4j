package cn.hippo4j.core.springboot.starter.config;

import lombok.Data;

/**
 * Adapter executor properties.
 */
@Data
public class AdapterExecutorProperties {

    /**
     * Mark
     */
    private String mark;

    /**
     * Thread-pool key
     */
    private String threadPoolKey;

    /**
     * Core pool size
     */
    private Integer corePoolSize;

    /**
     * Maximum pool size
     */
    private Integer maximumPoolSize;
}
