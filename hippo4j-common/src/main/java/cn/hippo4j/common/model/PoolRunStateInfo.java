package cn.hippo4j.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Pool run state info.
 *
 * @author chen.ma
 * @date 2021/7/7 18:57
 */
@Getter
@Setter
public class PoolRunStateInfo extends PoolBaseInfo implements Serializable {

    /**
     * currentLoad
     */
    private String currentLoad;

    /**
     * peakLoad
     */
    private String peakLoad;

    /**
     * tpId
     */
    private String tpId;

    /**
     * activeCount
     */
    private Integer activeCount;

    /**
     * poolSize
     */
    private Integer poolSize;

    /**
     * activeSize
     */
    private Integer activeSize;

    /**
     * The maximum number of threads that enter the thread pool at the same time
     */
    private Integer largestPoolSize;

    /**
     * queueSize
     */
    private Integer queueSize;

    /**
     * queueRemainingCapacity
     */
    private Integer queueRemainingCapacity;

    /**
     * completedTaskCount
     */
    private Long completedTaskCount;

    /**
     * rejectCount
     */
    private Integer rejectCount;

    /**
     * host
     */
    private String host;

    /**
     * memoryProportion
     */
    private String memoryProportion;

    /**
     * freeMemory
     */
    private String freeMemory;

    /**
     * clientLastRefreshTime
     */
    private String clientLastRefreshTime;

    /**
     * timestamp
     */
    private Long timestamp;

}
