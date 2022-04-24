package cn.hippo4j.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Pool base info.
 *
 * @author chen.ma
 * @date 2022/1/22 12:10
 */
@Data
@Accessors(chain = true)
public class PoolBaseInfo {

    /**
     * coreSize
     */
    private Integer coreSize;

    /**
     * maximumSize
     */
    private Integer maximumSize;

    /**
     * queueType
     */
    private String queueType;

    /**
     * queueCapacity
     */
    private Integer queueCapacity;

    /**
     * rejectedName
     */
    private String rejectedName;

    /**
     * keepAliveTime
     */
    private Long keepAliveTime;

}
