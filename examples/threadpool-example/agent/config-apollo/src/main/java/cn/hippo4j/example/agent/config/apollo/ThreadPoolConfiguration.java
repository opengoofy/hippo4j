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

package cn.hippo4j.example.agent.config.apollo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfiguration {

    // -------------------------------------------------------------------------
    // 未使用 Hippo4j，原始定义线程池创建方式
    // -------------------------------------------------------------------------

    @Bean
    public ThreadPoolExecutor runMessageSendTaskExecutor() {
        LinkedBlockingQueue<Runnable> linkedBlockingQueue = new LinkedBlockingQueue<>(1024);
        return new ThreadPoolExecutor(
                1,
                10,
                1024,
                TimeUnit.SECONDS,
                linkedBlockingQueue);
    }

    // -------------------------------------------------------------------------
    // 演示 Agent 模式修改线程池
    // -------------------------------------------------------------------------

    public static final ThreadPoolExecutor RUN_MESSAGE_SEND_TASK_EXECUTOR = new ThreadPoolExecutor(
            1,
            10,
            1024,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024));
}
