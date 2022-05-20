package cn.hippo4j.console.model;

import lombok.Data;

/**
 * Web thread-pool resp dto.
 */
@Data
public class WebThreadPoolRespDTO {

    /**
     * Active
     */
    private String active;

    /**
     * Identify
     */
    private String identify;

    /**
     * Client address
     */
    private String clientAddress;

    /**
     * Core size
     */
    private Integer coreSize;

    /**
     * Maximum size
     */
    private Integer maximumSize;

    /**
     * Queue type
     */
    private String queueType;

    /**
     * Queue capacity
     */
    private Integer queueCapacity;

    /**
     * Rejected name
     */
    private String rejectedName;

    /**
     * Keep alive time
     */
    private Long keepAliveTime;
}
