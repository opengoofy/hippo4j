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

package cn.hippo4j.common.handler;

import cn.hippo4j.common.api.DynamicThreadPoolAdapter;
import cn.hippo4j.common.toolkit.ReflectUtil;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Zipkin thread local executor adapter.
 */
public class ZipkinExecutorAdapter implements DynamicThreadPoolAdapter {

    private static final String MATCH_CLASS_NAME = "brave.internal.WrappingExecutorService";
    private static final String FIELD_NAME = "delegate";
    private static final String TYPE_NAME = "java.util.concurrent.ExecutorService";

    @Override
    public boolean match(Object executor) {
        return matchSuper(executor);
    }

    public boolean matchSuper(Object executor) {
        if (Objects.equals(MATCH_CLASS_NAME, Optional.ofNullable(executor).map(Object::getClass).map(Class::getName).orElse(null))) {
            return true;
        } else {
            return Objects.equals(MATCH_CLASS_NAME, Optional.ofNullable(executor).map(Object::getClass).map(Class::getSuperclass).map(Class::getName).orElse(null));
        }
    }

    @Override
    public ThreadPoolExecutor unwrap(Object executor) {
        Object unwrap = doUnwrap(executor);
        if (unwrap == null) {
            return null;
        }
        return (ThreadPoolExecutor) unwrap;
    }

    @Override
    public void replace(Object executor, Executor dynamicThreadPoolExecutor) {
        Field field = ReflectUtil.findField(executor, FIELD_NAME, TYPE_NAME);
        ReflectUtil.setFieldValue(executor, field, dynamicThreadPoolExecutor);
    }

    private Object doUnwrap(Object executor) {
        Object unwrap = ReflectUtil.getFieldValue(executor, FIELD_NAME);
        if (unwrap == null) {
            Field field = ReflectUtil.findField(executor, FIELD_NAME, TYPE_NAME);
            if (field != null) {
                return ReflectUtil.getFieldValue(executor, field);
            }
        }
        return null;
    }
}
