package cn.hippo4j.config.model.biz.threadpool;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class ConfigChangeSaveReqDTO {

    /**
     * thread pool config change type
     */
    private Integer type;

    /**
     * thread pool instance id
     */
    private String instanceId;

    /**
     * weather modify all instances
     */
    private Integer modifyAll;

    /**
     * TenantId
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tenantId;

    /**
     * Thread-pool id
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String tpId;

    /**
     * Item id
     */
    @Pattern(regexp = "^((?!\\+).)*$", message = "租户、项目、线程池 ID 包含+号")
    private String itemId;

    /**
     * Core size
     */
    private Integer coreSize;

    /**
     * Max size
     */
    private Integer maxSize;

    /**
     * Queue type
     */
    private Integer queueType;

    /**
     * Capacity
     */
    private Integer capacity;

    /**
     * Keep alive time
     */
    private Integer keepAliveTime;

    /**
     * Execute time out
     */
    private Long executeTimeOut;

    /**
     * Is alarm
     */
    private Integer isAlarm;

    /**
     * Capacity alarm
     */
    private Integer capacityAlarm;

    /**
     * Liveness alarm
     */
    private Integer livenessAlarm;

    /**
     * Rejected type
     */
    private Integer rejectedType;

    /**
     * Allow core thread timeout
     */
    private Integer allowCoreThreadTimeOut;
}
