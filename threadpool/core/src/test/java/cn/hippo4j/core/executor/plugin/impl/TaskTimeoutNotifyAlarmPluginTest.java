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

package cn.hippo4j.core.executor.plugin.impl;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.executor.plugin.manager.DefaultThreadPoolPluginManager;
import cn.hippo4j.threadpool.alarm.api.ThreadPoolCheckAlarm;
import lombok.Getter;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link TaskTimeoutNotifyAlarmPlugin}
 */
public class TaskTimeoutNotifyAlarmPluginTest {

    private final ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginManager(),
            5, 5, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.AbortPolicy());

    private final TestAlarm testAlarm = new TestAlarm();

    private final TaskTimeoutNotifyAlarmPlugin plugin = new TaskTimeoutNotifyAlarmPlugin(
            executor.getThreadPoolId(), 1L, executor, testAlarm);

    @Test
    public void testGetId() {
        Assert.assertEquals(TaskTimeoutNotifyAlarmPlugin.PLUGIN_NAME, plugin.getId());
    }

    @Test
    public void testGetRuntime() {
        Assert.assertNotNull(plugin.getPluginRuntime());
    }

    @Test
    public void testGetExecuteTimeOut() {
        Assert.assertEquals(1L, plugin.getExecuteTimeOut().longValue());
    }

    @Test
    public void testSetExecuteTimeOut() {
        plugin.setExecuteTimeOut(2L);
        Assert.assertEquals(2L, plugin.getExecuteTimeOut().longValue());
    }

    @Test
    public void testProcessTaskTime() {
        executor.register(plugin);

        executor.submit(() -> {
            ThreadUtil.sleep(100L);
        });
        executor.submit(() -> {
            ThreadUtil.sleep(300L);
        });

        // waiting for shutdown
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        Assert.assertEquals(2, testAlarm.getNumberOfAlarms().get());
    }

    private static class TestAlarm implements ThreadPoolCheckAlarm {

        @Getter
        private final AtomicInteger numberOfAlarms = new AtomicInteger(0);

        @Override
        public void checkPoolCapacityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
            // do noting
        }

        @Override
        public void checkPoolActivityAlarm(String threadPoolId, ThreadPoolExecutor threadPoolExecutor) {
            // do noting
        }

        @Override
        public void asyncSendRejectedAlarm(String threadPoolId) {
            // do noting
        }

        @Override
        public void asyncSendExecuteTimeOutAlarm(String threadPoolId, long executeTime, long executeTimeOut, ThreadPoolExecutor threadPoolExecutor) {
            numberOfAlarms.incrementAndGet();
        }
    }

}
