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

package cn.hippo4j.common.executor.support;

import cn.hippo4j.common.toolkit.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
public class ResizableCapacityLinkedBlockingQueueTest {

    @Test
    public void testResizableCapacityLinkedBlockingQueueSize() throws InterruptedException {
        ResizableCapacityLinkedBlockingQueue<Integer> queue1 = new ResizableCapacityLinkedBlockingQueue(10);
        queue1.setCapacity(20);
        Assert.assertEquals(20, queue1.remainingCapacity());
        queue1.add(1);
        Assert.assertEquals(19, queue1.remainingCapacity());
        ResizableCapacityLinkedBlockingQueue<Integer> queue2 = new ResizableCapacityLinkedBlockingQueue(Arrays.asList(1, 2, 3, 4));
        queue2.setCapacity(5);
        Assert.assertEquals(1, queue2.remainingCapacity());
    }

    @Test
    public void testIncreaseResizableCapacityLinkedBlockingQueue() throws InterruptedException {
        MyRejectedExecutionHandler myRejectedExecutionHandler = new MyRejectedExecutionHandler();
        ResizableCapacityLinkedBlockingQueue<Runnable> queue = new ResizableCapacityLinkedBlockingQueue();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                60, TimeUnit.SECONDS, queue, myRejectedExecutionHandler);
        Assert.assertSame(queue, threadPoolExecutor.getQueue());
        threadPoolExecutor.prestartAllCoreThreads();
        queue.setCapacity(6);
        IntStream.range(0, 4).forEach(s -> {
            threadPoolExecutor.execute(() -> ThreadUtil.sleep(0L));
        });
        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
        }
        Assert.assertEquals(4, threadPoolExecutor.getCompletedTaskCount());
        Assert.assertEquals(0, myRejectedExecutionHandler.getCount());

    }

    @Test
    public void testDecreaseResizableCapacityLinkedBlockingQueue() throws InterruptedException {
        MyRejectedExecutionHandler myRejectedExecutionHandler = new MyRejectedExecutionHandler();
        ResizableCapacityLinkedBlockingQueue<Runnable> queue = new ResizableCapacityLinkedBlockingQueue<>(4);

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                60, TimeUnit.SECONDS, queue, myRejectedExecutionHandler);
        Assert.assertSame(queue, threadPoolExecutor.getQueue());
        threadPoolExecutor.prestartAllCoreThreads();
        queue.setCapacity(0);
        IntStream.range(0, 4).forEach(s -> {
            threadPoolExecutor.execute(() -> ThreadUtil.sleep(0L));
        });
        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
        }
        Assert.assertEquals(0, threadPoolExecutor.getCompletedTaskCount());
        Assert.assertEquals(4, myRejectedExecutionHandler.getCount());

    }
}

class MyRejectedExecutionHandler implements RejectedExecutionHandler {

    public AtomicInteger count = new AtomicInteger(0);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }
        if (!executor.getQueue().offer(r)) {
            count.incrementAndGet();
        }
    }

    public int getCount() {
        return count.get();
    }
}