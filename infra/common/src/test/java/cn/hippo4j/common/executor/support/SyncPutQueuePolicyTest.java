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
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Synchronous placement queue policy implementation test
 */
public class SyncPutQueuePolicyTest {

    /**
     * test thread pool rejected execution
     */
    @Test
    public void testRejectedExecution() {
        SyncPutQueuePolicy syncPutQueuePolicy = new SyncPutQueuePolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 2,
                60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), syncPutQueuePolicy);
        threadPoolExecutor.prestartAllCoreThreads();

        Assert.assertSame(syncPutQueuePolicy, threadPoolExecutor.getRejectedExecutionHandler());
        IntStream.range(0, 4).forEach(s -> {
            threadPoolExecutor.execute(() -> ThreadUtil.sleep(200L));
        });
        threadPoolExecutor.shutdown();
        while (!threadPoolExecutor.isTerminated()) {
        }
        Assert.assertEquals(4, threadPoolExecutor.getCompletedTaskCount());
    }
}
