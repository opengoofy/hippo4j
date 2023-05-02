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

import cn.hippo4j.core.executor.ExtensibleThreadPoolExecutor;
import cn.hippo4j.core.plugin.ExecuteAwarePlugin;
import cn.hippo4j.core.plugin.PluginRuntime;
import cn.hippo4j.core.plugin.manager.DefaultThreadPoolPluginManager;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for {@link MaximumActiveThreadCountChecker}
 *
 * @author huangchengxing
 */
public class MaximumActiveThreadCountCheckerTest {

    @Test
    public void testGetPluginRuntime() {
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
            "test", new DefaultThreadPoolPluginManager(),
            5, 5, 0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(10), t -> new Thread(t, UUID.randomUUID().toString()), new ThreadPoolExecutor.AbortPolicy());
        MaximumActiveThreadCountChecker checker = new MaximumActiveThreadCountChecker(executor, true);
        Assert.assertEquals(checker.getClass().getSimpleName(), checker.getPluginRuntime().getPluginId());
        // check plugin info
        List<PluginRuntime.Info> infoList = checker.getPluginRuntime().getInfoList();
        Assert.assertEquals(1, infoList.size());
        Assert.assertEquals("enableSubmitTaskAfterCheckFail", infoList.get(0).getName());
        Assert.assertEquals(true, infoList.get(0).getValue());
    }

    @Test
    public void testWhenEnableSubmitTaskAfterCheckFail() {
        int maximumThreadNum = 3;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                maximumThreadNum, maximumThreadNum, 0L, TimeUnit.MILLISECONDS,
                queue, t -> new Thread(t, UUID.randomUUID().toString()), new ThreadPoolExecutor.AbortPolicy());
        MaximumActiveThreadCountChecker checker = new MaximumActiveThreadCountChecker(executor, true);
        Assert.assertTrue(checker.isEnableSubmitTaskAfterCheckFail());
        executor.register(checker);
        WaitBeforeExecute waitBeforeExecute = new WaitBeforeExecute(0L);
        executor.register(waitBeforeExecute);

        // create 2 workers and block them
        CountDownLatch latch1 = submitTaskForBlockingThread(maximumThreadNum - 1, executor);
        try {
            // wait for the 2 workers to be executed task
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*
         * after 2 worker blocked, submit task and create the last worker, then change the maximum number of pool before the last task actually executed by worker, make plugin throw exception and
         * re-deliver the task to the queue
         */
        waitBeforeExecute.setWaitBeforeExecute(200L);
        CountDownLatch latch2 = submitTaskForBlockingThread(1, executor);
        executor.setCorePoolSize(maximumThreadNum - 1);
        executor.setMaximumPoolSize(maximumThreadNum - 1);

        // wait for plugin to re-deliver the task to the queue
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(1, queue.size());
        // last worker destroyed due to the exception which thrown by plugin
        Assert.assertEquals(2, executor.getActiveCount());

        // free resources
        latch1.countDown();
        latch2.countDown();
        executor.shutdown();
        while (executor.isTerminated()) {
        }
    }

    @Test
    public void testWhenNotEnableSubmitTaskAfterCheckFail() {
        int maximumThreadNum = 3;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1);
        ExtensibleThreadPoolExecutor executor = new ExtensibleThreadPoolExecutor(
                "test", new DefaultThreadPoolPluginManager(),
                maximumThreadNum, maximumThreadNum, 0L, TimeUnit.MILLISECONDS,
                queue, t -> new Thread(t, UUID.randomUUID().toString()), new ThreadPoolExecutor.AbortPolicy());
        MaximumActiveThreadCountChecker checker = new MaximumActiveThreadCountChecker(executor, true);
        checker.setEnableSubmitTaskAfterCheckFail(false);
        Assert.assertFalse(checker.isEnableSubmitTaskAfterCheckFail());
        executor.register(checker);
        WaitBeforeExecute waitBeforeExecute = new WaitBeforeExecute(0L);
        executor.register(waitBeforeExecute);

        // create 2 workers and block them
        CountDownLatch latch1 = submitTaskForBlockingThread(maximumThreadNum - 1, executor);
        try {
            // wait for the 2 workers to be executed task
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /*
         * after 2 worker blocked, submit task and create the last worker, then change the maximum number of pool before the last task actually executed by worker, make plugin throw exception and
         * re-deliver the task to the queue
         */
        waitBeforeExecute.setWaitBeforeExecute(200L);
        CountDownLatch latch2 = submitTaskForBlockingThread(1, executor);
        executor.setCorePoolSize(maximumThreadNum - 1);
        executor.setMaximumPoolSize(maximumThreadNum - 1);

        // wait for plugin abort the task
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(0, queue.size());
        // last worker destroyed due to the exception which thrown by plugin
        Assert.assertEquals(2, executor.getActiveCount());

        // free resources
        latch1.countDown();
        latch2.countDown();
        executor.shutdown();
        while (executor.isTerminated()) {
        }
    }

    private CountDownLatch submitTaskForBlockingThread(int num, ThreadPoolExecutor executor) {
        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < num; i++) {
            Runnable runnable = () -> {
                System.out.println(Thread.currentThread().getName() + " start");
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " has been interrupted");
                }
                System.out.println(Thread.currentThread().getName() + " completed");
            };
            System.out.println("submit task@" + runnable.hashCode());
            executor.execute(runnable);
        }
        return latch;
    }

    @Setter
    @AllArgsConstructor
    private static class WaitBeforeExecute implements ExecuteAwarePlugin {

        private long waitBeforeExecute;
        @Override
        public void beforeExecute(Thread thread, Runnable runnable) {
            try {
                Thread.sleep(waitBeforeExecute);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}