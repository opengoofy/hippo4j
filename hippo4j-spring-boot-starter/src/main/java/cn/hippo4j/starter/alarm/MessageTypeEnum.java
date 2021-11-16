package cn.hippo4j.starter.alarm;

/**
 * Message type enum.
 *
 * @author chen.ma
 * @date 2021/8/16 20:50
 */
public enum MessageTypeEnum {

    /**
     * 通知类型
     */
    CHANGE,

    /**
     * 容量报警
     */
    CAPACITY,

    /**
     * 活跃度报警
     */
    LIVENESS,

    /**
     * 拒绝策略报警
     */
    REJECT

}
