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

package cn.hippo4j.config.springboot.starter.refresher.event;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.BeanUtil;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.config.springboot.starter.config.AdapterExecutorProperties;
import cn.hippo4j.config.springboot.starter.support.DynamicThreadPoolAdapterRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.hippo4j.common.constant.Constants.IDENTIFY_SLICER_SYMBOL;
import static cn.hippo4j.config.springboot.starter.refresher.event.Hippo4jConfigDynamicRefreshEventOrder.ADAPTER_EXECUTORS_LISTENER;

/**
 * Adapter executors refresh listener.
 */
@Slf4j
@Order(ADAPTER_EXECUTORS_LISTENER)
public class AdapterExecutorsRefreshListener extends AbstractRefreshListener<AdapterExecutorProperties> {

    @Override
    public String getNodes(AdapterExecutorProperties properties) {
        return properties.getNodes();
    }

    @Override
    public void onApplicationEvent(Hippo4jConfigDynamicRefreshEvent event) {
        List<AdapterExecutorProperties> adapterExecutors;
        Map<String, ThreadPoolAdapter> threadPoolAdapterMap = ApplicationContextHolder.getBeansOfType(ThreadPoolAdapter.class);
        if (CollectionUtil.isEmpty(adapterExecutors = event.getBootstrapConfigProperties().getAdapterExecutors()) || CollectionUtil.isEmpty(threadPoolAdapterMap)) {
            return;
        }
        for (AdapterExecutorProperties each : adapterExecutors) {
            String buildKey = each.getMark() + IDENTIFY_SLICER_SYMBOL + each.getThreadPoolKey();
            AdapterExecutorProperties adapterExecutorProperties = DynamicThreadPoolAdapterRegister.ADAPTER_EXECUTORS_MAP.get(buildKey);
            if (adapterExecutorProperties == null || !adapterExecutorProperties.getEnable() || !match(adapterExecutorProperties)) {
                continue;
            }
            if (!Objects.equals(adapterExecutorProperties.getCorePoolSize(), each.getCorePoolSize())
                    || !Objects.equals(adapterExecutorProperties.getMaximumPoolSize(), each.getMaximumPoolSize())) {
                threadPoolAdapterMap.forEach((key, val) -> {
                    if (Objects.equals(val.mark(), each.getMark())) {
                        val.updateThreadPool(BeanUtil.convert(each, ThreadPoolAdapterParameter.class));
                        DynamicThreadPoolAdapterRegister.ADAPTER_EXECUTORS_MAP.put(buildKey, each);
                    }
                });
            }
        }
    }
}
