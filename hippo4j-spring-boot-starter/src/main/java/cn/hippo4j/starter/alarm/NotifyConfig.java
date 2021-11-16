package cn.hippo4j.starter.alarm;

import lombok.Data;

/**
 * Alarm config.
 *
 * @author chen.ma
 * @date 2021/8/15 16:09
 */
@Data
public class NotifyConfig {

    /**
     * type
     */
    private String type;

    /**
     * url
     */
    private String url;

    /**
     * token
     */
    private String token;

    /**
     * receives
     */
    private String receives;

}
