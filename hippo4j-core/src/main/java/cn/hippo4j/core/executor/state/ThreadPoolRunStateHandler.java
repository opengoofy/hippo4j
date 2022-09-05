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

import cn.hippo4j.common.model.ManyThreadPoolRunStateInfo;
import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.common.toolkit.ByteConvertUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolExecutor;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.core.executor.support.AbstractDynamicExecutorSupport;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.RuntimeInfo;
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

    private final InetUtils hippo4JInetUtils;

    private final ConfigurableEnvironment environment;

    @Override
    public ThreadPoolRunStateInfo supplement(ThreadPoolRunStateInfo poolRunStateInfo) {
        RuntimeInfo runtimeInfo = new RuntimeInfo();
        String memoryProportion = StrUtil.builder(
                "已分配: ",
                ByteConvertUtil.getPrintSize(runtimeInfo.getTotalMemory()),
                " / 最大可用: ",
                ByteConvertUtil.getPrintSize(runtimeInfo.getMaxMemory())).toString();
        poolRunStateInfo.setCurrentLoad(poolRunStateInfo.getCurrentLoad() + "%");
        poolRunStateInfo.setPeakLoad(poolRunStateInfo.getPeakLoad() + "%");
        String ipAddress = hippo4JInetUtils.findFirstNonLoopBackHostInfo().getIpAddress();
        poolRunStateInfo.setHost(ipAddress);
        poolRunStateInfo.setMemoryProportion(memoryProportion);
        poolRunStateInfo.setFreeMemory(ByteConvertUtil.getPrintSize(runtimeInfo.getFreeMemory()));
        String threadPoolId = poolRunStateInfo.getTpId();
        DynamicThreadPoolWrapper executorService = GlobalThreadPoolManage.getExecutorService(threadPoolId);
        ThreadPoolExecutor pool = executorService.getExecutor();
        String rejectedName;
        if (pool instanceof AbstractDynamicExecutorSupport) {
            rejectedName = ((DynamicThreadPoolExecutor) pool).getRedundancyHandler().getClass().getSimpleName();
        } else {
            rejectedName = pool.getRejectedExecutionHandler().getClass().getSimpleName();
        }
        poolRunStateInfo.setRejectedName(rejectedName);
        ManyThreadPoolRunStateInfo manyThreadPoolRunStateInfo = BeanUtil.toBean(poolRunStateInfo, ManyThreadPoolRunStateInfo.class);
        manyThreadPoolRunStateInfo.setIdentify(CLIENT_IDENTIFICATION_VALUE);
        String active = environment.getProperty("spring.profiles.active", "UNKNOWN");
        manyThreadPoolRunStateInfo.setActive(active.toUpperCase());
        String threadPoolState = ThreadPoolStatusHandler.getThreadPoolState(pool);
        manyThreadPoolRunStateInfo.setState(threadPoolState);
        return manyThreadPoolRunStateInfo;
    }
}
