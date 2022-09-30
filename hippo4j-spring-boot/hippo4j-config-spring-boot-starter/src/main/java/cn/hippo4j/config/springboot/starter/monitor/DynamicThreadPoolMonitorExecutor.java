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

package cn.hippo4j.config.springboot.starter.monitor;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.springboot.starter.config.BootstrapConfigProperties;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.ThreadFactoryBuilder;
import cn.hippo4j.common.spi.DynamicThreadPoolServiceLoader;
import cn.hippo4j.monitor.base.DynamicThreadPoolMonitor;
import cn.hippo4j.monitor.base.ThreadPoolMonitor;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.core.executor.manage.GlobalThreadPoolManage.getThreadPoolNum;

/**
 * Dynamic thread-pool monitor executor.
 */
@Slf4j
@RequiredArgsConstructor
public class DynamicThreadPoolMonitorExecutor implements ApplicationRunner {

    private final BootstrapConfigProperties properties;

    private ScheduledThreadPoolExecutor collectExecutor;

    private List<ThreadPoolMonitor> threadPoolMonitors;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String collectType = properties.getCollectType();
        if (!properties.getCollect() || StringUtil.isBlank(collectType)) {
            return;
        }
        log.info("Start monitoring the running status of dynamic thread pool.");
        threadPoolMonitors = Lists.newArrayList();
        collectExecutor = new ScheduledThreadPoolExecutor(
                new Integer(1),
                ThreadFactoryBuilder.builder().daemon(true).prefix("client.scheduled.collect.data").build());
        // Get dynamic thread pool monitoring component.
        List<String> collectTypes = Arrays.asList(collectType.split(","));
        ApplicationContextHolder.getBeansOfType(ThreadPoolMonitor.class)
                .forEach((key, val) -> {
                    if (collectTypes.contains(val.getType())) {
                        threadPoolMonitors.add(val);
                    }
                });
        Collection<DynamicThreadPoolMonitor> dynamicThreadPoolMonitors =
                DynamicThreadPoolServiceLoader.getSingletonServiceInstances(DynamicThreadPoolMonitor.class);
        dynamicThreadPoolMonitors.stream().filter(each -> collectTypes.contains(each.getType())).forEach(each -> threadPoolMonitors.add(each));
        // Execute dynamic thread pool monitoring component.
        collectExecutor.scheduleWithFixedDelay(
                () -> scheduleRunnable(),
                properties.getInitialDelay(),
                properties.getCollectInterval(),
                TimeUnit.MILLISECONDS);
        if (GlobalThreadPoolManage.getThreadPoolNum() > 0) {
            log.info("Dynamic thread pool: [{}]. The dynamic thread pool starts data collection and reporting.", getThreadPoolNum());
        }
    }

    private void scheduleRunnable() {
        for (ThreadPoolMonitor each : threadPoolMonitors) {
            try {
                each.collect();
            } catch (Exception ex) {
                log.error("Error monitoring the running status of dynamic thread pool. Type: {}", each.getType(), ex);
            }
        }
    }
}
