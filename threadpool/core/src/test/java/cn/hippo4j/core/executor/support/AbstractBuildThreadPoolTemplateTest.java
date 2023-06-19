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

package cn.hippo4j.core.executor.support;

import cn.hippo4j.common.toolkit.ThreadUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link AbstractBuildThreadPoolTemplate}
 *
 * @author dmego
 */
public class AbstractBuildThreadPoolTemplateTest {

    AbstractBuildThreadPoolTemplate.ThreadPoolInitParam initParam;

    @Before
    public void before() {
        initParam = new AbstractBuildThreadPoolTemplate.ThreadPoolInitParam(Executors.defaultThreadFactory());
        initParam.setCorePoolNum(1)
                .setMaximumPoolSize(1)
                .setKeepAliveTime(1000L)
                .setCapacity(10)
                .setExecuteTimeOut(5000L)
                .setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy())
                .setWorkQueue(new LinkedBlockingQueue<>(1))
                .setTimeUnit(TimeUnit.MILLISECONDS)
                .setAllowCoreThreadTimeOut(false)
                .setThreadPoolId("test")
                .setTaskDecorator(runnable -> runnable);
    }

    @Test
    public void testBuildPool() {
        ThreadPoolExecutor executor = AbstractBuildThreadPoolTemplate.buildPool(initParam);
        AtomicInteger count = new AtomicInteger(0);
        executor.submit(() -> {
            ThreadUtil.sleep(100L);
            return count.incrementAndGet();
        });
        executor.submit(() -> {
            ThreadUtil.sleep(100L);
            count.incrementAndGet();
        });

        // waiting for shutdown
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        Assert.assertEquals(2, count.get());

    }

    @Test
    public void testBuildDynamicPool() {
        initParam.setWaitForTasksToCompleteOnShutdown(true);
        initParam.setAwaitTerminationMillis(5000L);
        ThreadPoolExecutor executor = AbstractBuildThreadPoolTemplate.buildDynamicPool(initParam);
        AtomicInteger count = new AtomicInteger(0);
        executor.submit(() -> {
            ThreadUtil.sleep(100L);
            return count.incrementAndGet();
        });
        executor.submit(() -> {
            ThreadUtil.sleep(100L);
            count.incrementAndGet();
        });
        // waiting for shutdown
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        Assert.assertEquals(2, count.get());
    }
}
