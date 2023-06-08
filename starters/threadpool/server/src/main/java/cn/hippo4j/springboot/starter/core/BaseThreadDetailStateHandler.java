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

import cn.hippo4j.common.api.ThreadDetailState;
import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.model.ThreadDetailStateInfo;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Base thread detail state handler.
 *
 * <p> The Java 8 implementation is temporarily provided, {@link ThreadDetailState} interface can be customized.
 */
@Slf4j
public class BaseThreadDetailStateHandler implements ThreadDetailState {

    private final String workersName = "workers";

    private final String threadName = "thread";

    @Override
    public List<ThreadDetailStateInfo> getThreadDetailStateInfo(String threadPoolId) {
        ThreadPoolExecutorHolder executorHolder = ThreadPoolExecutorRegistry.getHolder(threadPoolId);
        ThreadPoolExecutor threadPoolExecutor = executorHolder.getExecutor();
        return getThreadDetailStateInfo(threadPoolExecutor);
    }

    @Override
    public List<ThreadDetailStateInfo> getThreadDetailStateInfo(ThreadPoolExecutor threadPoolExecutor) {
        List<ThreadDetailStateInfo> resultThreadStates = new ArrayList();
        try {
            HashSet<Object> workers = (HashSet<Object>) ReflectUtil.getFieldValue(threadPoolExecutor, workersName);
            if (CollectionUtil.isEmpty(workers)) {
                return resultThreadStates;
            }
            for (Object worker : workers) {
                Thread thread;
                try {
                    thread = (Thread) ReflectUtil.getFieldValue(worker, threadName);
                    if (thread == null) {
                        log.warn("Reflection get worker thread is null. Worker: {}", worker);
                        continue;
                    }
                } catch (Exception ex) {
                    log.error("Reflection get worker thread exception. Worker: {}", worker, ex);
                    continue;
                }
                long threadId = thread.getId();
                String threadName = thread.getName();
                String threadStatus = thread.getState().name();
                StackTraceElement[] stackTrace = thread.getStackTrace();
                List<String> threadStack = new ArrayList(stackTrace.length);
                for (int i = 0; i < stackTrace.length; i++) {
                    threadStack.add(stackTrace[i].toString());
                }
                ThreadDetailStateInfo threadState = ThreadDetailStateInfo.builder()
                        .threadId(threadId)
                        .threadName(threadName)
                        .threadStatus(threadStatus)
                        .threadStack(threadStack)
                        .build();
                resultThreadStates.add(threadState);
            }
        } catch (Exception ex) {
            log.error("Failed to get thread status.", ex);
        }
        return resultThreadStates;
    }
}
