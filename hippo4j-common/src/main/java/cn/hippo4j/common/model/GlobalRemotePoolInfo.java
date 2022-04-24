package cn.hippo4j.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Global remote pool info.
 *
 * @author chen.ma
 * @date 2021/6/23 21:08
 */
@Getter
@Setter
public class GlobalRemotePoolInfo implements Serializable {

    private static final long serialVersionUID = 5447003335557127308L;

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String itemId;

    /**
     * tpId
     */
    private String tpId;

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
     * md5
     */
    private String md5;

    /**
     * content
     */
    private String content;

}
