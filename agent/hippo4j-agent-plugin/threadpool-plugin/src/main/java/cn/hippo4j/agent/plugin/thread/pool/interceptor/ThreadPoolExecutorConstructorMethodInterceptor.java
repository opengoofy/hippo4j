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

package cn.hippo4j.agent.plugin.thread.pool.interceptor;

import cn.hippo4j.agent.core.conf.Config;
import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.EnhancedInstance;
import cn.hippo4j.agent.core.plugin.interceptor.enhance.InstanceConstructorInterceptor;
import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.agent.core.util.CollectionUtil;
import cn.hippo4j.agent.core.util.StringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Thread pool executor constructor method interceptor
 */
public class ThreadPoolExecutorConstructorMethodInterceptor implements InstanceConstructorInterceptor {

    private static final ILog LOGGER = LogManager.getLogger(ThreadPoolExecutorConstructorMethodInterceptor.class);

    private static final List<String> EXCLUDE_STACK_TRACE_ELEMENT_CLASS_PREFIX = Arrays.asList("java", "cn.hippo4j.agent");

    private static final int INITIAL_CAPACITY = 3;

    @Override
    public void onConstruct(EnhancedInstance objInst, Object[] allArguments) throws Throwable {

        List<StackTraceElement> stackTraceElements = getStackTraceElements();
        if (CollectionUtil.isEmpty(stackTraceElements)) {
            return;
        }
        StackTraceElement declaredClassStackTraceElement = stackTraceElements.get(0);
        String declaredClassName = declaredClassStackTraceElement.getClassName();
        Class<?> declaredClass = Thread.currentThread().getContextClassLoader().loadClass(declaredClassName);
        ThreadPoolExecutorRegistry.REFERENCED_CLASS_MAP.put((ThreadPoolExecutor) objInst, declaredClass);
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

        List<StackTraceElement> result = new ArrayList<>(INITIAL_CAPACITY); // Find up to three layers
        for (int j = 0; i < stackTraceElements.length && j < INITIAL_CAPACITY; i++, j++) {
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
