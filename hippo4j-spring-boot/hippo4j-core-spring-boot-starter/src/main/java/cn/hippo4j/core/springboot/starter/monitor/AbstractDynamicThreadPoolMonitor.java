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

package cn.hippo4j.core.springboot.starter.monitor;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterState;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

import static cn.hippo4j.adapter.base.ThreadPoolAdapterBeanContainer.THREAD_POOL_ADAPTER_BEAN_CONTAINER;

/**
 * Abstract dynamic thread-pool monitor.
 *
 * @author chen.ma
 * @date 2022/3/25 12:07
 */
@RequiredArgsConstructor
public abstract class AbstractDynamicThreadPoolMonitor implements DynamicThreadPoolMonitor {

    private final ThreadPoolRunStateHandler threadPoolRunStateHandler;

    /**
     * Execute.
     *
     * @param poolRunStateInfo
     */
    protected abstract void execute(ThreadPoolRunStateInfo poolRunStateInfo);

    protected abstract void execute(ThreadPoolAdapterState poolAdapterState);

    @Override
    public void collect() {
        List<String> listDynamicThreadPoolId = GlobalThreadPoolManage.listThreadPoolId();
        for (String each : listDynamicThreadPoolId) {
            ThreadPoolRunStateInfo poolRunState = threadPoolRunStateHandler.getPoolRunState(each);
            // 查询是否是第三方框架线程池
            boolean flag = true;
            final Collection<ThreadPoolAdapter> values = THREAD_POOL_ADAPTER_BEAN_CONTAINER.values();
            if (CollUtil.isEmpty(values)) {
                continue;
            }
            for (ThreadPoolAdapter item : values) {
                final ThreadPoolAdapterState threadPoolAdapterState = item.getThreadPoolState(each);
                if (threadPoolAdapterState.getCoreSize() == null || threadPoolAdapterState.getCoreSize() == 0) {
                    continue;
                }
                flag = false;
                execute(threadPoolAdapterState);
                break;
            }
            if (flag) {
                execute(poolRunState);
            }
        }
    }
}
