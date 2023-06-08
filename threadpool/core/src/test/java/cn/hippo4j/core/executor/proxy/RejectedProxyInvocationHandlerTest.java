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

package cn.hippo4j.core.executor.proxy;

import cn.hippo4j.core.config.ApplicationContextHolder;
import cn.hippo4j.threadpool.alarm.api.ThreadPoolCheckAlarm;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(MockitoJUnitRunner.class)
public class RejectedProxyInvocationHandlerTest {

    @Mock
    private ThreadPoolCheckAlarm mockAlarmHandler;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private Object target;

    @Mock
    private Method mockMethod;

    private RejectedProxyInvocationHandler handler;

    private AtomicLong rejectCount;

    @Before
    public void setUp() {
        String threadPoolId = "test-pool";
        rejectCount = new AtomicLong(0);
        handler = new RejectedProxyInvocationHandler(target, threadPoolId, rejectCount);
    }

    @Test
    public void testInvoke() throws Throwable {
        Object[] mockArgs = new Object[]{"arg1", "arg2"};
        MockedStatic<ApplicationContextHolder> mockedStatic = Mockito.mockStatic(ApplicationContextHolder.class);
        mockedStatic.when(ApplicationContextHolder::getInstance).thenReturn(applicationContext);
        mockedStatic.when(() -> ApplicationContextHolder.getBean(ThreadPoolCheckAlarm.class)).thenReturn(mockAlarmHandler);
        Mockito.doNothing().when(mockAlarmHandler).asyncSendRejectedAlarm("test-pool");
        handler.invoke(null, mockMethod, mockArgs);
        Mockito.doThrow(new InvocationTargetException(new Throwable())).when(mockMethod).invoke(target, mockArgs);
        Assertions.assertThrows(Throwable.class, () -> handler.invoke(null, mockMethod, mockArgs));
        Assertions.assertSame(rejectCount.get(), 2L);
    }
}
