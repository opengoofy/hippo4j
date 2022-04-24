package cn.hippo4j.common.model;

import lombok.Data;

/**
 * Many pool run state info.
 *
 * @author chen.ma
 * @date 2022/1/8 12:54
 */
@Data
public class ManyPoolRunStateInfo extends PoolRunStateInfo {

    /**
     * identify
     */
    private String identify;

    /**
     * active
     */
    private String active;

    /**
     * state
     */
    private String state;

}
