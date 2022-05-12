package cn.hippo4j.adapter.base;

import lombok.Data;

/**
 * Thread pool adapter state info.
 */
@Data
public class ThreadPoolAdapterState {

    /**
     * Core size.
     */
    private Integer coreSize;

    /**
     * Maximum size.
     */
    private Integer maximumSize;
}
