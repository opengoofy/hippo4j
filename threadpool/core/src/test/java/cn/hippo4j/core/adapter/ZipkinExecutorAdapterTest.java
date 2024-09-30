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

package cn.hippo4j.core.adapter;

import cn.hippo4j.common.executor.support.RunsOldestTaskPolicy;
import cn.hippo4j.common.handler.ZipkinExecutorAdapter;
import cn.hippo4j.core.CustomWrappingExecutorService;
import cn.hippo4j.core.executor.support.ThreadPoolBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * test for ${@link ZipkinExecutorAdapter}
 * */
@Slf4j
public class ZipkinExecutorAdapterTest {

    ZipkinExecutorAdapter zipkinExecutorAdapter = new ZipkinExecutorAdapter();
    Executor dynamicThreadPool = ThreadPoolBuilder.builder()
            .threadPoolId("test")
            .dynamicPool()
            .corePoolSize(1)
            .maximumPoolSize(2)
            .keepAliveTime(1000)
            .timeUnit(TimeUnit.MICROSECONDS)
            .threadFactory(new DefaultManagedAwareThreadFactory())
            .workQueue(new SynchronousQueue<>())
            .rejected(new RunsOldestTaskPolicy())
            .build();

    @Test
    public void testMatch() {
        Object executor = new CustomWrappingExecutorService(Executors.newCachedThreadPool());
        Assert.assertTrue(zipkinExecutorAdapter.match(executor));
    }

    @Test
    public void testUnwrap() {
        Object executor = new CustomWrappingExecutorService(Executors.newCachedThreadPool());
        ThreadPoolExecutor unwrap = zipkinExecutorAdapter.unwrap(executor);
        Assert.assertNull(unwrap);
    }

    @Test
    public void testReplace() {
        Object executor = new CustomWrappingExecutorService(Executors.newCachedThreadPool());
        CustomWrappingExecutorService executorChange = (CustomWrappingExecutorService) executor;
        ExecutorService beforeReplace = executorChange.delegate();
        zipkinExecutorAdapter.replace(executor, dynamicThreadPool);
        ExecutorService afterReplace = executorChange.delegate();

        Assert.assertNotSame(beforeReplace, afterReplace);
        Assert.assertSame(afterReplace, dynamicThreadPool);
    }
}