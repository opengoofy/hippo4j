package cn.hippo4j.starter.alarm;

import lombok.Data;

/**
 * 报警通知实体.
 *
 * @author chen.ma
 * @date 2021/11/17 22:12
 */
@Data
public class AlarmNotifyDTO {

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 项目id
     */
    private String itemId;

    /**
     * 线程池id
     */
    private String tpId;

    /**
     * 通知平台
     */
    private String platform;

    /**
     * 密钥
     */
    private String secretKey;

    /**
     * 报警间隔
     */
    private Integer interval;

    /**
     * 接收者
     */
    private String receives;

}
