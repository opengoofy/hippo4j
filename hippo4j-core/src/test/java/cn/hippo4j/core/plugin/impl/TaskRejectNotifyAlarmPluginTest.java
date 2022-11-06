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

package cn.hippo4j.core.plugin.impl;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.manager.DefaultThreadPoolPluginManager;
import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link TaskRejectNotifyAlarmPlugin}
 */
public class TaskRejectNotifyAlarmPluginTest {

    @Test
    public void testGetId() {
        Assert.assertEquals(TaskRejectNotifyAlarmPlugin.PLUGIN_NAME, new TaskRejectNotifyAlarmPlugin().getId());
    }

    @Test
    public void testGetRuntime() {
        Assert.assertNotNull(new TaskRejectNotifyAlarmPlugin().getPluginRuntime());
    }

    @Test
    public void testBeforeRejectedExecution() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy());

        AtomicInteger rejectCount = new AtomicInteger(0);
        executor.register(new TestPlugin(rejectCount, executor));
        executor.submit(() -> ThreadUtil.sleep(200L));
        executor.submit(() -> ThreadUtil.sleep(200L));
        executor.submit(() -> ThreadUtil.sleep(200L));

        // waiting for shutdown
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        Assert.assertEquals(1, rejectCount.get());
    }

    @RequiredArgsConstructor
    private static class TestPlugin extends TaskRejectNotifyAlarmPlugin {

        private final AtomicInteger count;
        private final ThreadPoolExecutor targetExecutor;

        /**
         * Callback before task is rejected.
         *
         * @param runnable task
         * @param executor executor
         */
        @Override
        public void beforeRejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            count.incrementAndGet();
            Assert.assertEquals(targetExecutor, executor);
            super.beforeRejectedExecution(runnable, executor);
        }
    }

}
