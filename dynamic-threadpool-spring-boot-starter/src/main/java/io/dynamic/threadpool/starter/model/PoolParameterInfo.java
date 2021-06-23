package io.dynamic.threadpool.starter.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 线程池参数
 *
 * @author chen.ma
 * @date 2021/6/16 23:18
 */
@Data
public class PoolParameterInfo implements Serializable {

    private static final long serialVersionUID = -7123935122108553864L;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 项目 Id
     */
    private String itemId;

    /**
     * 线程池 Id
     */
    private String tpId;

    /**
     * 内容
     */
    private String content;

    /**
     * 核心线程数
     */
    private Integer coreSize;

    /**
     * 最大线程数
     */
    private Integer maxSize;

    /**
     * 队列类型
     */
    private Integer queueType;

    /**
     * 队列长度
     */
    private Integer capacity;

    /**
     * 线程存活时长
     */
    private Integer keepAliveTime;
}
