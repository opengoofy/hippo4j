package cn.hippo4j.config.model.biz.threadpool;

import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * Thread pool save or update req dto.
 *
 * @author chen.ma
 * @date 2021/6/30 21:23
 */
@Data
public class ThreadPoolSaveOrUpdateReqDTO {

    /**
     * tenantId
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tenantId;

    /**
     * TpId
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tpId;

    /**
     * ItemId
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String itemId;

    /**
     * coreSize
     */
    private Integer coreSize;

    /**
     * maxSize
     */
    private Integer maxSize;

    /**
     * queueType
     */
    private Integer queueType;

    /**
     * capacity
     */
    private Integer capacity;

    /**
     * keepAliveTime
     */
    private Integer keepAliveTime;

    /**
     * isAlarm
     */
    private Integer isAlarm;

    /**
     * capacityAlarm
     */
    private Integer capacityAlarm;

    /**
     * livenessAlarm
     */
    private Integer livenessAlarm;

    /**
     * rejectedType
     */
    private Integer rejectedType;

    /**
     * allowCoreThreadTimeOut
     */
    private Integer allowCoreThreadTimeOut;

}
