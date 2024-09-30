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

package cn.hippo4j.agent.plugin.apollo.interceptor;

import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import cn.hippo4j.agent.plugin.apollo.listeners.ApolloConfigPropertiesLoaderCompletedListener;
import cn.hippo4j.common.extension.design.AbstractSubjectCenter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default config constructor interceptor
 */
public class ApolloConfigConstructorInterceptor implements InstanceConstructorInterceptor {

    private static final AtomicBoolean isExecuted = new AtomicBoolean(false);

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {

        // This logic will only be executed once
        if (isExecuted.compareAndSet(false, true)) {
            // The Apollo plugin triggers before the Spring configuration plug-in.
            // This means that when the Apollo plug-in executes, Spring's Environment is not yet ready,
            // so the configuration cannot be read
            // After listening to the AGENT_SPRING_PROPERTIES_LOADER_COMPLETED event, register the listener for Apollo
            AbstractSubjectCenter.register(AbstractSubjectCenter.SubjectType.AGENT_SPRING_PROPERTIES_LOADER_COMPLETED,
                    new ApolloConfigPropertiesLoaderCompletedListener());
        }
    }
}
