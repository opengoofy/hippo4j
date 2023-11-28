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

import cn.hippo4j.common.executor.ThreadFactoryBuilder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import cn.hippo4j.common.monitor.Message;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.threadpool.monitor.support.MonitorTypeEnum;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.springboot.starter.config.MonitorProperties;
import cn.hippo4j.springboot.starter.monitor.collect.Collector;
import cn.hippo4j.springboot.starter.monitor.send.MessageSender;
import cn.hippo4j.springboot.starter.remote.ServerHealthCheck;
import cn.hippo4j.threadpool.monitor.api.ThreadPoolMonitor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Dynamic thread pool collection and reporting event executor.
 *
 * <p> {@link BlockingQueue} Act as a buffer container, enabling a production-consumption model.
 */
@Slf4j
@RequiredArgsConstructor
public class ReportingEventExecutor implements Runnable, CommandLineRunner, DisposableBean {

    private final BootstrapProperties properties;

    private final MessageSender messageSender;

    private final ServerHealthCheck serverHealthCheck;

    /**
     * Collection of data collection components.
     */
    private Map<String, Collector> collectors;

    /**
     * Thread pool monitoring collection.
     */
    private List<ThreadPoolMonitor> threadPoolMonitors;

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

    static {
        ServiceLoaderRegistry.register(ThreadPoolMonitor.class);
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            try {
                Message message = messageCollectVessel.take();
                messageSender.send(message);
            } catch (Throwable ex) {
                log.error("Consumption buffer container task failed. Number of buffer container tasks: {}", messageCollectVessel.size(), ex);
            }
        }
    }

    @Override
    public void run(String... args) {
        MonitorProperties monitor = properties.getMonitor();
        if (monitor == null
                || !monitor.getEnable()
                || StringUtil.isBlank(monitor.getThreadPoolTypes())
                || StringUtil.isBlank(monitor.getCollectTypes())) {
            return;
        }
        threadPoolMonitors = new ArrayList<>();
        String collectType = Optional.ofNullable(StringUtil.emptyToNull(monitor.getCollectTypes())).orElse(MonitorTypeEnum.SERVER.name().toLowerCase());
        collectVesselExecutor = new ScheduledThreadPoolExecutor(
                new Integer(collectType.split(",").length),
                ThreadFactoryBuilder.builder().daemon(true).prefix("client.scheduled.collect.data").build());
        Collection<ThreadPoolMonitor> dynamicThreadPoolMonitors =
                ServiceLoaderRegistry.getSingletonServiceInstances(ThreadPoolMonitor.class);
        Map<String, ThreadPoolMonitor> threadPoolMonitorMap = ApplicationContextHolder.getBeansOfType(ThreadPoolMonitor.class);
        boolean customerDynamicThreadPoolMonitorFlag = CollectionUtil.isNotEmpty(dynamicThreadPoolMonitors) || CollectionUtil.isNotEmpty(threadPoolMonitorMap);
        if (customerDynamicThreadPoolMonitorFlag) {
            threadPoolMonitorMap.forEach((beanName, bean) -> threadPoolMonitors.add(bean));
            dynamicThreadPoolMonitors.forEach(each -> threadPoolMonitors.add(each));
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
            messageCollectVessel = new LinkedBlockingQueue(bufferSize);
            // Get all data collection components, currently only historical operation data collection.
            collectors = ApplicationContextHolder.getBeansOfType(Collector.class);
            // Start reporting monitoring data thread.
            ThreadUtil.newThread(this, "client.thread.reporting.task", Boolean.TRUE).start();
        }
        if (ThreadPoolExecutorRegistry.getThreadPoolExecutorSize() > 0) {
            log.info("Dynamic thread pool: [{}]. The dynamic thread pool starts data collection and reporting.", ThreadPoolExecutorRegistry.getThreadPoolExecutorSize());
        }
    }

    @Override
    public void destroy() {
        Optional.ofNullable(collectVesselExecutor).ifPresent((each) -> each.shutdown());
    }

    /**
     * Running dynamic thread pool monitoring.
     */
    private void dynamicThreadPoolMonitor() {
        threadPoolMonitors.forEach(each -> each.collect());
    }

    /**
     * Collect dynamic thread pool data and add buffer queues.
     */
    private void runTimeGatherTask() {
        boolean healthStatus = serverHealthCheck.isHealthStatus();
        if (!healthStatus || CollectionUtil.isEmpty(collectors)) {
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
