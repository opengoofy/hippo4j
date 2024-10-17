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

package cn.hippo4j.agent.plugin.spring.boot.v3.interceptor;

import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceMethodsAroundInterceptor;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.MethodInterceptResult;
import cn.hippo4j.agent.plugin.spring.common.event.DynamicThreadPoolRefreshListener;
import cn.hippo4j.agent.plugin.spring.common.support.SpringPropertiesLoader;
import cn.hippo4j.agent.plugin.spring.common.support.SpringThreadPoolRegisterSupport;
import cn.hippo4j.common.extension.design.AbstractSubjectCenter;
import cn.hippo4j.common.logging.api.ILog;
import cn.hippo4j.common.logging.api.LogManager;
import cn.hippo4j.core.config.ApplicationContextHolder;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Event publishing started interceptor
 */
public class EventPublishingStartedInterceptorV3 implements InstanceMethodsAroundInterceptor {

    private static final AtomicBoolean isExecuted = new AtomicBoolean(false);

    private static final ILog LOGGER = LogManager.getLogger(EventPublishingStartedInterceptorV3.class);

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, MethodInterceptResult result) throws Throwable {

    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) allArguments[0];
        // This logic will only be executed once
        if (isExecuted.compareAndSet(false, true)) {
            ApplicationContextHolder contextHolder = new ApplicationContextHolder();
            contextHolder.setApplicationContext(context);
            // Load Spring Properties
            SpringPropertiesLoader.loadSpringProperties(context.getEnvironment());
            // the thread pool registration will be carried out
            SpringThreadPoolRegisterSupport.registerThreadPoolInstances(context);
            // register Dynamic ThreadPool Refresh Listener
            if (AbstractSubjectCenter.get(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH) == null) {
                AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.THREAD_POOL_DYNAMIC_REFRESH, new DynamicThreadPoolRefreshListener());
            }
        }

        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

    }
}
