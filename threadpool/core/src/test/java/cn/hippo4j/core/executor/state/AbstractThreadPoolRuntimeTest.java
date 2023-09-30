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

package cn.hippo4j.core.executor.state;

import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.support.AbstractThreadPoolRuntime;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class AbstractThreadPoolRuntimeTest {

    @Test
    public void testPoolRunState() {
        AbstractThreadPoolRuntime threadPoolRuntime = new AbstractThreadPoolRuntime() {

            @Override
            public ThreadPoolRunStateInfo supplement(ThreadPoolRunStateInfo threadPoolRunStateInfo) {
                return threadPoolRunStateInfo;
            }
        };
        final String threadPoolId = "test";
        DynamicThreadPoolExecutor executor = new DynamicThreadPoolExecutor(
                1, 1, 1000L, TimeUnit.MILLISECONDS,
                1000L, true, 1000L,
                new ArrayBlockingQueue<>(1), threadPoolId, Thread::new, new ThreadPoolExecutor.DiscardOldestPolicy());
        ThreadPoolExecutorRegistry.putHolder(threadPoolId, executor, null);
        ThreadPoolRunStateInfo threadPoolRunStateInfo = threadPoolRuntime.getPoolRunState(threadPoolId);
        Assertions.assertNotNull(threadPoolRunStateInfo);
        threadPoolRunStateInfo = threadPoolRuntime.getPoolRunState(threadPoolId, executor);
        Assertions.assertNotNull(threadPoolRunStateInfo);
    }
}