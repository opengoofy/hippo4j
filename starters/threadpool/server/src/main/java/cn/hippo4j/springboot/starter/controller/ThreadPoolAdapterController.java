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

package cn.hippo4j.springboot.starter.controller;

import cn.hippo4j.adapter.base.ThreadPoolAdapter;
import cn.hippo4j.adapter.base.ThreadPoolAdapterParameter;
import cn.hippo4j.common.model.ThreadPoolAdapterState;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.core.api.ClientNetworkService;
import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import cn.hippo4j.springboot.starter.toolkit.CloudCommonIdUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static cn.hippo4j.adapter.base.ThreadPoolAdapterBeanContainer.THREAD_POOL_ADAPTER_BEAN_CONTAINER;

/**
 * Thread-pool adapter controller.
 */
@Slf4j
@RestController
@AllArgsConstructor
public class ThreadPoolAdapterController {

    private final ConfigurableEnvironment environment;
    private final InetUtils hippo4jInetUtils;

    @GetMapping("/adapter/thread-pool/info")
    public Result<ThreadPoolAdapterState> getAdapterThreadPool(ThreadPoolAdapterParameter requestParameter) {
        ThreadPoolAdapter threadPoolAdapter = THREAD_POOL_ADAPTER_BEAN_CONTAINER.get(requestParameter.getMark());
        ThreadPoolAdapterState result = Optional.ofNullable(threadPoolAdapter).map(each -> {
            ThreadPoolAdapterState threadPoolState = each.getThreadPoolState(requestParameter.getThreadPoolKey());
            String active = environment.getProperty("spring.profiles.active", "UNKNOWN");
            threadPoolState.setActive(active.toUpperCase());
            String[] customerNetwork = ServiceLoaderRegistry.getSingletonServiceInstances(ClientNetworkService.class)
                    .stream().findFirst().map(network -> network.getNetworkIpPort(environment)).orElse(null);
            String clientAddress;
            if (customerNetwork != null) {
                clientAddress = StringUtil.newBuilder(customerNetwork[0], ":", customerNetwork[1]);
            } else {
                clientAddress = CloudCommonIdUtil.getClientIpPort(environment, hippo4jInetUtils);
            }
            threadPoolState.setClientAddress(clientAddress);
            threadPoolState.setIdentify(IdentifyUtil.getIdentify());
            return threadPoolState;
        }).orElse(null);
        return new Result<ThreadPoolAdapterState>().setCode(Result.SUCCESS_CODE).setData(result);
    }

    @PostMapping("/adapter/thread-pool/update")
    public Result<Void> updateAdapterThreadPool(@RequestBody ThreadPoolAdapterParameter requestParameter) {
        log.info("[{}] Change third-party thread pool data. key: {}, coreSize: {}, maximumSize: {}",
                requestParameter.getMark(), requestParameter.getThreadPoolKey(), requestParameter.getCorePoolSize(), requestParameter.getMaximumPoolSize());
        ThreadPoolAdapter threadPoolAdapter = THREAD_POOL_ADAPTER_BEAN_CONTAINER.get(requestParameter.getMark());
        Optional.ofNullable(threadPoolAdapter).ifPresent(each -> each.updateThreadPool(requestParameter));
        return new Result<Void>().setCode(Result.SUCCESS_CODE);
    }
}
