package cn.hippo4j.config.model.biz.notify;

import lombok.Data;

/**
 * 消息通知入参实体.
 *
 * @author chen.ma
 * @date 2021/11/18 20:15
 */
@Data
public class NotifyReqDTO {

    /**
     * id
     */
    private String id;

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
     * 是否启用
     */
    private Integer enable;

}
