package io.dynamic.threadpool.common.model;

/**
 * Pool Parameter.
 *
 * @author chen.ma
 * @date 2021/6/24 16:04
 */
public interface PoolParameter {

    /**
     * namespace
     *
     * @return
     */
    String getNamespace();

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

}
