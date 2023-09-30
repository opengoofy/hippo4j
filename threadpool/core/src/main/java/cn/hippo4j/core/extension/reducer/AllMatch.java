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

import cn.hippo4j.core.extension.IExtension;
import cn.hippo4j.core.extension.support.ReduceType;
import cn.hippo4j.common.toolkit.CollectionUtil;
import lombok.Getter;
import lombok.NonNull;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * All-match extension reduce policy.
 */
public class AllMatch<Element> extends Reducer<Element, Boolean> {

    @Getter
    private final Predicate<Element> predicate;

    public AllMatch(@NonNull Predicate<Element> predicate) {
        Objects.requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public Boolean reduce() {
        if (CollectionUtil.isEmpty(realizations)) {
            return false;
        } else {
            for (IExtension realization : realizations) {
                if (!predicate.test(getCallback().apply(realization))) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ReduceType reducerType() {
        return ReduceType.ALL;
    }

}
