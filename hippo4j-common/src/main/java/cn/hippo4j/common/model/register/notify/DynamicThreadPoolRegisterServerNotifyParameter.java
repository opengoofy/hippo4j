package cn.hippo4j.common.model.register.notify;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dynamic thread-pool register server notify parameter.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicThreadPoolRegisterServerNotifyParameter {

    /**
     * Thread-pool id
     */
    private String threadPoolId;

    /**
     * Platform
     */
    private String platform;

    /**
     * Config type
     */
    private String type;

    /**
     * Secret key
     */
    private String secretKey;

    /**
     * Interval
     */
    private Integer interval;

    /**
     * Receives
     */
    private String receives;
}
