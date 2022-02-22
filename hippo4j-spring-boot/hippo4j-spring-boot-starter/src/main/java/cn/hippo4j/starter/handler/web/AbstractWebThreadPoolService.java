package cn.hippo4j.starter.handler.web;

import java.util.concurrent.Executor;

/**
 * Abstract web thread pool service.
 *
 * @author chen.ma
 * @date 2022/1/19 21:20
 */
public abstract class AbstractWebThreadPoolService implements WebThreadPoolService {

    /**
     * Thread pool executor.
     */
    protected volatile Executor executor;

    /**
     * Get web thread pool by server.
     *
     * @return
     */
    protected abstract Executor getWebThreadPoolByServer();

    @Override
    public Executor getWebThreadPool() {
        if (executor == null) {
            synchronized (AbstractWebThreadPoolService.class) {
                if (executor == null) {
                    executor = getWebThreadPoolByServer();
                }
            }
        }

        return executor;
    }

}
