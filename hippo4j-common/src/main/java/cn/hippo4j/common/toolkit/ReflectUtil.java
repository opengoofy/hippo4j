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

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ClassUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflect util.
 */
public class ReflectUtil {

    private static final Map<Class<?>, Field[]> FIELDS_CACHE = new ConcurrentHashMap();

    public static Object getFieldValue(Object obj, String fieldName) {
        if (null == obj || StringUtil.isBlank(fieldName)) {
            return null;
        }
        Field field = getField(obj instanceof Class ? (Class<?>) obj : obj.getClass(), fieldName);
        return getFieldValue(obj, field);
    }

    public static Object getFieldValue(Object obj, Field field) {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            obj = null;
        }
        setAccessible(field);
        Object result;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            String exceptionMsg = String.format("IllegalAccess for %s.%s", field.getDeclaringClass(), field.getName());
            throw new RuntimeException(exceptionMsg, e);
        }
        return result;
    }

    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && false == accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    public static Field getField(Class<?> beanClass, String name) throws SecurityException {
        final Field[] fields = getFields(beanClass);
        return ArrayUtil.firstMatch((field) -> name.equals(getFieldName(field)), fields);
    }

    public static Field[] getFields(Class<?> beanClass) throws SecurityException {
        Field[] allFields = FIELDS_CACHE.get(beanClass);
        if (null != allFields) {
            return allFields;
        }
        allFields = getFieldsDirectly(beanClass, true);
        FIELDS_CACHE.put(beanClass, allFields);
        return allFields;
    }

    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        Assert.notNull(beanClass);
        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                int length = allFields.length;
                allFields = Arrays.copyOf(allFields, length + declaredFields.length);
                System.arraycopy(declaredFields, 0, allFields, length, declaredFields.length);
            }
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }

        return allFields;
    }

    public static String getFieldName(Field field) {
        if (null == field) {
            return null;
        }
        return field.getName();
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws UtilException {
        cn.hutool.core.lang.Assert.notNull(obj);
        cn.hutool.core.lang.Assert.notBlank(fieldName);
        final Field field = getField((obj instanceof Class) ? (Class<?>) obj : obj.getClass(), fieldName);
        cn.hutool.core.lang.Assert.notNull(field, "Field [{}] is not exist in [{}]", fieldName, obj.getClass().getName());
        setFieldValue(obj, field, value);
    }

    public static void setFieldValue(Object obj, Field field, Object value) throws UtilException {
        cn.hutool.core.lang.Assert.notNull(field, "Field in [{}] not exist !", obj);
        final Class<?> fieldType = field.getType();
        if (null != value) {
            if (false == fieldType.isAssignableFrom(value.getClass())) {
                final Object targetValue = Convert.convert(fieldType, value);
                if (null != targetValue) {
                    value = targetValue;
                }
            }
        } else {
            value = ClassUtil.getDefaultValue(fieldType);
        }
        setAccessible(field);
        try {
            field.set(obj instanceof Class ? null : obj, value);
        } catch (IllegalAccessException e) {
            throw new UtilException(e, "IllegalAccess for {}.{}", obj, field.getName());
        }
    }
}
