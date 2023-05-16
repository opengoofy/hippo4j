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

package cn.hippo4j.agent.plugin.thread.pool.define;

import cn.hippo4j.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import cn.hippo4j.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import cn.hippo4j.agent.core.plugin.match.ClassMatch;
import cn.hippo4j.agent.core.plugin.match.NameMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

/**
 * Thread pool executor instrumentation
 */
public class ThreadPoolExecutorInstrumentation extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "java.util.concurrent.ThreadPoolExecutor";

    private static final String CONSTRUCTOR_INTERCEPT_CLASS = "cn.hippo4j.agent.plugin.thread.pool.interceptor.ThreadPoolExecutorConstructorMethodInterceptor";

    private static final int CONSTRUCTOR_INTERCEPT_PARAMETER_LENGTH = 7;

    @Override
    protected ClassMatch enhanceClass() {
        return NameMatch.byName(ENHANCE_CLASS);
    }

    @Override
    public boolean isBootstrapInstrumentation() {
        return true;
    }

    /**
     * The constructor method that only intercepts all parameters prevents repeated interception.
     */
    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[]{
                new ConstructorInterceptPoint() {

                    @Override
                    public ElementMatcher<MethodDescription> getConstructorMatcher() {
                        return takesArguments(CONSTRUCTOR_INTERCEPT_PARAMETER_LENGTH);
                    }

                    @Override
                    public String getConstructorInterceptor() {
                        return CONSTRUCTOR_INTERCEPT_CLASS;
                    }
                }
        };
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[0];
    }
}
