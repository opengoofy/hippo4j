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

import cn.hippo4j.common.extension.function.Matcher;

import java.lang.reflect.Array;

/**
 * Array util.<br>
 * Refer to cn.hutool.core.util.ArrayUtil:<br>
 */
public class ArrayUtil {

    /**
     * Is empty.
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * Is not empty.
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }

    /**
     * First match.
     *
     * @param matcher
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T firstMatch(Matcher<T> matcher, T... array) {
        if (!isEmpty(array)) {
            for (final T val : array) {
                if (matcher.match(val)) {
                    return val;
                }
            }
        }
        return null;
    }

    /**
     * Add all.
     *
     * @param array1
     * @param array2
     * @param <T>
     * @return
     */
    public static <T> T[] addAll(final T[] array1, @SuppressWarnings("unchecked") final T... array2) {
        if (array1 == null) {
            return clone(array2);
        } else if (array2 == null) {
            return clone(array1);
        }
        final Class<?> type1 = array1.getClass().getComponentType();
        @SuppressWarnings("unchecked")
        final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        } catch (final ArrayStoreException ase) {
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of "
                        + type1.getName(), ase);
            }
            throw ase;
        }
        return joinedArray;
    }

    /**
     * Clone.
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T[] clone(final T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
}
