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

package cn.hippo4j.config.toolkit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import cn.hippo4j.common.toolkit.Assert;
import org.junit.Test;

/**
 * SimpleReadWriteLock Test
 */
public class SimpleReadWriteLockTest {

    @Test
    public void singleTryReadLockTest() {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        boolean result = simpleReadWriteLock.tryReadLock();
        Assert.isTrue(result);
    }

    @Test
    public void multiTryReadLockTest() {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        simpleReadWriteLock.tryReadLock();
        boolean result = simpleReadWriteLock.tryReadLock();
        Assert.isTrue(result);
    }

    @Test
    public void singleTryWriteLockTest() {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        boolean result = simpleReadWriteLock.tryWriteLock();
        Assert.isTrue(result);
    }
    @Test
    public void multiTryWriteLockTest() {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        simpleReadWriteLock.tryWriteLock();
        boolean result = simpleReadWriteLock.tryWriteLock();
        Assert.isTrue(!result);
    }

    @Test
    public void tryReadWriteLockTest() {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        simpleReadWriteLock.tryReadLock();
        boolean result = simpleReadWriteLock.tryWriteLock();
        Assert.isTrue(!result);
    }

    @Test
    public void tryWriteReadLockTest() {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        simpleReadWriteLock.tryWriteLock();
        boolean result = simpleReadWriteLock.tryReadLock();
        Assert.isTrue(!result);
    }

    @Test
    public void releaseReadLockTest() {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        simpleReadWriteLock.tryReadLock();
        simpleReadWriteLock.releaseReadLock();
        boolean result = simpleReadWriteLock.tryWriteLock();
        Assert.isTrue(result);
    }

    @Test
    public void releaseWriteLockTest() {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        simpleReadWriteLock.tryWriteLock();
        simpleReadWriteLock.releaseWriteLock();
        boolean result = simpleReadWriteLock.tryReadLock();
        Assert.isTrue(result);
    }

    @Test
    public void multiThreadTryWriteLockTest() throws Exception {
        SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
        CountDownLatch countDownLatch = new CountDownLatch(2);
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
                while (true) {
                    if (simpleReadWriteLock.tryWriteLock()) {
                        System.out.println(Thread.currentThread() + " -1 get write lock success");
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println(Thread.currentThread() + " -1 execute done");
                        simpleReadWriteLock.releaseWriteLock();
                        countDownLatch.countDown();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                while (true) {
                    if (simpleReadWriteLock.tryWriteLock()) {
                        System.out.println(Thread.currentThread() + " -2 get write lock success");
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println(Thread.currentThread() + " -2 execute done");
                        countDownLatch.countDown();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                simpleReadWriteLock.releaseWriteLock();
            }
        }).start();
        countDownLatch.await();
        Assert.isTrue(simpleReadWriteLock.tryWriteLock());
    }

}
