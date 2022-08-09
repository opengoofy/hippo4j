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
import cn.hippo4j.common.model.register.notify.DynamicThreadPoolRegisterCoreNotifyParameter;
import cn.hippo4j.common.model.register.notify.DynamicThreadPoolRegisterServerNotifyParameter;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;
import cn.hippo4j.message.enums.NotifyPlatformEnum;
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
        parameterInfo.setThreadNamePrefix(threadPoolId);
        parameterInfo.setCorePoolSize(3);
        parameterInfo.setMaximumPoolSize(14);
        parameterInfo.setQueueType(9);
        parameterInfo.setCapacity(110);
        parameterInfo.setKeepAliveTime(110L);
        parameterInfo.setRejectedType(2);
        parameterInfo.setIsAlarm(0);
        parameterInfo.setCapacityAlarm(90);
        parameterInfo.setActiveAlarm(90);
        parameterInfo.setAllowCoreThreadTimeOut(Boolean.TRUE);

        // core 模式和 server 模式，各选其一即可
        DynamicThreadPoolRegisterCoreNotifyParameter coreNotifyParameter = DynamicThreadPoolRegisterCoreNotifyParameter.builder()
                .activeAlarm(80)
                .capacityAlarm(80)
                .receives("chen.ma")
                .alarm(true)
                .interval(5)
                .build();
        DynamicThreadPoolRegisterServerNotifyParameter serverNotifyParameter = DynamicThreadPoolRegisterServerNotifyParameter.builder()
                .platform(NotifyPlatformEnum.WECHAT.name())
                .secretKey("xxx")
                .threadPoolId(threadPoolId)
                .interval(5)
                .receives("chen.ma")
                .build();
        DynamicThreadPoolRegisterWrapper registerWrapper = DynamicThreadPoolRegisterWrapper.builder()
                .dynamicThreadPoolRegisterParameter(parameterInfo)
                .dynamicThreadPoolRegisterCoreNotifyParameter(coreNotifyParameter)
                .dynamicThreadPoolRegisterServerNotifyParameter(serverNotifyParameter)
                .build();
        ThreadPoolExecutor dynamicThreadPool = GlobalThreadPoolManage.dynamicRegister(registerWrapper);
        log.info("Dynamic registration thread pool parameter details: {}", JSONUtil.toJSONString(dynamicThreadPool));
    }
}
