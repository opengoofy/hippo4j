package com.github.dynamic.threadpool.common.model;

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
public class PoolRunStateInfo implements Serializable {

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
     * coreSize
     */
    private Integer coreSize;

    /**
     * maximumSize
     */
    private Integer maximumSize;

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
     * queueType
     */
    private String queueType;

    /**
     * queueCapacity
     */
    private Integer queueCapacity;

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

}
