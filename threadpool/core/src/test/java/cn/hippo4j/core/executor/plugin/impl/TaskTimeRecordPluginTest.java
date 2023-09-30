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
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for {@link TaskTimeRecordPlugin}
 */
@Slf4j
public class TaskTimeRecordPluginTest {

    @Test
    public void testGetId() {
        Assert.assertEquals(TaskTimeRecordPlugin.PLUGIN_NAME, new TaskTimeRecordPlugin().getId());
    }

    @Test
    public void testGetRuntime() {
        Assert.assertNotNull(new TaskTimeRecordPlugin().getPluginRuntime());
    }

    @Test
    public void testSummarize() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                3, 3, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy());

        TaskTimeRecordPlugin plugin = new TaskTimeRecordPlugin(3);
        executor.register(plugin);
        executor.submit(() -> ThreadUtil.sleep(1000L));
        executor.submit(() -> ThreadUtil.sleep(3000L));
        executor.submit(() -> ThreadUtil.sleep(2000L));
        executor.submit(() -> ThreadUtil.sleep(2000L));

        // waiting for shutdown
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        TaskTimeRecordPlugin.Summary summary = plugin.summarize();
        Assert.assertTrue(summary.getMinTaskTimeMillis() > 0L);
        Assert.assertTrue(summary.getMaxTaskTimeMillis() > 0L);
        Assert.assertTrue(summary.getAvgTaskTimeMillis() > 0L);
        Assert.assertTrue(summary.getTotalTaskTimeMillis() > 0L);
        // Assert.assertTrue(testInDeviation(summary.getMinTaskTimeMillis(), 1000L, 300L));
        // Assert.assertTrue(testInDeviation(summary.getMaxTaskTimeMillis(), 3000L, 300L));
        // Assert.assertTrue(testInDeviation(summary.getAvgTaskTimeMillis(), 2000L, 300L));
        // Assert.assertTrue(testInDeviation(summary.getTotalTaskTimeMillis(), 8000L, 300L));
    }

    private boolean testInDeviation(long except, long actual, long offer) {
        long exceptLower = except - offer;
        long exceptUpper = except + offer;
        log.info("test {} < [{}] < {}", exceptLower, actual, exceptUpper);
        return exceptLower < actual && actual < exceptUpper;
    }

    @Test
    public void testTableSizeFor() {
        int maxCap = 1 << 30;
        for (int i = 0; i <= maxCap; i++) {
            int tabSize1 = tabSizeFor_JDK8(i);
            int tabSize2 = TaskTimeRecordPlugin.tableSizeFor(i);
            Assert.assertTrue(tabSize1 == tabSize2);
        }
    }

    private static int tabSizeFor_JDK8(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= 1073741824) ? 1073741824 : n + 1;
    }

}
