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

import cn.hippo4j.common.model.PoolRunStateInfo;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
    protected abstract void execute(PoolRunStateInfo poolRunStateInfo);

    @Override
    public void collect() {
        List<String> listDynamicThreadPoolId = GlobalThreadPoolManage.listThreadPoolId();
        for (String each : listDynamicThreadPoolId) {
            PoolRunStateInfo poolRunState = threadPoolRunStateHandler.getPoolRunState(each);
            execute(poolRunState);
        }
    }
}
