package cn.hippo4j.starter.wrapper;

import cn.hippo4j.starter.common.CommonDynamicThreadPool;
import cn.hippo4j.starter.core.DynamicExecutorConfigurationSupport;
import lombok.Data;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Dynamic threadPool wrapper.
 *
 * @author chen.ma
 * @date 2021/6/20 16:55
 */
@Data
public class DynamicThreadPoolWrapper implements DisposableBean {

    /**
     * Tenant id
     */
    private String tenantId;

    /**
     * Item id
     */
    private String itemId;

    /**
     * Thread pool id
     */
    private String tpId;

    /**
     * Subscribe flag
     */
    private boolean subscribeFlag;

    /**
     * executor
     * {@link cn.hippo4j.starter.core.DynamicThreadPoolExecutor}
     */
    private ThreadPoolExecutor executor;

    /**
     * 首选服务端线程池, 为空使用默认线程池 {@link CommonDynamicThreadPool#getInstance(String)}
     *
     * @param threadPoolId
     */
    public DynamicThreadPoolWrapper(String threadPoolId) {
        this(threadPoolId, CommonDynamicThreadPool.getInstance(threadPoolId));
    }

    /**
     * 首选服务端线程池, 为空使用 threadPoolExecutor.
     *
     * @param threadPoolId
     * @param threadPoolExecutor
     */
    public DynamicThreadPoolWrapper(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
        this.tpId = threadPoolId;
        this.executor = threadPoolExecutor;
    }

    /**
     * 提交任务.
     *
     * @param command
     */
    public void execute(Runnable command) {
        executor.execute(command);
    }

    /**
     * 提交任务.
     *
     * @param task
     * @return
     */
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    /**
     * 提交任务.
     *
     * @param task
     * @param <T>
     * @return
     */
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }

    @Override
    public void destroy() throws Exception {
        if (executor != null && executor instanceof DynamicExecutorConfigurationSupport) {
            ((DynamicExecutorConfigurationSupport) executor).destroy();
        }
    }

}
