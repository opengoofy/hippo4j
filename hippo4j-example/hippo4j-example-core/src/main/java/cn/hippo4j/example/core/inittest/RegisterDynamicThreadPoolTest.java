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

package cn.hippo4j.example.core.inittest;

import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Register dynamic thread-pool test.
 */
@Slf4j
@Component
public class RegisterDynamicThreadPoolTest {

    @PostConstruct
    public void registerDynamicThreadPool() {
        String threadPoolId = "register-dynamic-thread-pool";
        DynamicThreadPoolRegisterParameter parameterInfo = new DynamicThreadPoolRegisterParameter();
        parameterInfo.setThreadPoolId(threadPoolId);
        parameterInfo.setCorePoolSize(3);
        parameterInfo.setMaximumPoolSize(14);
        parameterInfo.setQueueType(9);
        parameterInfo.setCapacity(110);
        parameterInfo.setKeepAliveTime(110);
        parameterInfo.setRejectedType(2);
        parameterInfo.setIsAlarm(0);
        parameterInfo.setCapacityAlarm(90);
        parameterInfo.setLivenessAlarm(90);
        parameterInfo.setAllowCoreThreadTimeOut(0);
        DynamicThreadPoolRegisterWrapper registerWrapper = DynamicThreadPoolRegisterWrapper.builder()
                .dynamicThreadPoolRegisterParameter(parameterInfo)
                .build();
        ThreadPoolExecutor dynamicThreadPool = GlobalThreadPoolManage.dynamicRegister(registerWrapper);
        log.info("Dynamic registration thread pool parameter details: {}", JSONUtil.toJSONString(dynamicThreadPool));
    }
}
