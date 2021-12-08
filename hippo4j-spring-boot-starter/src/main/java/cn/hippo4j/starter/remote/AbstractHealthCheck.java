package cn.hippo4j.starter.remote;

import cn.hippo4j.starter.toolkit.thread.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract health check.
 *
 * @author chen.ma
 * @date 2021/12/8 20:19
 */
@Slf4j
public abstract class AbstractHealthCheck implements InitializingBean, ServerHealthCheck {

    /**
     * Health status
     */
    private volatile boolean healthStatus = true;

    /**
     * Health main lock
     */
    private final ReentrantLock healthMainLock = new ReentrantLock();

    /**
     * Health condition
     */
    private final Condition healthCondition = healthMainLock.newCondition();

    /**
     * Health check executor.
     */
    private final ScheduledThreadPoolExecutor healthCheckExecutor = new ScheduledThreadPoolExecutor(
            new Integer(1),
            ThreadFactoryBuilder.builder().daemon(true).prefix("health-check-scheduled").build()
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
                log.info("🚀 The client reconnects to the server successfully.");
                signalAllBizThread();
            }
        } else {
            healthStatus = false;
        }

    }

    @Override
    @SneakyThrows
    public boolean isHealthStatus() {
        while (!healthStatus) {
            healthMainLock.lock();
            try {
                healthCondition.await();
            } finally {
                healthMainLock.unlock();
            }
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
        healthCheckExecutor.scheduleWithFixedDelay(() -> healthCheck(), 0, 5, TimeUnit.SECONDS);
    }

}
