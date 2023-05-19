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

package cn.hippo4j.common.toolkit;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Bean util.<br>
 * use com.github.dozermapper
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanUtil {

    protected static final Mapper BEAN_MAPPER_BUILDER;

    static {
        BEAN_MAPPER_BUILDER = DozerBeanMapperBuilder.buildDefault();
    }

    public static <T, S> T convert(S source, Class<T> clazz) {
        return Optional.ofNullable(source)
                .map(each -> BEAN_MAPPER_BUILDER.map(each, clazz))
                .orElse(null);
    }

    public static <T, S> T convert(S source, T target) {
        Optional.ofNullable(source)
                .ifPresent(each -> BEAN_MAPPER_BUILDER.map(each, target));
        return target;
    }

    public static <T, S> List<T> convert(List<S> sources, Class<T> clazz) {
        return Optional.ofNullable(sources)
                .map(each -> {
                    List<T> targetList = new ArrayList<T>(each.size());
                    each.forEach(item -> targetList.add(BEAN_MAPPER_BUILDER.map(item, clazz)));
                    return targetList;
                })
                .orElse(null);
    }

    public static <T, S> Set<T> convert(Set<S> sources, Class<T> clazz) {
        return Optional.ofNullable(sources)
                .map(each -> {
                    Set<T> targetSize = new HashSet<T>(each.size());
                    each.forEach(item -> targetSize.add(BEAN_MAPPER_BUILDER.map(item, clazz)));
                    return targetSize;
                })
                .orElse(null);
    }

    /**
     * map to bean
     *
     * @param map         source data
     * @param clazz       type
     * @param toCamelCase key convert
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz, boolean toCamelCase) {
        if (clazz == null) {
            return null;
        }
        try {
            T newInstance = clazz.newInstance();
            return mapToBean(map, newInstance, toCamelCase);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("do not create instance for " + clazz.getName(), e);
        }
    }

    /**
     * map to bean
     *
     * @param map         map
     * @param bean        obj bean
     * @param toCamelCase format to camel case
     * @param <T>         bean type
     * @return T
     */
    public static <T> T mapToBean(Map<String, Object> map, T bean, boolean toCamelCase) {
        if (bean == null) {
            return null;
        }
        if (map.isEmpty()) {
            return bean;
        }
        Class<?> clazz = bean.getClass();
        map.forEach((s, o) -> {
            String name = toCamelCase ? StringUtil.toCamelCase(s, StringUtil.UNDERLINE) : s;
            Method method = setter(clazz, name);
            if (method != null) {
                ReflectUtil.invoke(bean, method, o);
            }
        });
        return bean;
    }

    /**
     * getter for properties
     *
     * @param o              obj
     * @param propertiesName name
     * @return Method for get
     */
    public static Method getter(Class<?> o, String propertiesName) {
        if (o == null) {
            return null;
        }
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(propertiesName, o);
            return descriptor.getReadMethod();
        } catch (IntrospectionException e) {
            throw new RuntimeException("not find getter for" + propertiesName + "in" + o.getName(), e);
        }
    }

    /**
     * setter for properties
     *
     * @param o              obj
     * @param propertiesName name
     * @return Method for set
     */
    public static Method setter(Class<?> o, String propertiesName) {
        if (o == null) {
            return null;
        }
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(propertiesName, o);
            return descriptor.getWriteMethod();
        } catch (IntrospectionException e) {
            throw new RuntimeException("not find setter for" + propertiesName + "in" + o.getName(), e);
        }
    }
}
