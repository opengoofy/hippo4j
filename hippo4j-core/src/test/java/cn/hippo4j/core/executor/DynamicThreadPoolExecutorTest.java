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
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.task.TaskDecorator;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * test for {@link DynamicThreadPoolExecutor}
 */
public class DynamicThreadPoolExecutorTest {

    @Test
    public void testRedundancyHandler() {
        RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardOldestPolicy();

        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, true, 1000L,
                new ArrayBlockingQueue<>(1), "test", Thread::new, handler);

        Assert.assertEquals(handler, executor.getRedundancyHandler());
        handler = new ThreadPoolExecutor.AbortPolicy();
        executor.setRedundancyHandler(handler);
        Assert.assertEquals(handler, executor.getRedundancyHandler());
    }

    @Test
    public void testTaskDecorator() {
        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, true, 1000L,
                new ArrayBlockingQueue<>(1), "test", Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());

        Assert.assertNull(executor.getTaskDecorator());
        TaskDecorator decorator = runnable -> runnable;
        executor.setTaskDecorator(decorator);
        Assert.assertEquals(decorator, executor.getTaskDecorator());

        decorator = runnable -> runnable;
        executor.setTaskDecorator(decorator);
        Assert.assertEquals(decorator, executor.getTaskDecorator());
    }

    @Test
    public void testExecuteTimeOut() {
        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, true, 1000L,
                new ArrayBlockingQueue<>(1), "test", Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());

        Assert.assertEquals(1000L, executor.getExecuteTimeOut().longValue());
        executor.setExecuteTimeOut(500L);
        Assert.assertEquals(500L, executor.getExecuteTimeOut().longValue());
    }

    @Test
    public void testDestroyWhenWaitForTask() {
        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, true, 1000L,
                new ArrayBlockingQueue<>(1), "test", Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());
        AtomicInteger count = new AtomicInteger(0);

        executor.execute(() -> {
            ThreadUtil.sleep(500L);
            count.incrementAndGet();
        });
        executor.execute(() -> {
            ThreadUtil.sleep(500L);
            count.incrementAndGet();
        });
        executor.destroy();

        // waiting for terminated
        while (!executor.isTerminated()) {
        } ;
        Assert.assertEquals(2, count.get());
    }

    @Test
    public void testDestroyWhenNotWaitForTask() {
        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, false, 1000L,
                new ArrayBlockingQueue<>(1), "test", Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());
        AtomicInteger count = new AtomicInteger(0);

        executor.execute(() -> {
            ThreadUtil.sleep(500L);
            count.incrementAndGet();
        });
        executor.execute(() -> {
            ThreadUtil.sleep(500L);
            count.incrementAndGet();
        });
        executor.destroy();

        // waiting for terminated
        while (!executor.isTerminated()) {
        }
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void testRejectCount() {
        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, true, 1000L,
                new ArrayBlockingQueue<>(1), "test", Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());

        Assert.assertEquals(0L, executor.getRejectCountNum().longValue());
        Assert.assertEquals(0L, executor.getRejectCount().get());

        executor.submit(() -> ThreadUtil.sleep(100L));
        executor.submit(() -> ThreadUtil.sleep(100L));
        executor.submit(() -> ThreadUtil.sleep(100L));
        ThreadUtil.sleep(200L);
        Assert.assertEquals(1L, executor.getRejectCountNum().longValue());
        Assert.assertEquals(1L, executor.getRejectCount().get());
    }

    @Test
    public void testSupportParam() {
        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, true, 1000L,
                new ArrayBlockingQueue<>(1), "test", Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());
        Assert.assertEquals(1000L, executor.getAwaitTerminationMillis());
        Assert.assertTrue(executor.isWaitForTasksToCompleteOnShutdown());

        executor.setSupportParam(500L, false);
        Assert.assertEquals(500L, executor.getAwaitTerminationMillis());
        Assert.assertFalse(executor.isWaitForTasksToCompleteOnShutdown());
    }

    @Test
    public void testIsActive() {
        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, true, 1000L,
                new ArrayBlockingQueue<>(1), "test", Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());
        Assert.assertTrue(executor.isActive());

        // waiting for terminated
        executor.destroy();
        while (!executor.isTerminated()) {
        }
        Assert.assertFalse(executor.isActive());
        executor.destroy();
        Assert.assertFalse(executor.isActive());
    }

}
