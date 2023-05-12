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

package cn.hippo4j.agent.core.util;

import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {

    private static final ILog LOGGER = LogManager.getLogger(ReflectUtil.class);

    public static List<Field> getStaticFieldsFromType(Class<?> clazz, Class<?> declaredType) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> result = new ArrayList<>();
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(declaredType)
                    && Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                result.add(field);
            }
        }
        return result;
    }
}
