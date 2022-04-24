package cn.hippo4j.core.starter.config;

import lombok.Data;

/**
 * Notify platform properties.
 *
 * @author chen.ma
 * @date 2022/2/25 19:29
 */
@Data
public class NotifyPlatformProperties {

    /**
     * Platform.
     */
    private String platform;

    /**
     * Secret key.
     */
    private String secretKey;

}
