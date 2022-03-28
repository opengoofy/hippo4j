package cn.hippo4j.example.core.handler;

import cn.hippo4j.core.spi.CustomRejectedExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义拒绝策略.
 *
 * @author chen.ma
 * @date 2022/1/4 22:19
 */
public class ErrorLogRejectedExecutionHandler implements CustomRejectedExecutionHandler {

    @Override
    public Integer getType() {
        return 12;
    }

    @Override
    public RejectedExecutionHandler generateRejected() {
        return new CustomErrorLogRejectedExecutionHandler();
    }

    public static class CustomErrorLogRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error("线程池抛出拒绝策略.");
        }

    }

}
