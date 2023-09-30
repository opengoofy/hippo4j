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

package cn.hippo4j.adapter.hystrix;

import cn.hippo4j.common.executor.ThreadFactoryBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * thread pool adapter schedule.
 */
@Slf4j
public class ThreadPoolAdapterScheduler {

    private static final int TASK_INTERVAL_SECONDS = 10;

    @Getter
    private final ScheduledExecutorService scheduler;

    public ThreadPoolAdapterScheduler() {
        scheduler = new ScheduledThreadPoolExecutor(2,
                new ThreadFactoryBuilder()
                        .prefix("threadPoolAdapter")
                        .daemon(true)
                        .build());
    }

    /**
     * Gt task interval seconds.
     *
     * @return task interval seconds
     */
    public int getTaskIntervalSeconds() {
        return TASK_INTERVAL_SECONDS;
    }
}
