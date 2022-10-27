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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link TaskDecoratorPlugin}
 */
public class TaskDecoratorPluginTest {

    private final AtomicInteger taskExecuteCount = new AtomicInteger(0);

    @Test
    public void testExecute() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                5, 5, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, new ThreadPoolExecutor.DiscardPolicy());
        TaskDecoratorPlugin plugin = new TaskDecoratorPlugin();
        plugin.addDecorator(runnable -> () -> {
            taskExecuteCount.incrementAndGet();
            runnable.run();
        });
        plugin.addDecorator(runnable -> () -> {
            taskExecuteCount.incrementAndGet();
            runnable.run();
        });
        executor.register(plugin);
        executor.execute(() -> {
        });
        ThreadUtil.sleep(500L);
        Assert.assertEquals(2, taskExecuteCount.get());
    }

}
