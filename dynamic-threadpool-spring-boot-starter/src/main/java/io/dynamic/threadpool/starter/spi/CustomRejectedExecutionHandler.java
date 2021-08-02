package io.dynamic.threadpool.starter.spi;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * 自定义拒绝策略
 *
 * @author chen.ma
 * @date 2021/7/10 23:51
 */
public interface CustomRejectedExecutionHandler {

    /**
     * 获取类型
     *
     * @return
     */
    Integer getType();

    /**
     * 生成拒绝策略
     *
     * @return
     */
    RejectedExecutionHandler generateRejected();

}
