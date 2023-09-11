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
import cn.hippo4j.common.support.AbstractThreadPoolRuntime;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.ByteConvertUtil;
import cn.hippo4j.common.toolkit.MemoryUtil;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.ThreadPoolExecutor;

import static cn.hippo4j.core.toolkit.IdentifyUtil.CLIENT_IDENTIFICATION_VALUE;

/**
 * Thread pool run state service.
 */
@Slf4j
@AllArgsConstructor
public class ThreadPoolRunStateHandler extends AbstractThreadPoolRuntime {

    private final InetUtils hippo4jInetUtils;

    private final ConfigurableEnvironment environment;

    @Override
    public ThreadPoolRunStateInfo supplement(ThreadPoolRunStateInfo poolRunStateInfo) {
        long used = MemoryUtil.heapMemoryUsed();
        long max = MemoryUtil.heapMemoryMax();
        String memoryProportion = StringUtil.newBuilder(
                "Allocation: ",
                ByteConvertUtil.getPrintSize(used),
                " / Maximum available: ",
                ByteConvertUtil.getPrintSize(max));
        poolRunStateInfo.setCurrentLoad(poolRunStateInfo.getCurrentLoad() + "%");
        poolRunStateInfo.setPeakLoad(poolRunStateInfo.getPeakLoad() + "%");
        String ipAddress = hippo4jInetUtils.findFirstNonLoopBackHostInfo().getIpAddress();
        poolRunStateInfo.setHost(ipAddress);
        poolRunStateInfo.setMemoryProportion(memoryProportion);
        poolRunStateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(Math.subtractExact(max, used)));
        String threadPoolId = poolRunStateInfo.getTpId();
        ThreadPoolExecutorHolder executorHolder = ThreadPoolExecutorRegistry.getHolder(threadPoolId);
        ThreadPoolExecutor pool = executorHolder.getExecutor();
        String rejectedName;
        rejectedName = pool.getRejectedExecutionHandler().getClass().getSimpleName();
        poolRunStateInfo.setRejectedName(rejectedName);

        ManyThreadPoolRunStateInfo manyThreadPoolRunStateInfo = convert(poolRunStateInfo);
        manyThreadPoolRunStateInfo.setIdentify(CLIENT_IDENTIFICATION_VALUE);
        String active = environment.getProperty("spring.profiles.active", "UNKNOWN");
        manyThreadPoolRunStateInfo.setActive(active.toUpperCase());
        String threadPoolState = ThreadPoolStatusHandler.getThreadPoolState(pool);
        manyThreadPoolRunStateInfo.setState(threadPoolState);
        return manyThreadPoolRunStateInfo;
    }

    private ManyThreadPoolRunStateInfo convert(ThreadPoolRunStateInfo poolRunStateInfo) {
        ManyThreadPoolRunStateInfo manyThreadPoolRunStateInfo = new ManyThreadPoolRunStateInfo();
        manyThreadPoolRunStateInfo.setCurrentLoad(poolRunStateInfo.getCurrentLoad());
        manyThreadPoolRunStateInfo.setPeakLoad(poolRunStateInfo.getPeakLoad());
        manyThreadPoolRunStateInfo.setTpId(poolRunStateInfo.getTpId());
        manyThreadPoolRunStateInfo.setActiveCount(poolRunStateInfo.getActiveCount());
        manyThreadPoolRunStateInfo.setPoolSize(poolRunStateInfo.getPoolSize());
        manyThreadPoolRunStateInfo.setActiveSize(poolRunStateInfo.getActiveSize());
        manyThreadPoolRunStateInfo.setLargestPoolSize(poolRunStateInfo.getLargestPoolSize());
        manyThreadPoolRunStateInfo.setQueueSize(poolRunStateInfo.getQueueSize());
        manyThreadPoolRunStateInfo.setQueueRemainingCapacity(poolRunStateInfo.getQueueRemainingCapacity());
        manyThreadPoolRunStateInfo.setCompletedTaskCount(poolRunStateInfo.getCompletedTaskCount());
        manyThreadPoolRunStateInfo.setRejectCount(poolRunStateInfo.getRejectCount());
        manyThreadPoolRunStateInfo.setHost(poolRunStateInfo.getHost());
        manyThreadPoolRunStateInfo.setMemoryProportion(poolRunStateInfo.getMemoryProportion());
        manyThreadPoolRunStateInfo.setFreeMemory(poolRunStateInfo.getFreeMemory());
        manyThreadPoolRunStateInfo.setClientLastRefreshTime(poolRunStateInfo.getClientLastRefreshTime());
        manyThreadPoolRunStateInfo.setTimestamp(poolRunStateInfo.getTimestamp());
        manyThreadPoolRunStateInfo.setCoreSize(poolRunStateInfo.getCoreSize());
        manyThreadPoolRunStateInfo.setMaximumSize(poolRunStateInfo.getMaximumSize());
        manyThreadPoolRunStateInfo.setQueueType(poolRunStateInfo.getQueueType());
        manyThreadPoolRunStateInfo.setQueueCapacity(poolRunStateInfo.getQueueCapacity());
        manyThreadPoolRunStateInfo.setRejectedName(poolRunStateInfo.getRejectedName());
        manyThreadPoolRunStateInfo.setKeepAliveTime(poolRunStateInfo.getKeepAliveTime());
        return manyThreadPoolRunStateInfo;
    }
}
