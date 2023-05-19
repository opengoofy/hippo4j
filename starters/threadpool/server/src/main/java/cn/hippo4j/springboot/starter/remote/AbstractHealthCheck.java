/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.springboot.starter.remote;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.springboot.starter.event.ApplicationRefreshedEvent;
import cn.hippo4j.springboot.starter.core.ShutdownExecuteException;
import cn.hippo4j.common.executor.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import static cn.hippo4j.common.constant.Constants.MAX_CHECK_FAILURE_COUNT;
import static cn.hippo4j.common.constant.Constants.SECONDS_IN_MILLISECONDS;
import static cn.hippo4j.common.constant.Constants.HEALTH_CHECK_INTERVAL;
import static cn.hippo4j.common.constant.Constants.FAILURE_SLEEP_INTERVAL;

/**
 * Abstract health check.
 */
@Slf4j
public abstract class AbstractHealthCheck implements ServerHealthCheck, InitializingBean, ApplicationListener<ApplicationRefreshedEvent> {

    /**
     * Health status
     */
    private volatile boolean healthStatus = true;

    /**
     * Health check failure count
     */
    private volatile int checkFailureCount = 0;

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
            ThreadFactoryBuilder.builder().daemon(true).prefix("client.scheduled.health.check").build());

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
                checkFailureCount = 0;
                log.info("The client reconnects to the server successfully.");
                signalAllBizThread();
            }
        } else {
            healthStatus = false;
            checkFailureCount++;
            if (checkFailureCount > 1 && checkFailureCount < MAX_CHECK_FAILURE_COUNT) {
                ThreadUtil.sleep((long) HEALTH_CHECK_INTERVAL * SECONDS_IN_MILLISECONDS * (checkFailureCount - 1));
            } else if (checkFailureCount >= MAX_CHECK_FAILURE_COUNT) {
                ThreadUtil.sleep(FAILURE_SLEEP_INTERVAL);
            }
        }
    }

    @Override
    @SneakyThrows
    public boolean isHealthStatus() {
        while (contextInitComplete && !healthStatus && !clientShutdownHook) {
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
        /**
         * Add a hook function, when the client stops, if the server is in an unhealthy state,
         * the client destroy function will suspend operation.
         */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            clientShutdownHook = true;
            signalAllBizThread();
        }));
        healthCheckExecutor.scheduleWithFixedDelay(this::healthCheck, 0, HEALTH_CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void onApplicationEvent(ApplicationRefreshedEvent event) {
        contextInitComplete = true;
    }
}
