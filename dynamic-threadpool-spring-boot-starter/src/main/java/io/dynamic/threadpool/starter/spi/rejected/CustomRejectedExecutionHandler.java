package io.dynamic.threadpool.starter.spi.rejected;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * 自定义拒绝策略
 *
 * @author chen.ma
 * @date 2021/7/10 23:51
 */
public interface CustomRejectedExecutionHandler {

    /**
     * 生成拒绝策略
     *
     * @return
     */
    RejectedExecutionHandlerWrap generateRejected();

    @Getter
    @Setter
    @AllArgsConstructor
    class RejectedExecutionHandlerWrap {

        private Integer type;

        private RejectedExecutionHandler rejectedExecutionHandler;

    }
}
