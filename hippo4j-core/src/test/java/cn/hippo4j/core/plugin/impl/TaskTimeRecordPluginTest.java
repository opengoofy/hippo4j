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
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for {@link TaskTimeRecordPlugin}
 */
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

        TaskTimeRecordPlugin plugin = new TaskTimeRecordPlugin();
        executor.register(plugin);
        executor.submit(() -> ThreadUtil.sleep(1000L));
        executor.submit(() -> ThreadUtil.sleep(3000L));
        executor.submit(() -> ThreadUtil.sleep(2000L));

        // waiting for shutdown
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        TaskTimeRecordPlugin.Summary summary = plugin.summarize();
        Assert.assertEquals(1, summary.getMinTaskTimeMillis() / 1000L);
        Assert.assertEquals(3, summary.getMaxTaskTimeMillis() / 1000L);
        Assert.assertEquals(2, summary.getAvgTaskTimeMillis() / 1000L);
        Assert.assertEquals(6, summary.getTotalTaskTimeMillis() / 1000L);
    }
}
