package cn.hippo4j.config.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * His run data info.
 *
 * @author chen.ma
 * @date 2021/12/10 21:30
 */
@Data
@TableName("his_run_data")
public class HisRunDataInfo {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 线程池id
     */
    private String tpId;

    /**
     * 当前负载
     */
    private Long currentLoad;

    /**
     * 峰值负载
     */
    private Long peakLoad;

    /**
     * 线程数
     */
    private Long poolSize;

    /**
     * 活跃线程数
     */
    private Long activeSize;

    /**
     * 队列容量
     */
    private Long queueCapacity;

    /**
     * 队列元素
     */
    private Long queueSize;

    /**
     * 队列剩余容量
     */
    private Long queueRemainingCapacity;

    /**
     * 已完成任务计数
     */
    private Long completedTaskCount;

    /**
     * 拒绝次数
     */
    private Long rejectCount;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date gmtCreate;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;

}
