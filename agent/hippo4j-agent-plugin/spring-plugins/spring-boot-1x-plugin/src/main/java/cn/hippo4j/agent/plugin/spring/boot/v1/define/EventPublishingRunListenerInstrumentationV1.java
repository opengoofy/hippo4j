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

package cn.hippo4j.agent.plugin.spring.boot.v1.define;

import cn.hippo4j.agent.core.plugin.WitnessMethod;
import cn.hippo4j.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import cn.hippo4j.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.ClassInstanceMethodsEnhancePluginDefine;
import cn.hippo4j.agent.core.plugin.match.ClassMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.Collections;
import java.util.List;

import static cn.hippo4j.agent.core.plugin.match.NameMatch.byName;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * SpringBoot v1 EventPublishingRunListener Finished instrumentation
 */
public class EventPublishingRunListenerInstrumentationV1 extends ClassInstanceMethodsEnhancePluginDefine {

    private static final String ENHANCE_CLASS = "org.springframework.boot.context.event.EventPublishingRunListener";

    private static final String EVENT_PUBLISHING_FINISHED_INTERCEPTOR_V1 = "cn.hippo4j.agent.plugin.spring.boot.v1.interceptor.EventPublishingRunListenerFinishedInterceptorV1";

    @Override
    protected ClassMatch enhanceClass() {
        return byName(ENHANCE_CLASS);
    }

    @Override
    public ConstructorInterceptPoint[] getConstructorsInterceptPoints() {
        return new ConstructorInterceptPoint[0];
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return new InstanceMethodsInterceptPoint[]{new InstanceMethodsInterceptPoint() {

            @Override
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return named("finished");
            }

            @Override
            public String getMethodsInterceptor() {
                return EVENT_PUBLISHING_FINISHED_INTERCEPTOR_V1;
            }

            @Override
            public boolean isOverrideArgs() {
                return false;
            }
        }};
    }

    @Override
    protected List<WitnessMethod> witnessMethods() {
        return Collections.singletonList(new WitnessMethod("org.springframework.boot.context.event.EventPublishingRunListener", named("finished")));
    }
}
