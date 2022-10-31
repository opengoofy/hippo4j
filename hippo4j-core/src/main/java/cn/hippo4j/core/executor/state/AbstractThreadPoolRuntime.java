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

import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.CalculateUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.plugin.impl.TaskRejectCountRecordPlugin;
import cn.hippo4j.core.plugin.impl.TaskTimeRecordPlugin;
import cn.hippo4j.core.plugin.impl.TaskTimeoutNotifyAlarmPlugin;
import cn.hippo4j.core.plugin.manager.ThreadPoolPluginManager;
import cn.hippo4j.core.plugin.manager.ThreadPoolPluginSupport;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Abstract threadPool runtime info.
 */
public abstract class AbstractThreadPoolRuntime {

    /**
     * Supplement.
     *
     * @param threadPoolRunStateInfo
     * @return
     */
    public abstract ThreadPoolRunStateInfo supplement(ThreadPoolRunStateInfo threadPoolRunStateInfo);

    /**
     * Get pool run state.
     *
     * @param threadPoolId
     * @return
     */
    public ThreadPoolRunStateInfo getPoolRunState(String threadPoolId) {
        DynamicThreadPoolWrapper executorService = GlobalThreadPoolManage.getExecutorService(threadPoolId);
        ThreadPoolExecutor pool = executorService.getExecutor();
        return getPoolRunState(threadPoolId, pool);
    }

    /**
     * Get pool run state.
     *
     * @param threadPoolId
     * @param executor
     * @return
     */
    public ThreadPoolRunStateInfo getPoolRunState(String threadPoolId, Executor executor) {
        ThreadPoolRunStateInfo stateInfo = new ThreadPoolRunStateInfo();
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
        stateInfo.setTpId(threadPoolId);
        collectThreadPoolExecutorInfo(threadPoolExecutor, stateInfo);
        collectPluginInfo(threadPoolExecutor, stateInfo);
        stateInfo.setClientLastRefreshTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        stateInfo.setTimestamp(System.currentTimeMillis());
        return supplement(stateInfo);
    }

    private void collectThreadPoolExecutorInfo(ThreadPoolExecutor executor, ThreadPoolRunStateInfo stateInfo) {
        // 核心线程数
        int corePoolSize = executor.getCorePoolSize();
        // 最大线程数
        int maximumPoolSize = executor.getMaximumPoolSize();
        // 线程池当前线程数 (有锁)
        int poolSize = executor.getPoolSize();
        // 活跃线程数 (有锁)
        int activeCount = executor.getActiveCount();
        // 同时进入池中的最大线程数 (有锁)
        int largestPoolSize = executor.getLargestPoolSize();
        // 线程池中执行任务总数量 (有锁)
        long completedTaskCount = executor.getCompletedTaskCount();
        stateInfo.setCoreSize(corePoolSize);
        stateInfo.setPoolSize(poolSize);
        stateInfo.setMaximumSize(maximumPoolSize);
        stateInfo.setActiveSize(activeCount);
        stateInfo.setLargestPoolSize(largestPoolSize);
        stateInfo.setCompletedTaskCount(completedTaskCount);

        // 当前负载
        String currentLoad = CalculateUtil.divide(activeCount, maximumPoolSize) + "";
        // 峰值负载
        String peakLoad = CalculateUtil.divide(largestPoolSize, maximumPoolSize) + "";
        stateInfo.setCurrentLoad(currentLoad);
        stateInfo.setPeakLoad(peakLoad);

        BlockingQueue<Runnable> queue = executor.getQueue();
        // 队列元素个数
        int queueSize = queue.size();
        // 队列类型
        String queueType = queue.getClass().getSimpleName();
        // 队列剩余容量
        int remainingCapacity = queue.remainingCapacity();
        // 队列容量
        int queueCapacity = queueSize + remainingCapacity;
        stateInfo.setQueueType(queueType);
        stateInfo.setQueueSize(queueSize);
        stateInfo.setQueueCapacity(queueCapacity);
        stateInfo.setQueueRemainingCapacity(remainingCapacity);
    }

    private void collectPluginInfo(ThreadPoolExecutor executor, ThreadPoolRunStateInfo stateInfo) {
        ThreadPoolPluginManager poolPluginManager = executor instanceof ThreadPoolPluginSupport ? (ThreadPoolPluginManager) executor : ThreadPoolPluginManager.empty();
        // TODO Make the runtime info of the plugin to internal object in stateInfo
        // get reject count
        Long rejectCount = poolPluginManager.getPluginOfType(TaskRejectCountRecordPlugin.PLUGIN_NAME, TaskRejectCountRecordPlugin.class)
                .map(TaskRejectCountRecordPlugin::getRejectCountNum)
                .orElse(-1L);
        stateInfo.setRejectCount(rejectCount);
        // get time records
        TaskTimeRecordPlugin.Summary summary = poolPluginManager.getPluginOfType(TaskTimeoutNotifyAlarmPlugin.PLUGIN_NAME, TaskTimeoutNotifyAlarmPlugin.class)
                .map(TaskTimeoutNotifyAlarmPlugin::summarize)
                .orElse(new TaskTimeRecordPlugin.Summary(-1L, -1L, -1L, 0));
        stateInfo.setMinTaskTime(summary.getMinTaskTimeMillis() + "ms");
        stateInfo.setMaxTaskTime(summary.getMaxTaskTimeMillis() + "ms");
        stateInfo.setAvgTaskTime(summary.getAvgTaskTimeMillis() + "ms");
        if (summary.getTaskCount() > 0) {
            stateInfo.setCompletedTaskCount(summary.getTaskCount());
        }
    }

}
