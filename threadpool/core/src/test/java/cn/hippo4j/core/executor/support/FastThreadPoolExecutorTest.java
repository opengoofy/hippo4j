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

package cn.hippo4j.core.executor.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for {@link FastThreadPoolExecutor}
 */
public class FastThreadPoolExecutorTest {

    private final static int corePoolSize = 1;

    private final static int capacity = 1;

    private final TaskQueue<Runnable> taskQueue = new TaskQueue<>(capacity);

    private final FastThreadPoolExecutor fastThreadPoolExecutor = new FastThreadPoolExecutor(corePoolSize,
            corePoolSize,
            10,
            TimeUnit.SECONDS,
            taskQueue,
            Thread::new,
            new ThreadPoolExecutor.AbortPolicy());

    {
        taskQueue.setExecutor(fastThreadPoolExecutor);
    }

    @Test
    void testSubmittedTaskCount() {
        fastThreadPoolExecutor.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ignored) {
            }
        });

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
        Assertions.assertEquals(1, fastThreadPoolExecutor.getSubmittedTaskCount());

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
        Assertions.assertEquals(0, fastThreadPoolExecutor.getSubmittedTaskCount());

        // exception
        int expected = 0;
        for (int i = 0; i <= (corePoolSize + capacity); i++) {
            expected++;
            try {
                fastThreadPoolExecutor.execute(() -> {
                    synchronized (fastThreadPoolExecutor) {
                        try {
                            fastThreadPoolExecutor.wait();
                        } catch (InterruptedException ignored) {
                        }
                    }
                });
            } catch (Exception e) {
                expected--;
            }
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ignored) {
        }
        Assertions.assertEquals(expected, fastThreadPoolExecutor.getSubmittedTaskCount());

        synchronized (fastThreadPoolExecutor) {
            fastThreadPoolExecutor.notifyAll();
        }
    }
}
