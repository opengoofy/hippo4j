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

package cn.hippo4j.core.extension.reducer;

import lombok.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * reducers
 */
@SuppressWarnings("all")
public class Reducers {

    /**
     * No reduce policy needed.
     *
     * @return None type reducer
     */
    public static <E> Reducer<E, List<E>> none() {
        return new None<>();
    }

    /**
     * Build a FirstOf Reducer.
     *
     * @param predicate the condition predicate.
     * @return FirstOf Policy Reducer.
     */
    public static <E> Reducer<E, E> firstOf(@NonNull Predicate<E> predicate) {
        return new FirstOf<>(predicate);
    }

    /**
     * Build a FirstOf Not-null Reducer.
     *
     * @return FirstOf Not-null Policy Reducer.
     */
    public static <E> Reducer<E, E> firstOfNotNull() {
        return new FirstOf<>(Objects::nonNull);
    }

    /**
     * Build a AnyMatch Reducer.
     *
     * @param predicate the condition predicate.
     * @return AnyMatch Policy Reducer.
     */
    public static <E> Reducer<E, Boolean> anyMatch(Predicate<E> predicate) {
        return new AnyMatch<>(predicate);
    }

    /**
     * Build a AllMatch Reducer
     *
     * @param predicate the condition predicate.
     * @return AllMatch Policy Reducer.
     */
    public static <E> Reducer<E, Boolean> allMatch(@NonNull Predicate<E> predicate) {
        return new AllMatch<>(predicate);
    }

}
