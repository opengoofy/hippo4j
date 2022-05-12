package cn.hippo4j.adapter.base;

import lombok.Data;

/**
 * Thread pool adapter parameter info.
 */
@Data
public class ThreadPoolAdapterParameter {

    /**
     * Core size.
     */
    private Integer coreSize;

    /**
     * Maximum size.
     */
    private Integer maximumSize;
}
