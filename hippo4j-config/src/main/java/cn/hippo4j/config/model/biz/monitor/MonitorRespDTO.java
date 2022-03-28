package cn.hippo4j.config.model.biz.monitor;

import lombok.Data;

/**
 * Monitor resp dto.
 *
 * @author chen.ma
 * @date 2021/12/10 20:23
 */
@Data
public class MonitorRespDTO {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 项目id
     */
    private String itemId;

    /**
     * 实例id
     */
    private String instanceId;

    /**
     * 已完成任务计数
     */
    private String completedTaskCount;

    /**
     * 线程池id
     */
    private String tpId;

    /**
     * 当前负载
     */
    private String currentLoad;

    /**
     * 峰值负载
     */
    private String peakLoad;

    /**
     * 线程数
     */
    private String poolSize;

    /**
     * 活跃线程数
     */
    private String activeSize;

    /**
     * 队列容量
     */
    private String queueCapacity;

    /**
     * 队列元素
     */
    private String queueSize;

    /**
     * 队列剩余容量
     */
    private String queueRemainingCapacity;

    /**
     * 拒绝次数
     */
    private String rejectCount;

}
