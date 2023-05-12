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

package cn.hippo4j.agent.core.plugin.interceptor.enhance.v2;

import cn.hippo4j.agent.core.plugin.AbstractClassEnhancePluginDefine;
import cn.hippo4j.agent.core.plugin.EnhanceContext;
import cn.hippo4j.agent.core.plugin.PluginException;
import cn.hippo4j.agent.core.plugin.bootstrap.BootstrapInstrumentBoost;
import cn.hippo4j.agent.core.plugin.interceptor.ConstructorInterceptPoint;
import cn.hippo4j.agent.core.plugin.interceptor.EnhanceException;
import cn.hippo4j.agent.core.plugin.interceptor.InstanceMethodsInterceptPoint;
import cn.hippo4j.agent.core.plugin.interceptor.StaticMethodsInterceptPoint;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.ConstructorInter;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.OverrideCallable;
import cn.hippo4j.agent.core.plugin.interceptor.v2.ConstructorInterceptV2Point;
import cn.hippo4j.agent.core.plugin.interceptor.v2.DeclaredInstanceMethodsInterceptV2Point;
import cn.hippo4j.agent.core.plugin.interceptor.v2.InstanceMethodsInterceptV2Point;
import cn.hippo4j.agent.core.plugin.interceptor.v2.StaticMethodsInterceptV2Point;
import cn.hippo4j.agent.core.util.StringUtil;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import static net.bytebuddy.jar.asm.Opcodes.ACC_PRIVATE;
import static net.bytebuddy.jar.asm.Opcodes.ACC_VOLATILE;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * This class controls all enhance operations, including enhance constructors, instance methods and static methods. All
 * the enhances base on three types interceptor point: {@link ConstructorInterceptV2Point}, {@link
 * InstanceMethodsInterceptV2Point} and {@link StaticMethodsInterceptV2Point} If plugin is going to enhance constructors,
 * instance methods, or both, {@link ClassEnhancePluginDefineV2} will add a field of {@link Object} type.
 */
public abstract class ClassEnhancePluginDefineV2 extends AbstractClassEnhancePluginDefine {

    @Override
    protected DynamicType.Builder<?> enhanceClass(TypeDescription typeDescription,
                                                  DynamicType.Builder<?> newClassBuilder,
                                                  ClassLoader classLoader) throws PluginException {
        StaticMethodsInterceptV2Point[] staticMethodsInterceptV2Points = getStaticMethodsInterceptV2Points();
        String enhanceOriginClassName = typeDescription.getTypeName();
        if (staticMethodsInterceptV2Points == null || staticMethodsInterceptV2Points.length == 0) {
            return newClassBuilder;
        }

        for (StaticMethodsInterceptV2Point staticMethodsInterceptV2Point : staticMethodsInterceptV2Points) {
            String interceptor = staticMethodsInterceptV2Point.getMethodsInterceptorV2();
            if (StringUtil.isEmpty(interceptor)) {
                throw new EnhanceException(
                        "no StaticMethodsAroundInterceptorV2 define to enhance class " + enhanceOriginClassName);
            }

            if (staticMethodsInterceptV2Point.isOverrideArgs()) {
                if (isBootstrapInstrumentation()) {
                    newClassBuilder = newClassBuilder.method(
                            isStatic().and(staticMethodsInterceptV2Point.getMethodsMatcher()))
                            .intercept(MethodDelegation.withDefaultConfiguration()
                                    .withBinders(Morph.Binder.install(OverrideCallable.class))
                                    .to(BootstrapInstrumentBoost.forInternalDelegateClass(interceptor)));
                } else {
                    newClassBuilder = newClassBuilder.method(
                            isStatic().and(staticMethodsInterceptV2Point.getMethodsMatcher()))
                            .intercept(MethodDelegation.withDefaultConfiguration()
                                    .withBinders(Morph.Binder.install(OverrideCallable.class))
                                    .to(new StaticMethodsInterV2WithOverrideArgs(interceptor)));
                }
            } else {
                if (isBootstrapInstrumentation()) {
                    newClassBuilder = newClassBuilder.method(
                            isStatic().and(staticMethodsInterceptV2Point.getMethodsMatcher()))
                            .intercept(MethodDelegation.withDefaultConfiguration()
                                    .to(BootstrapInstrumentBoost.forInternalDelegateClass(interceptor)));
                } else {
                    newClassBuilder = newClassBuilder.method(
                            isStatic().and(staticMethodsInterceptV2Point.getMethodsMatcher()))
                            .intercept(MethodDelegation.withDefaultConfiguration()
                                    .to(new StaticMethodsInterV2(interceptor)));
                }
            }

        }

        return newClassBuilder;
    }

