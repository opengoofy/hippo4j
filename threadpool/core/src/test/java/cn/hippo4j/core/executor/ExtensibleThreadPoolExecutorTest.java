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

package cn.hippo4j.core.executor;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.core.executor.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.executor.plugin.RejectedAwarePlugin;
import cn.hippo4j.core.executor.plugin.ShutdownAwarePlugin;
import cn.hippo4j.core.executor.plugin.TaskAwarePlugin;
import cn.hippo4j.core.executor.plugin.manager.DefaultThreadPoolPluginManager;
import cn.hippo4j.core.executor.plugin.manager.ThreadPoolPluginManager;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link ExtensibleThreadPoolExecutor}
 */
public class ExtensibleThreadPoolExecutorTest {

    private final RejectedExecutionHandler originalHandler = new ThreadPoolExecutor.DiscardPolicy();

    private ExtensibleThreadPoolExecutor executor;

    private ThreadPoolPluginManager manager;

    @Before
    public void initExecutor() {
        manager = new DefaultThreadPoolPluginManager();
        executor = new ExtensibleThreadPoolExecutor(
                "test", manager,
                5, 5, 1000L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), Thread::new, originalHandler);
    }

    @Test
    public void testGetThreadPoolId() {
        Assert.assertEquals("test", executor.getThreadPoolId());
    }

    @Test
    public void testGetThreadPoolExecutor() {
        Assert.assertSame(executor, executor.getThreadPoolExecutor());
    }

    @Test
    public void testGetThreadPoolPluginManager() {
        Assert.assertSame(manager, executor.getThreadPoolPluginManager());
    }

    @Test
    public void testGetOrSetRejectedHandler() {
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();
        executor.setRejectedExecutionHandler(handler);
        Assert.assertSame(handler, executor.getRejectedExecutionHandler());
    }

    @Test
    public void testInvokeTaskAwarePlugin() {
        TestTaskAwarePlugin plugin = new TestTaskAwarePlugin();
        executor.register(plugin);
        executor.submit(() -> {
        });
        executor.submit(() -> true);
        executor.submit(() -> {
        }, false);
        executor.execute(() -> {
        });
        Assert.assertEquals(7, plugin.getInvokeCount().get());
    }

    @Test
    public void testInvokeExecuteAwarePlugin() {
        TestExecuteAwarePlugin plugin = new TestExecuteAwarePlugin();
        executor.register(plugin);
        executor.execute(() -> {
        });
        ThreadUtil.sleep(500L);
        Assert.assertEquals(2, plugin.getInvokeCount().get());

        // no task will be executed because it has been replaced with null
        executor.register(new TestTaskToNullAwarePlugin());
        executor.execute(() -> {
        });
        ThreadUtil.sleep(500L);
        Assert.assertEquals(2, plugin.getInvokeCount().get());
    }

    @Test
    public void testInvokeRejectedAwarePlugin() {
        executor.setCorePoolSize(1);
        executor.setMaximumPoolSize(1);

        TestRejectedAwarePlugin plugin = new TestRejectedAwarePlugin();
        executor.register(plugin);
        // blocking pool and queue
        executor.submit(() -> ThreadUtil.sleep(500L));
        executor.submit(() -> ThreadUtil.sleep(500L));
        // reject 3 tasks
        executor.submit(() -> {
        });
        executor.submit(() -> {
        });
        executor.submit(() -> {
        });

        ThreadUtil.sleep(500L);
        Assert.assertEquals(3, plugin.getInvokeCount().get());
    }

    @Test
    public void testInvokeTestShutdownAwarePluginWhenShutdown() throws InterruptedException {
        TestShutdownAwarePlugin plugin = new TestShutdownAwarePlugin();
        executor.register(plugin);
        executor.shutdown();
        executor.submit(() -> {
            throw new IllegalArgumentException("???");
        });
        if (executor.awaitTermination(500L, TimeUnit.MILLISECONDS)) {
            Assert.assertEquals(3, plugin.getInvokeCount().get());
        }
    }

    @Test
    public void testInvokeTestShutdownAwarePluginWhenShutdownNow() throws InterruptedException {
        TestShutdownAwarePlugin plugin = new TestShutdownAwarePlugin();
        executor.register(plugin);
        executor.shutdownNow();
        if (executor.awaitTermination(500L, TimeUnit.MILLISECONDS)) {
            Assert.assertEquals(3, plugin.getInvokeCount().get());
        }
    }

    private final static class TestTaskToNullAwarePlugin implements TaskAwarePlugin {

        @Override
        public @Nullable Runnable beforeTaskExecute(@NonNull Runnable runnable) {
            return null;
        }
    }

    @Getter
    private final static class TestTaskAwarePlugin implements TaskAwarePlugin {

        private final AtomicInteger invokeCount = new AtomicInteger(0);
        private final String id = "TestTaskAwarePlugin";
        @Override
        public <V> Runnable beforeTaskCreate(ThreadPoolExecutor executor, Runnable runnable, V value) {
            invokeCount.incrementAndGet();
            return TaskAwarePlugin.super.beforeTaskCreate(executor, runnable, value);
        }
        @Override
        public <V> Callable<V> beforeTaskCreate(ThreadPoolExecutor executor, Callable<V> future) {
            invokeCount.incrementAndGet();
            return TaskAwarePlugin.super.beforeTaskCreate(executor, future);
        }
        @Override
        public Runnable beforeTaskExecute(@NonNull Runnable runnable) {
            invokeCount.incrementAndGet();
            return TaskAwarePlugin.super.beforeTaskExecute(runnable);
        }
    }

    @Getter
    private final static class TestExecuteAwarePlugin implements ExecuteAwarePlugin {

        private final AtomicInteger invokeCount = new AtomicInteger(0);
        private final String id = "TestExecuteAwarePlugin";
        @Override
        public void beforeExecute(Thread thread, Runnable runnable) {
            invokeCount.incrementAndGet();
            ExecuteAwarePlugin.super.beforeExecute(thread, runnable);
        }
        @Override
        public void afterExecute(Runnable runnable, Throwable throwable) {
            invokeCount.incrementAndGet();
            ExecuteAwarePlugin.super.afterExecute(runnable, throwable);
        }
    }

    @Getter
    private final static class TestRejectedAwarePlugin implements RejectedAwarePlugin {

        private final AtomicInteger invokeCount = new AtomicInteger(0);
        private final String id = "TestRejectedAwarePlugin";
        @Override
        public void beforeRejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            invokeCount.incrementAndGet();
        }
    }

    @Getter
    private final static class TestShutdownAwarePlugin implements ShutdownAwarePlugin {

        private final AtomicInteger invokeCount = new AtomicInteger(0);
        private final String id = "TestShutdownAwarePlugin";
        @Override
        public void beforeShutdown(ThreadPoolExecutor executor) {
            invokeCount.incrementAndGet();
            ShutdownAwarePlugin.super.beforeShutdown(executor);
        }
        @Override
        public void afterShutdown(ThreadPoolExecutor executor, List<Runnable> remainingTasks) {
            invokeCount.incrementAndGet();
            ShutdownAwarePlugin.super.afterShutdown(executor, remainingTasks);
        }
        @Override
        public void afterTerminated(ThreadPoolExecutor executor) {
            invokeCount.incrementAndGet();
            ShutdownAwarePlugin.super.afterTerminated(executor);
        }
    }

}
