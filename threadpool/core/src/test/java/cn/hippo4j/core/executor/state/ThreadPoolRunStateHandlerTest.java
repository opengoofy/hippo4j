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

import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.handler.ThreadPoolStatusHandler;
import cn.hippo4j.common.model.ManyThreadPoolRunStateInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.ByteConvertUtil;
import cn.hippo4j.common.toolkit.MemoryUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.core.toolkit.IdentifyUtil.CLIENT_IDENTIFICATION_VALUE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThreadPoolRunStateHandlerTest {

    @Mock
    ConfigurableEnvironment environment;

    ThreadPoolRunStateInfo poolRunStateInfo = new ThreadPoolRunStateInfo();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        poolRunStateInfo.setCurrentLoad(poolRunStateInfo.getCurrentLoad() + "%");
        poolRunStateInfo.setPeakLoad(poolRunStateInfo.getPeakLoad() + "%");
        poolRunStateInfo.setTpId("1");
    }

    @Test
    void testSupplement() {
        long used = MemoryUtil.heapMemoryUsed();
        long max = MemoryUtil.heapMemoryMax();
        String memoryProportion = StringUtil.newBuilder(
                "Allocation: ",
                ByteConvertUtil.getPrintSize(used),
                " / Maximum available: ",
                ByteConvertUtil.getPrintSize(max));

        String ipAddress = "127.0.0.1";

        poolRunStateInfo.setHost(ipAddress);
        poolRunStateInfo.setMemoryProportion(memoryProportion);
        poolRunStateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(Math.subtractExact(max, used)));

        String threadPoolId = poolRunStateInfo.getTpId();

        try (final MockedStatic<ThreadPoolExecutorRegistry> globalThreadPoolManage = mockStatic(ThreadPoolExecutorRegistry.class)) {
            globalThreadPoolManage.when(() -> ThreadPoolExecutorRegistry.getHolder("1")).thenReturn(new ThreadPoolExecutorHolder());
            ThreadPoolExecutorHolder executorHolder = ThreadPoolExecutorRegistry.getHolder(threadPoolId);
            Assertions.assertNotNull(executorHolder);
        }

        ThreadPoolExecutorHolder threadPoolExecutorHolderMock = mock(ThreadPoolExecutorHolder.class);
        when(threadPoolExecutorHolderMock.getExecutor()).thenReturn(new ThreadPoolExecutor(2, 2, 2000, TimeUnit.SECONDS, new SynchronousQueue<>()));
        ThreadPoolExecutor pool = threadPoolExecutorHolderMock.getExecutor();
        Assertions.assertNotNull(pool);

        String rejectedName;
        rejectedName = "java.util.concurrent.ThreadPoolExecutor.AbortPolicy";
        poolRunStateInfo.setRejectedName(rejectedName);

        ManyThreadPoolRunStateInfo manyThreadPoolRunStateInfo = BeanUtil.convert(poolRunStateInfo, ManyThreadPoolRunStateInfo.class);
        manyThreadPoolRunStateInfo.setIdentify(CLIENT_IDENTIFICATION_VALUE);
        String active = environment.getProperty("spring.profiles.active", "UNKNOWN");
        manyThreadPoolRunStateInfo.setActive("TRUE");
        String threadPoolState = ThreadPoolStatusHandler.getThreadPoolState(pool);
        manyThreadPoolRunStateInfo.setState(threadPoolState);
        Assertions.assertNotNull(manyThreadPoolRunStateInfo);
    }

    @Test
    void testGetHeapMemory() {
        try (MockedStatic<MemoryUtil> memoryUtil = mockStatic(MemoryUtil.class)) {
            memoryUtil.when(MemoryUtil::heapMemoryUsed).thenReturn(57534464L);
            memoryUtil.when(MemoryUtil::heapMemoryMax).thenReturn(8566865920L);
            Assertions.assertEquals(8566865920L, MemoryUtil.heapMemoryMax());
            Assertions.assertEquals(57534464L, MemoryUtil.heapMemoryUsed());
        }
    }

    @Test
    void testMemoryProportion() {
        long used = 57534464L;
        long max = 8566865920L;
        String memoryProportion = StringUtil.newBuilder(
                "Allocation: ",
                ByteConvertUtil.getPrintSize(used),
                " / Maximum available: ",
                ByteConvertUtil.getPrintSize(max));
        Assertions.assertEquals("Allocation: 54.87MB / Maximum available: 7.98GB", memoryProportion);
    }
}
