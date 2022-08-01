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

package cn.hippo4j.springboot.starter.monitor;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.ThreadFactoryBuilder;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.springboot.starter.monitor.collect.Collector;
import cn.hippo4j.springboot.starter.monitor.send.MessageSender;
import cn.hippo4j.springboot.starter.remote.ServerHealthCheck;
import cn.hutool.core.collection.CollUtil;
import com.example.monitor.base.MonitorTypeEnum;
import com.example.monitor.base.ThreadPoolMonitor;
import com.google.common.base.Strings;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.core.executor.manage.GlobalThreadPoolManage.getThreadPoolNum;

/**
 * Dynamic thread pool collection and reporting event executor.
 * <p>
 * {@link BlockingQueue} Act as a buffer container, enabling a production-consumption model.
 *
 * @author chen.ma
 * @date 2021/12/6 20:23
 */
@Slf4j
@RequiredArgsConstructor
public class ReportingEventExecutor implements Runnable, CommandLineRunner, DisposableBean {

    @NonNull
    private final BootstrapProperties properties;

    @NonNull
    private final MessageSender messageSender;

    @NonNull
    private final ServerHealthCheck serverHealthCheck;

    /**
     * Collection of data collection components.
     */
    private Map<String, Collector> collectors;

    /**
     * Thread pool monitoring collection.
     */
    private Map<String, ThreadPoolMonitor> threadPoolMonitors;

    /**
     * Buffer container for data collection, waiting
     * for ReportingEventExecutor to report to the server.
     */
    private BlockingQueue<Message> messageCollectVessel;

    /**
     * Data collection timing executor, after Spring starts,
     * it delays for a period of time to collect the running data of the dynamic thread pool.
     */
    private ScheduledThreadPoolExecutor collectVesselExecutor;

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            try {
                Message message = messageCollectVessel.take();
                messageSender.send(message);
            } catch (Throwable ex) {
                log.error("Consumption buffer container task failed. Number of buffer container tasks :: {}", messageCollectVessel.size(), ex);
            }
        }
    }

    @Override
    public void run(String... args) {
        if (!properties.getCollect()) {
            return;
        }
        String collectType = Optional.ofNullable(Strings.emptyToNull(properties.getCollectType())).orElse(MonitorTypeEnum.SERVER.name().toLowerCase());
        collectVesselExecutor = new ScheduledThreadPoolExecutor(
                new Integer(collectType.split(",").length),
                ThreadFactoryBuilder.builder().daemon(true).prefix("client.scheduled.collect.data").build());
        if (collectType.contains(MonitorTypeEnum.PROMETHEUS.name().toLowerCase())
                || collectType.contains(MonitorTypeEnum.LOG.name().toLowerCase())) {
            // Get all dynamic thread pool monitoring components.
            threadPoolMonitors = ApplicationContextHolder.getBeansOfType(ThreadPoolMonitor.class);
            collectVesselExecutor.scheduleWithFixedDelay(
                    () -> dynamicThreadPoolMonitor(),
                    properties.getInitialDelay(),
                    properties.getCollectInterval(),
                    TimeUnit.MILLISECONDS);
        }
        if (collectType.contains(MonitorTypeEnum.SERVER.name().toLowerCase())) {
            collectVesselExecutor.scheduleWithFixedDelay(
                    () -> runTimeGatherTask(),
                    properties.getInitialDelay(),
                    properties.getCollectInterval(),
                    TimeUnit.MILLISECONDS);
            Integer bufferSize = properties.getTaskBufferSize();
            messageCollectVessel = new ArrayBlockingQueue(bufferSize);
            // Get all data collection components, currently only historical operation data collection.
            collectors = ApplicationContextHolder.getBeansOfType(Collector.class);
            // Start reporting monitoring data thread.
            ThreadUtil.newThread(this, "client.thread.reporting.task", Boolean.TRUE).start();
        }
        if (GlobalThreadPoolManage.getThreadPoolNum() > 0) {
            log.info("Dynamic thread pool :: [{}]. The dynamic thread pool starts data collection and reporting. ", getThreadPoolNum());
        }
    }

    @Override
    public void destroy() {
        Optional.ofNullable(collectVesselExecutor).ifPresent((each) -> each.shutdown());
    }

    private void dynamicThreadPoolMonitor() {
        threadPoolMonitors.forEach((beanName, monitor) -> monitor.collect());
    }

    /**
     * Collect dynamic thread pool data and add buffer queues.
     */
    private void runTimeGatherTask() {
        boolean healthStatus = serverHealthCheck.isHealthStatus();
        if (!healthStatus || CollUtil.isEmpty(collectors)) {
            return;
        }
        collectors.forEach((beanName, collector) -> {
            Message message = collector.collectMessage();
            boolean offer = messageCollectVessel.offer(message);
            if (!offer) {
                log.warn("Buffer data starts stacking data...");
            }
        });
    }
}
