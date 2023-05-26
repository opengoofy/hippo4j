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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RunsOldestTaskPolicyTest {

    private final RunsOldestTaskPolicy runsOldestTaskPolicy = new RunsOldestTaskPolicy();

    @Mock
    private Runnable runnable;

    @Mock
    private Runnable runnableInTheQueue;

    @Mock
    private ThreadPoolExecutor threadPoolExecutor;

    @Mock
    private BlockingQueue<Runnable> workQueue;

    @Test
    public void testRejectedExecutionWhenExecutorIsShutDown() {
        when(threadPoolExecutor.isShutdown()).thenReturn(true);

        runsOldestTaskPolicy.rejectedExecution(runnable, threadPoolExecutor);

        verify(threadPoolExecutor, never()).execute(runnable);
        verify(runnable, never()).run();
    }

    @Test
    public void testRejectedExecutionWhenATaskIsInTheQueueTheExecutorShouldNotExecute() {
        when(threadPoolExecutor.isShutdown()).thenReturn(false);
        when(threadPoolExecutor.getQueue()).thenReturn(workQueue);
        when(workQueue.poll()).thenReturn(runnableInTheQueue);
        when(workQueue.offer(runnable)).thenReturn(true);

        runsOldestTaskPolicy.rejectedExecution(runnable, threadPoolExecutor);

        verify(runnableInTheQueue).run();
        verify(threadPoolExecutor, never()).execute(runnable);
        verify(runnable, never()).run();
    }

    @Test
    public void testRejectedExecutionWhenATaskIsInTheQueueTheExecutorShouldExecute() {
        when(threadPoolExecutor.isShutdown()).thenReturn(false);
        when(threadPoolExecutor.getQueue()).thenReturn(workQueue);
        when(workQueue.poll()).thenReturn(runnableInTheQueue);
        when(workQueue.offer(runnable)).thenReturn(false);

        runsOldestTaskPolicy.rejectedExecution(runnable, threadPoolExecutor);

        verify(runnableInTheQueue).run();
        verify(threadPoolExecutor).execute(runnable);
        verify(runnable, never()).run();
    }

    @Test
    public void testRejectedExecutionWhenATaskIsInTheQueueAndThePollReturnANullValue() {
        when(threadPoolExecutor.isShutdown()).thenReturn(false);
        when(threadPoolExecutor.getQueue()).thenReturn(workQueue);
        when(workQueue.poll()).thenReturn(null);
        when(workQueue.offer(runnable)).thenReturn(false);

        runsOldestTaskPolicy.rejectedExecution(runnable, threadPoolExecutor);

        verify(runnableInTheQueue, never()).run();
        verify(threadPoolExecutor).execute(runnable);
        verify(runnable, never()).run();
    }
}