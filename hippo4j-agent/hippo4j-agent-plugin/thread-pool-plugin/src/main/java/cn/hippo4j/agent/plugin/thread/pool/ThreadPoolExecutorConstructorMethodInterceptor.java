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

package cn.hippo4j.agent.plugin.thread.pool;

import cn.hippo4j.agent.core.conf.Config;
import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import cn.hippo4j.agent.core.registry.AgentThreadPoolInstanceRegistry;
import cn.hippo4j.agent.core.util.CollectionUtil;
import cn.hippo4j.agent.core.util.ReflectUtil;
import cn.hippo4j.agent.core.util.StringUtil;
import cn.hippo4j.common.config.ExecutorProperties;
import cn.hippo4j.common.executor.support.BlockingQueueTypeEnum;
import cn.hippo4j.common.executor.support.RejectedPolicyTypeEnum;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterParameter;
import cn.hippo4j.common.model.register.DynamicThreadPoolRegisterWrapper;
import cn.hippo4j.common.model.register.notify.DynamicThreadPoolRegisterCoreNotifyParameter;
import cn.hippo4j.common.toolkit.BooleanUtil;
import cn.hippo4j.core.executor.DynamicThreadPoolWrapper;
import cn.hippo4j.core.executor.manage.GlobalThreadPoolManage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorConstructorMethodInterceptor implements InstanceConstructorInterceptor {

    private static final ILog LOGGER = LogManager.getLogger(ThreadPoolExecutorConstructorMethodInterceptor.class);

    private static final List<String> EXCLUDE_STACK_TRACE_ELEMENT_CLASS_PREFIX = Arrays.asList("java", "cn.hippo4j.agent");

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {

        List<StackTraceElement> stackTraceElements = getStackTraceElements();
        if (CollectionUtil.isEmpty(stackTraceElements)) {
            return;
        }
        StackTraceElement declaredClassStackTraceElement = stackTraceElements.get(0);
        String declaredClassName = declaredClassStackTraceElement.getClassName();
        List<Field> staticFieldsFromType = ReflectUtil.getStaticFieldsFromType(Class.forName(declaredClassName),
                ThreadPoolExecutor.class);
        for (Field field : staticFieldsFromType) {
            try {
                Object value = field.get(null);
                if (value != null) {
                    String threadPoolId = declaredClassName + "#" + field.getName();
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) field.get(null);
                    register(threadPoolId, executor);
                }
            } catch (IllegalAccessException e) {
                LOGGER.error(String.format("ExecutorNameUtil, register thread pool error. ClassName=[%s], ThreadPoolFieldName=[%s]",
                        objInst.getClass().getName(), field.getName()), e);
            }
        }
    }

    private void register(String threadPoolId, ThreadPoolExecutor executor) {
        // build parameter info.
        ExecutorProperties executorProperties = ExecutorProperties.builder()
                .threadPoolId(threadPoolId)
                .corePoolSize(executor.getCorePoolSize())
                .maximumPoolSize(executor.getMaximumPoolSize())
                .allowCoreThreadTimeOut(BooleanUtil.toBoolean(String.valueOf(executor.allowsCoreThreadTimeOut())))
                .keepAliveTime(executor.getKeepAliveTime(TimeUnit.MILLISECONDS))
                .blockingQueue(BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName(executor.getQueue().getClass().getSimpleName()).getName())
                .queueCapacity(executor.getQueue().remainingCapacity())
                .threadNamePrefix(threadPoolId)
                .rejectedHandler(RejectedPolicyTypeEnum.getRejectedPolicyTypeEnumByName(executor.getRejectedExecutionHandler().getClass().getSimpleName()).getName())
                .executeTimeOut(10000L)
                .build();

        // register executor.
        AgentThreadPoolInstanceRegistry.getInstance().putHolder(threadPoolId, executor, executorProperties);

    }

    private List<StackTraceElement> getStackTraceElements() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        int i;
        for (i = 0; i < stackTraceElements.length; i++) {
            String fullClassName = stackTraceElements[i].getClassName();
            if (isBusinessStackTraceClassName(fullClassName)) {
                if (isExcludeThreadPoolClass(fullClassName)) {
                    return Collections.emptyList();
                } else {
                    break;
                }
            }
        }

        List<StackTraceElement> result = new ArrayList<>(3); // Find up to three layers
        for (int j = 0; i < stackTraceElements.length && j < 3; i++, j++) {
            String fullClassName = stackTraceElements[i].getClassName();
            if (isExcludeThreadPoolClass(fullClassName)) {
                break;
            } else {
                result.add(stackTraceElements[i]);
            }
        }

        return result;
    }

    private boolean isBusinessStackTraceClassName(String className) {
        for (String prefix : EXCLUDE_STACK_TRACE_ELEMENT_CLASS_PREFIX) {
            if (className.startsWith(prefix)) {
                return false;
            }
        }
        return true;
    }
    private boolean isExcludeThreadPoolClass(String className) {
        if (StringUtil.isBlank(className)) {
            return true;
        }
        List<String> excludePackagePrefix = Config.Plugin.ThreadPool.EXCLUDE_PACKAGE_PREFIX;
        for (String excludePrefix : excludePackagePrefix) {
            if (className.startsWith(excludePrefix)) {
                return true;
            }
        }
        return false;
    }
}
