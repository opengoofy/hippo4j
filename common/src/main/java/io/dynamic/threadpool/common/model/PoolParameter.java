package io.dynamic.threadpool.common.model;

/**
 * Pool Parameter.
 *
 * @author chen.ma
 * @date 2021/6/24 16:04
 */
public interface PoolParameter {

    String getNamespace();

    String getItemId();

    String getTpId();

    Integer getCoreSize();

    Integer getMaxSize();

    Integer getQueueType();

    Integer getCapacity();

    Integer getKeepAliveTime();

    Integer getIsAlarm();

    Integer getCapacityAlarm();

    Integer getLivenessAlarm();

}
