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

package cn.hippo4j.common.support;

import cn.hippo4j.common.executor.ThreadPoolExecutorHolder;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.CalculateUtil;
import cn.hippo4j.common.toolkit.ReflectUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.common.constant.Constants.NO_REJECT_COUNT_NUM;

/**
 * Abstract threadPool runtime info.
 */
public abstract class AbstractThreadPoolRuntime {

    /**
     * Supplemental thread pool runtime information.
     *
     * @param threadPoolRunStateInfo thread-pool run state info
     * @return thread-pool run state info
     */
    public abstract ThreadPoolRunStateInfo supplement(ThreadPoolRunStateInfo threadPoolRunStateInfo);

    /**
     * Get pool run state.
     *
     * @param threadPoolId thread-pool id
     * @return thread-pool run state info
     */
    public ThreadPoolRunStateInfo getPoolRunState(String threadPoolId) {
        ThreadPoolExecutorHolder executorHolder = ThreadPoolExecutorRegistry.getHolder(threadPoolId);
        ThreadPoolExecutor pool = executorHolder.getExecutor();
        return getPoolRunState(threadPoolId, pool);
    }

    /**
     * Get pool run state.
     *
     * @param threadPoolId thread-pool id
     * @param executor     executor
     * @return thread-pool run state info
     */
    public ThreadPoolRunStateInfo getPoolRunState(String threadPoolId, Executor executor) {
        ThreadPoolExecutor actualExecutor = (ThreadPoolExecutor) executor;
        int activeCount = actualExecutor.getActiveCount();
        int largestPoolSize = actualExecutor.getLargestPoolSize();
        BlockingQueue<Runnable> blockingQueue = actualExecutor.getQueue();
        long rejectCount = NO_REJECT_COUNT_NUM;
        if (Objects.equals(actualExecutor.getClass().getName(), "cn.hippo4j.core.executor.DynamicThreadPoolExecutor")) {
            Object actualRejectCountNum = ReflectUtil.invoke(actualExecutor, "getRejectCountNum");
            if (actualRejectCountNum != null) {
                rejectCount = (long) actualRejectCountNum;
            }
        }
        ThreadPoolRunStateInfo stateInfo = ThreadPoolRunStateInfo.builder()
                .tpId(threadPoolId)
                .activeSize(activeCount)
                .poolSize(actualExecutor.getPoolSize())
                .completedTaskCount(actualExecutor.getCompletedTaskCount())
                .largestPoolSize(largestPoolSize)
                .currentLoad(CalculateUtil.divide(activeCount, actualExecutor.getMaximumPoolSize()) + "")
                .clientLastRefreshTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .peakLoad(CalculateUtil.divide(largestPoolSize, actualExecutor.getMaximumPoolSize()) + "")
                .queueSize(blockingQueue.size())
                .queueRemainingCapacity(blockingQueue.remainingCapacity())
                .rejectCount(rejectCount)
                .timestamp(System.currentTimeMillis())
                .build();
        stateInfo.setCoreSize(actualExecutor.getCorePoolSize());
        stateInfo.setMaximumSize(actualExecutor.getMaximumPoolSize());
        stateInfo.setQueueType(blockingQueue.getClass().getSimpleName());
        stateInfo.setQueueCapacity(blockingQueue.size() + blockingQueue.remainingCapacity());
        return supplement(stateInfo);
    }
}