    @Override
    protected DynamicType.Builder<?> enhanceInstance(TypeDescription typeDescription,
                                                     DynamicType.Builder<?> newClassBuilder, ClassLoader classLoader,
                                                     EnhanceContext context) throws PluginException {
        ConstructorInterceptPoint[] constructorInterceptPoints = getConstructorsInterceptPoints();
        InstanceMethodsInterceptV2Point[] instanceMethodsInterceptV2Points = getInstanceMethodsInterceptV2Points();
        String enhanceOriginClassName = typeDescription.getTypeName();

        boolean existedConstructorInterceptPoint = false;
        if (constructorInterceptPoints != null && constructorInterceptPoints.length > 0) {
            existedConstructorInterceptPoint = true;
        }
        boolean existedMethodsInterceptV2Points = false;
        if (instanceMethodsInterceptV2Points != null && instanceMethodsInterceptV2Points.length > 0) {
            existedMethodsInterceptV2Points = true;
        }

        if (!existedConstructorInterceptPoint && !existedMethodsInterceptV2Points) {
            return newClassBuilder;
        }

        if (!typeDescription.isAssignableTo(EnhancedInstance.class)) {
            if (!context.isObjectExtended()) {
                newClassBuilder = newClassBuilder.defineField(
                        CONTEXT_ATTR_NAME, Object.class, ACC_PRIVATE | ACC_VOLATILE)
                        .implement(EnhancedInstance.class)
                        .intercept(FieldAccessor.ofField(CONTEXT_ATTR_NAME));
                context.extendObjectCompleted();
            }
        }

        if (existedConstructorInterceptPoint) {
            for (ConstructorInterceptPoint constructorInterceptPoint : constructorInterceptPoints) {
                if (isBootstrapInstrumentation()) {
                    newClassBuilder = newClassBuilder.constructor(constructorInterceptPoint.getConstructorMatcher())
                            .intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.withDefaultConfiguration()
                                    .to(BootstrapInstrumentBoost
                                            .forInternalDelegateClass(constructorInterceptPoint
                                                    .getConstructorInterceptor()))));
                } else {
                    newClassBuilder = newClassBuilder.constructor(constructorInterceptPoint.getConstructorMatcher())
                            .intercept(SuperMethodCall.INSTANCE.andThen(MethodDelegation.withDefaultConfiguration()
                                    .to(new ConstructorInter(constructorInterceptPoint
                                            .getConstructorInterceptor(), classLoader))));
                }
            }
        }

        if (existedMethodsInterceptV2Points) {
            for (InstanceMethodsInterceptV2Point instanceMethodsInterceptV2Point : instanceMethodsInterceptV2Points) {
                String interceptor = instanceMethodsInterceptV2Point.getMethodsInterceptorV2();
                if (StringUtil.isEmpty(interceptor)) {
                    throw new EnhanceException(
                            "no InstanceMethodsAroundInterceptorV2 define to enhance class " + enhanceOriginClassName);
                }
                ElementMatcher.Junction<MethodDescription> junction = not(isStatic()).and(
                        instanceMethodsInterceptV2Point.getMethodsMatcher());
                if (instanceMethodsInterceptV2Point instanceof DeclaredInstanceMethodsInterceptV2Point) {
                    junction = junction.and(ElementMatchers.<MethodDescription>isDeclaredBy(typeDescription));
                }
                if (instanceMethodsInterceptV2Point.isOverrideArgs()) {
                    if (isBootstrapInstrumentation()) {
                        newClassBuilder = newClassBuilder.method(junction)
                                .intercept(MethodDelegation.withDefaultConfiguration()
                                        .withBinders(Morph.Binder.install(OverrideCallable.class))
                                        .to(BootstrapInstrumentBoost.forInternalDelegateClass(interceptor)));
                    } else {
                        newClassBuilder = newClassBuilder.method(junction)
                                .intercept(MethodDelegation.withDefaultConfiguration()
                                        .withBinders(Morph.Binder.install(OverrideCallable.class))
                                        .to(new InstMethodsInterV2WithOverrideArgs(interceptor, classLoader)));
                    }
                } else {
                    if (isBootstrapInstrumentation()) {
                        newClassBuilder = newClassBuilder.method(junction)
                                .intercept(MethodDelegation.withDefaultConfiguration()
                                        .to(BootstrapInstrumentBoost.forInternalDelegateClass(interceptor)));
                    } else {
                        newClassBuilder = newClassBuilder.method(junction)
                                .intercept(MethodDelegation.withDefaultConfiguration()
                                        .to(new InstMethodsInterV2(interceptor, classLoader)));
                    }
                }
            }
        }

        return newClassBuilder;
    }

    @Override
    public InstanceMethodsInterceptPoint[] getInstanceMethodsInterceptPoints() {
        return null;
    }

    @Override
    public StaticMethodsInterceptPoint[] getStaticMethodsInterceptPoints() {
        return null;
    }
}
