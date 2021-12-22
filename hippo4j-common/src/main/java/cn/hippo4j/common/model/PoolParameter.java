package cn.hippo4j.common.model;

/**
 * Pool parameter.
 *
 * @author chen.ma
 * @date 2021/6/24 16:04
 */
public interface PoolParameter {

    /**
     * tenantId
     *
     * @return
     */
    String getTenantId();

    /**
     * itemId
     *
     * @return
     */
    String getItemId();

    /**
     * tpId
     *
     * @return
     */
    String getTpId();

    /**
     * coreSize
     *
     * @return
     */
    Integer getCoreSize();

    /**
     * maxSize
     *
     * @return
     */
    Integer getMaxSize();

    /**
     * queueType
     *
     * @return
     */
    Integer getQueueType();

    /**
     * capacity
     *
     * @return
     */
    Integer getCapacity();

    /**
     * keepAliveTime
     *
     * @return
     */
    Integer getKeepAliveTime();

    /**
     * rejectedType
     *
     * @return
     */
    Integer getRejectedType();

    /**
     * isAlarm
     *
     * @return
     */
    Integer getIsAlarm();

    /**
     * capacityAlarm
     *
     * @return
     */
    Integer getCapacityAlarm();

    /**
     * livenessAlarm
     *
     * @return
     */
    Integer getLivenessAlarm();

    /**
     * allowCoreThreadTimeOut
     *
     * @return
     */
    Integer getAllowCoreThreadTimeOut();

}
