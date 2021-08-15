package com.github.dynamic.threadpool.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Pool Run State Info.
 *
 * @author chen.ma
 * @date 2021/7/7 18:57
 */
@Getter
@Setter
public class PoolRunStateInfo implements Serializable {

    /**
     * 当前负载
     */
    private String currentLoad;

    /**
     * 峰值负载
     */
    private String peakLoad;

    /**
     * 线程池 ID
     */
    private String tpId;

    /**
     * 核心线程数
     */
    private Integer coreSize;

    /**
     * 最大线程数
     */
    private Integer maximumSize;

    /**
     * 线程池当前线程数
     */
    private Integer poolSize;

    /**
     * 活跃线程数
     */
    private Integer activeSize;

    /**
     * 线程池中同时进入的最大线程数
     */
    private Integer largestPoolSize;

    /**
     * 队列类型
     */
    private String queueType;

    /**
     * 队列容量
     */
    private Integer queueCapacity;

    /**
     * 队列元素个数
     */
    private Integer queueSize;

    /**
     * 队列剩余容量
     */
    private Integer queueRemainingCapacity;

    /**
     * 线程池中执行任务总数量
     */
    private Long completedTaskCount;

    /**
     * 拒绝策略发生次数
     */
    private Integer rejectCount;

    /**
     * Host
     */
    private String host;

}
