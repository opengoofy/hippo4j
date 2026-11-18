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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
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

    @Test
    public void testOfferAfterRemovingLastElement() {
        ResizableCapacityLinkedBlockingQueue<String> queue = new ResizableCapacityLinkedBlockingQueue<>(10);
        queue.offer("a");
        queue.offer("b");
        Assert.assertTrue(queue.remove("b"));
        queue.offer("c");
        Assert.assertEquals(Arrays.asList("a", "c"), Arrays.asList(queue.toArray()));
        Assert.assertEquals("a", queue.poll());
        Assert.assertEquals("c", queue.poll());
        Assert.assertNull(queue.poll());
    }

    @Test
    public void testOfferAfterIteratorRemovesLastElement() {
        ResizableCapacityLinkedBlockingQueue<String> queue = new ResizableCapacityLinkedBlockingQueue<>(10);
        queue.offer("a");
        Iterator<String> iterator = queue.iterator();
        Assert.assertEquals("a", iterator.next());
        iterator.remove();
        queue.offer("b");
        Assert.assertEquals("b", queue.peek());
        Assert.assertEquals("b", queue.poll());
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testOfferAfterClear() {
        ResizableCapacityLinkedBlockingQueue<String> queue = new ResizableCapacityLinkedBlockingQueue<>(10);
        queue.offer("a");
        queue.clear();
        queue.offer("b");
        Assert.assertEquals("b", queue.peek());
        Assert.assertEquals("b", queue.poll());
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testOfferAfterDrainTo() {
        ResizableCapacityLinkedBlockingQueue<String> queue = new ResizableCapacityLinkedBlockingQueue<>(10);
        queue.offer("a");
        List<String> drained = new ArrayList<>();
        Assert.assertEquals(1, queue.drainTo(drained));
        queue.offer("b");
        Assert.assertEquals("b", queue.poll());

        queue.offer("c");
        queue.offer("d");
        drained.clear();
        Assert.assertEquals(2, queue.drainTo(drained, 5));
        Assert.assertEquals(Arrays.asList("c", "d"), drained);
        queue.offer("e");
        Assert.assertEquals("e", queue.poll());

        queue.offer("f");
        queue.offer("g");
        drained.clear();
        Assert.assertEquals(1, queue.drainTo(drained, 1));
        queue.offer("h");
        Assert.assertEquals(Arrays.asList("g", "h"), Arrays.asList(queue.toArray()));
    }

    @Test
    public void testExecutorRemoveLastQueuedTask() throws InterruptedException {
        ResizableCapacityLinkedBlockingQueue<Runnable> queue = new ResizableCapacityLinkedBlockingQueue<>(10);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 1,
                60, TimeUnit.SECONDS, queue);
        CountDownLatch release = new CountDownLatch(1);
        threadPoolExecutor.execute(() -> {
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Runnable cancelled = () -> {
        };
        threadPoolExecutor.execute(cancelled);
        Assert.assertTrue(threadPoolExecutor.remove(cancelled));
        AtomicInteger ran = new AtomicInteger();
        threadPoolExecutor.execute(ran::incrementAndGet);
        release.countDown();
        threadPoolExecutor.shutdown();
        Assert.assertTrue(threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS));
        Assert.assertEquals(1, ran.get());
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