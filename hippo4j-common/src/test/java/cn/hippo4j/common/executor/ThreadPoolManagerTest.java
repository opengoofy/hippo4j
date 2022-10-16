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

package cn.hippo4j.common.executor;

import cn.hippo4j.common.toolkit.ReflectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadPoolManagerTest {

    // tenantId = schedule，group = schedule
    ScheduledExecutorService executorService1 = Executors.newScheduledThreadPool(1);

    // tenantId = schedule，group = schedule
    ScheduledExecutorService executorService2 = Executors.newScheduledThreadPool(10);

    // tenantId = executor，group = executor
    ExecutorService executorService3 = Executors.newFixedThreadPool(8);

    // tenantId = executor，group = executor
    ExecutorService executorService4 = Executors.newFixedThreadPool(16);

    static final String schedule = "schedule";

    static final String executor = "executor";

    @Test
    public void getInstance() {
        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();
        Assert.assertNotNull(poolManager);
    }

    @Test
    public void register() {
        ThreadPoolManager poolManager = ThreadPoolManager.getInstance();
        poolManager.register(schedule, schedule, executorService1);
        poolManager.register(schedule, schedule, executorService2);
        poolManager.register(executor, executor, executorService3);
        poolManager.register(executor, executor, executorService4);

        Map<String, Map<String, Set<ExecutorService>>> manager = (Map<String, Map<String, Set<ExecutorService>>>) ReflectUtil.getFieldValue(poolManager, "resourcesManager");

        Map<String, Set<ExecutorService>> scheduleMap = manager.get(schedule);
        Assert.assertEquals(1, scheduleMap.size());
        Set<ExecutorService> scheduleSet = scheduleMap.get(schedule);
        Assert.assertEquals(2, scheduleSet.size());

        Map<String, Set<ExecutorService>> executorMap = manager.get(executor);
        Assert.assertEquals(1, executorMap.size());
        Set<ExecutorService> executorSet = executorMap.get(executor);
        Assert.assertEquals(2, executorSet.size());
    }
}
