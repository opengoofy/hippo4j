package cn.hippo4j.starter.alarm;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 通知实体.
 *
 * @author chen.ma
 * @date 2021/11/17 22:12
 */
@Data
@Accessors(chain = true)
public class NotifyDTO {

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
     * 通知类型
     */
    private String type;

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

    /**
     * 报警类型
     */
    private MessageTypeEnum typeEnum;

}
