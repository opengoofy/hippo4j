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

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Synchronous put queue policy.
 */
@Slf4j
public class SyncPutQueuePolicy implements RejectedExecutionHandler {

    // The timeout value for the offer method (ms).
    private int timeout;

    private final boolean enableTimeout;

    public SyncPutQueuePolicy(int timeout){
        if (timeout < 0){
            throw new IllegalArgumentException("timeout must be greater than 0");
        }
        this.timeout = timeout;
        this.enableTimeout = true;
    }

    public SyncPutQueuePolicy (){
        this.enableTimeout = false;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }
        try {
            if (enableTimeout) {
                if (!executor.getQueue().offer(r, timeout, TimeUnit.MILLISECONDS)) {
                    throw new RejectedExecutionException("Task " + r.toString() +
                            " rejected from " +
                            executor.toString() + " with timeout " + timeout + "ms.");
                }
            }
            else {
                executor.getQueue().put(r);
            }
        } catch (InterruptedException e) {
            log.error("Adding Queue task to thread pool failed.", e);
        }
    }
}
