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

package cn.hippo4j.adapter.base;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * thread pool adapter extra.
 */
@Slf4j
public class ThreadPoolAdapterExtra {

    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    private BlockingQueue<Map<String, ThreadPoolAdapter>> blockingQueue;

    public ThreadPoolAdapterExtra() {
        blockingQueue = new ArrayBlockingQueue(BLOCKING_QUEUE_CAPACITY);
    }

    public void offerQueue(Map<String, ThreadPoolAdapter> map) throws InterruptedException {
        blockingQueue.offer(map, 5, TimeUnit.SECONDS);
    }

    public void extraStart(ThreadPoolAdapterExtraHandle threadPoolAdapterExtraHandle) {
        new Thread(() -> {
            try {
                for (;;) {
                    Map<String, ThreadPoolAdapter> map = blockingQueue.take();
                    threadPoolAdapterExtraHandle.execute(map);
                }
            } catch (InterruptedException e) {
                log.error("extraStart error", e);
            }
        }, "threadPoolAdapterExtra").start();
    }
}
