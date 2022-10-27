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
import cn.hippo4j.core.plugin.ThreadPoolPlugin;
import cn.hippo4j.core.plugin.manager.DefaultThreadPoolPluginManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link ThreadPoolExecutorShutdownPlugin}
 */
public class ThreadPoolExecutorShutdownPluginTest {

    public ExtensibleThreadPoolExecutor getExecutor(ThreadPoolPlugin plugin) {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                2, 2, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        executor.register(plugin);
        return executor;
    }

    private static Callable<Integer> getCallable(AtomicInteger completedCount) {
        return () -> {
            ThreadUtil.sleep(1000L);
            return completedCount.incrementAndGet();
        };
    }

    @Test
    public void testExecuteShutdownWhenWaitTaskCompleted() {
        ExtensibleThreadPoolExecutor executor = getExecutor(
                new ThreadPoolExecutorShutdownPlugin(2000L, true));

        AtomicInteger completedCount = new AtomicInteger(0);
        Callable<Integer> future1 = getCallable(completedCount);
        Callable<Integer> future2 = getCallable(completedCount);
        executor.submit(future1);
        executor.submit(future2);

        executor.shutdown();
        Assert.assertEquals(2, completedCount.get());
    }

    @Test
    public void testExecuteShutdownWhenNotWaitTaskCompleted() {
        ExtensibleThreadPoolExecutor executor = getExecutor(
                new ThreadPoolExecutorShutdownPlugin(-1L, true));

        AtomicInteger completedCount = new AtomicInteger(0);
        Callable<Integer> future1 = getCallable(completedCount);
        Callable<Integer> future2 = getCallable(completedCount);
        executor.submit(future1);
        executor.submit(future2);

        executor.shutdown();
        Assert.assertEquals(0, completedCount.get());
    }
}