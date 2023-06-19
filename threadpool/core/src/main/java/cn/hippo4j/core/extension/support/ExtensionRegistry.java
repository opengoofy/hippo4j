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

package cn.hippo4j.core.extension.support;

import cn.hippo4j.core.extension.IExtension;
import cn.hippo4j.core.extension.annotation.Realization;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.logtracing.LogMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Extension registry
 */
@Slf4j
public final class ExtensionRegistry implements IExtensionRegistry {

    private final Map<Class<? extends IExtension>, List<IExtension>> registry = new ConcurrentHashMap<>();

    private static ExtensionRegistry INSTANCE;

    private ExtensionRegistry() {
    }

    public static ExtensionRegistry getInstance() {
        if (null == INSTANCE) {
            synchronized (ExtensionRegistry.class) {
                if (null == INSTANCE) {
                    INSTANCE = new ExtensionRegistry();
                }
            }
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void register(IExtension realization) {

        Class<?> implClass = realization.getClass();
        if (AopUtils.isAopProxy(implClass)) {
            implClass = ClassUtils.getUserClass(implClass);
        }
        Realization annotation = implClass.getAnnotation(Realization.class);

        Class<?>[] interfaces = implClass.getInterfaces();

        for (Class<?> intf : interfaces) {
            if (IExtension.class.isAssignableFrom(intf)) {
                this.register((Class<IExtension>) intf, realization);
            }
        }
    }

    private void register(Class<? extends IExtension> extension, IExtension realization) {
        if (!registry.containsKey(extension) || CollectionUtil.isEmpty(registry.get(extension))) {
            List<IExtension> realizations = new ArrayList<>();
            realizations.add(realization);
            registry.put(extension, realizations);
        } else {
            if (registry.get(extension).contains(realization)) {
                log.warn(LogMessage.getInstance()
                        .kv("realizationClassName", realization.getClass().getName())
                        .msg("Extension realization already registered, skip."));
            }
            List<IExtension> realizations = registry.get(extension);
            realizations.add(realization);
            registry.put(extension, realizations);
        }
    }

    @Override
    public List<IExtension> find(Class<? extends IExtension> extension) {
        return registry.get(extension);
    }
}
