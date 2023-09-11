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

package cn.hippo4j.monitor.base;

import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.threadpool.monitor.api.DynamicThreadPoolMonitor;

import javax.annotation.Resource;
import java.util.List;

/**
 * Abstract dynamic thread-pool monitor.
 */
public abstract class AbstractDynamicThreadPoolMonitor implements DynamicThreadPoolMonitor {

    private ThreadPoolRunStateHandler threadPoolRunStateHandler;

    public AbstractDynamicThreadPoolMonitor(ThreadPoolRunStateHandler handler) {
        this.threadPoolRunStateHandler = handler;
    }

    /**
     * Execute collection thread pool running data.
     *
     * @param dynamicThreadPoolRunStateInfo dynamic thread-pool run state info
     */
    protected abstract void execute(ThreadPoolRunStateInfo dynamicThreadPoolRunStateInfo);

    @Override
    public void collect() {
        List<String> listDynamicThreadPoolId = ThreadPoolExecutorRegistry.listThreadPoolExecutorId();
        listDynamicThreadPoolId.forEach(each -> execute(threadPoolRunStateHandler.getPoolRunState(each)));
    }
}
