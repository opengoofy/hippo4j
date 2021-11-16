package cn.hippo4j.starter.spi;

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
     * Get custom reject policy.
     *
     * @return
     */
    RejectedExecutionHandler generateRejected();

}
