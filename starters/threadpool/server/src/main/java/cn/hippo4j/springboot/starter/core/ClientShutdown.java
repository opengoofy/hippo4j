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

package cn.hippo4j.springboot.starter.core;

import lombok.Getter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Called when the application is closed to avoid data exceptions caused by
 * long polling on the client side.
 *
 * @version 1.5.0
 * @see <a href="https://github.com/opengoofy/hippo4j/issues/1121" />
 */
public class ClientShutdown {

    @Getter
    private volatile boolean prepareClose = false;
    private static final Long TIME_OUT_SECOND = 1L;

    private static final int DEFAULT_COUNT = 1;
    private final CountDownLatch countDownLatch = new CountDownLatch(DEFAULT_COUNT);

    /**
     * Called when the application is closed.
     *
     * @throws InterruptedException
     */
    public void prepareDestroy() throws InterruptedException {
        prepareClose = true;
        countDownLatch.await(TIME_OUT_SECOND, TimeUnit.SECONDS);
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero.
     *
     * <p>If the current count is greater than zero then it is decremented.
     * If the new count is zero then all waiting threads are re-enabled for
     * thread scheduling purposes.
     *
     * <p>If the current count equals zero then nothing happens.
     */
    public void countDown() {
        countDownLatch.countDown();
    }
}
