package cn.hippo4j.core.spi;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * Custom rejectedExecution handler.
 *
 * @author chen.ma
 * @date 2021/7/10 23:51
 */
public interface CustomRejectedExecutionHandler {

    /**
     * Get custom reject policy type.
     *
     * @return
     */
    Integer getType();

    /**
     * Adapt hippo4j core rejected execution handler.
     *
     * @return
     */
    default String getName() {
        return "";
    }

    /**
     * Get custom reject policy.
     *
     * @return
     */
    RejectedExecutionHandler generateRejected();

}
