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

package cn.hippo4j.agent.plugin.spring.boot.v2.interceptor;

import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import cn.hippo4j.agent.plugin.spring.boot.v2.DynamicThreadPoolChangeHandlerSpring2x;
import cn.hippo4j.agent.plugin.spring.common.support.SpringPropertiesLoader;
import cn.hippo4j.agent.plugin.spring.common.support.SpringThreadPoolRegisterSupport;
import cn.hippo4j.common.extension.design.AbstractSubjectCenter;
import cn.hippo4j.threadpool.dynamic.api.ThreadPoolDynamicRefresh;
import cn.hippo4j.threadpool.dynamic.mode.config.refresher.event.DynamicThreadPoolRefreshListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;

/**
 * Event publishing started interceptor
 */
@Slf4j
public class EventPublishingStartedInterceptor implements InstanceMethodsAroundInterceptor {

    private static final ILog LOGGER = LogManager.getLogger(EventPublishingStartedInterceptor.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) allArguments[0];
        if (context.getParent() != null) {
            // After the child container is started, the thread pool registration will be carried out
            SpringThreadPoolRegisterSupport.registerThreadPoolInstances(context);
            return ret;
        }
        SpringPropertiesLoader.loadSpringProperties(context.getEnvironment());
        ThreadPoolDynamicRefresh dynamicRefresh = new DynamicThreadPoolChangeHandlerSpring2x();
        dynamicRefresh.registerListener();
        AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH,
                new DynamicThreadPoolRefreshListener());

        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
