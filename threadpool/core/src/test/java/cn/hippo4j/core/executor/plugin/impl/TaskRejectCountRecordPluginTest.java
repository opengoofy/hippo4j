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
import cn.hippo4j.core.executor.plugin.impl.TaskRejectCountRecordPlugin;
import cn.hippo4j.core.executor.plugin.manager.DefaultThreadPoolPluginManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * test for {@link TaskRejectCountRecordPlugin}
 */
public class TaskRejectCountRecordPluginTest {

    @Test
    public void testGetId() {
        Assert.assertEquals(TaskRejectCountRecordPlugin.PLUGIN_NAME, new TaskRejectCountRecordPlugin().getId());
    }

    @Test
    public void testGetRuntime() {
        Assert.assertNotNull(new TaskRejectCountRecordPlugin().getPluginRuntime());
    }

    @Test
    public void testGetRejectCountNum() {
        TaskRejectCountRecordPlugin plugin = new TaskRejectCountRecordPlugin();
        Assert.assertEquals((Long) 0L, plugin.getRejectCountNum());
    }

    @Test
    public void testGetRejectCount() {
        TaskRejectCountRecordPlugin plugin = new TaskRejectCountRecordPlugin();
        Assert.assertEquals(0L, plugin.getRejectCount().get());
    }

    @Test
    public void testSetRejectCount() {
        TaskRejectCountRecordPlugin plugin = new TaskRejectCountRecordPlugin();
        AtomicLong atomicLong = new AtomicLong(0);
        plugin.setRejectCount(atomicLong);
        Assert.assertSame(atomicLong, plugin.getRejectCount());
    }

    @Test
    public void testBeforeRejectedExecution() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy());

        TaskRejectCountRecordPlugin plugin = new TaskRejectCountRecordPlugin();
        executor.register(plugin);
        executor.submit(() -> ThreadUtil.sleep(500L));
        executor.submit(() -> ThreadUtil.sleep(500L));
        executor.submit(() -> ThreadUtil.sleep(500L));

        ThreadUtil.sleep(500L);
        Assert.assertEquals((Long) 1L, plugin.getRejectCountNum());
    }

}
