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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Collection util.
 */
public class CollectionUtil {

    /**
     * Get first.
     *
     * @param iterable
     * @param <T>
     * @return
     */
    public static <T> T getFirst(Iterable<T> iterable) {
        Iterator<T> iterator;
        if (iterable != null && (iterator = iterable.iterator()) != null && iterator.hasNext()) {
            return iterator.next();
        }

        return null;
    }

    /**
     * Is empty.
     *
     * @param list
     * @return
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Is not empty.
     *
     * @param list
     * @return
     */
    public static boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }

    /**
     * Is empty.
     *
     * @param map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Is not empty.
     *
     * @param map
     * @return
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * Is empty.
     *
     * @param iterator
     * @return
     */
    public static boolean isEmpty(Iterator<?> iterator) {
        return null == iterator || false == iterator.hasNext();
    }

    /**
     * Is not empty.
     *
     * @param iterator
     * @return
     */
    public static boolean isNotEmpty(Iterator<?> iterator) {
        return !isEmpty(iterator);
    }

    /**
     * Is empty.
     *
     * @param collection
     * @return
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Is not empty.
     *
     * @param collection
     * @return
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
}
