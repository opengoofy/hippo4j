package cn.hippo4j.starter.remote;

import cn.hippo4j.starter.core.ShutdownExecuteException;
import cn.hippo4j.starter.event.ApplicationCompleteEvent;
import cn.hippo4j.core.executor.support.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static cn.hippo4j.common.constant.Constants.HEALTH_CHECK_INTERVAL;

/**
 * Abstract health check.
 *
 * @author chen.ma
 * @date 2021/12/8 20:19
 */
@Slf4j
public abstract class AbstractHealthCheck implements ServerHealthCheck, InitializingBean, ApplicationListener<ApplicationCompleteEvent> {

    /**
     * Health status
     */
    private volatile boolean healthStatus = true;

    /**
     * Client shutdown hook
     */
    private volatile boolean clientShutdownHook = false;

    /**
     * Spring context init complete. TODO: Why this
     */
    private boolean contextInitComplete = false;

    /**
     * Health main lock
     */
    private final ReentrantLock healthMainLock = new ReentrantLock();

    /**
     * Health condition
     */
    private final Condition healthCondition = healthMainLock.newCondition();

    /**
     * Health check executor
     */
    private final ScheduledThreadPoolExecutor healthCheckExecutor = new ScheduledThreadPoolExecutor(
            new Integer(1),
            ThreadFactoryBuilder.builder().daemon(true).prefix("client.scheduled.health.check").build()
    );

    /**
     * Send health check.
     *
     * @return
     */
    protected abstract boolean sendHealthCheck();

    /**
     * Health check.
     */
    public void healthCheck() {
        boolean healthCheckStatus = sendHealthCheck();
        if (healthCheckStatus) {
            if (Objects.equals(healthStatus, false)) {
                healthStatus = true;
                log.info("The client reconnects to the server successfully.");
                signalAllBizThread();
            }
        } else {
            healthStatus = false;
        }
    }

    @Override
    @SneakyThrows
    public boolean isHealthStatus() {
        while (contextInitComplete
                && !healthStatus && !clientShutdownHook) {
            healthMainLock.lock();
            try {
                healthCondition.await();
            } finally {
                healthMainLock.unlock();
            }
        }

        if (!healthStatus) {
            throw new ShutdownExecuteException();
        }

        return healthStatus;
    }

    @Override
    public void setHealthStatus(boolean healthStatus) {
        healthMainLock.lock();
        try {
            this.healthStatus = healthStatus;
            log.warn("The server health status setting is unavailable.");
        } finally {
            healthMainLock.unlock();
        }
    }

    /**
     * Signal all biz thread.
     */
    private void signalAllBizThread() {
        healthMainLock.lock();
        try {
            healthCondition.signalAll();
        } finally {
            healthMainLock.unlock();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 添加钩子函数, Client 端停止时, 如果 Server 端是非健康状态, Client 销毁函数会暂停运行
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clientShutdownHook = true;
            signalAllBizThread();
        }));

        healthCheckExecutor.scheduleWithFixedDelay(() -> healthCheck(), 0, HEALTH_CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void onApplicationEvent(ApplicationCompleteEvent event) {
        contextInitComplete = true;
    }

}
