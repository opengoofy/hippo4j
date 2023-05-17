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

import cn.hippo4j.common.extension.spi.ServiceLoaderRegistry;
import cn.hippo4j.core.extension.IExtension;
import cn.hippo4j.core.extension.reducer.Reducer;
import cn.hippo4j.core.extension.reducer.Reducers;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension point invoker.
 *
 * Providing extension-point invocation ability that supports the use of reducing policy.
 */
public class ExtensionInvoker {

    private static final ExtensionRegistry REGISTRY = ExtensionRegistry.getInstance();

    public static <T extends IExtension, E> List<E> reduceExecute(Class<T> targetClz,
                                                                  ExtensionCallback<T, E> callback) {
        return reduceExecute(targetClz, callback, Reducers.none());
    }

    @SuppressWarnings("unchecked")
    public static <T extends IExtension, E, R> R reduceExecute(Class<T> targetClz,
                                                               ExtensionCallback<T, E> callback,
                                                               Reducer<E, R> reducer) {
        Assert.isTrue(IExtension.class.isAssignableFrom(targetClz),
                "can not execute extension point. please implement base extension interface(" + IExtension.class.getName() + ") first.");

        List<IExtension> realizations = REGISTRY.find(targetClz);
        if (CollectionUtil.isEmpty(realizations)) {
            realizations = new ArrayList<>(ServiceLoaderRegistry.getSingletonServiceInstances(targetClz));
        }
        Assert.notEmpty(realizations, "can not find any extension realizations with interface: " + targetClz.getName());

        reducer.setRealizations(realizations);
        reducer.setCallback((ExtensionCallback<IExtension, E>) callback);
        return reducer.reduce();
    }
}
