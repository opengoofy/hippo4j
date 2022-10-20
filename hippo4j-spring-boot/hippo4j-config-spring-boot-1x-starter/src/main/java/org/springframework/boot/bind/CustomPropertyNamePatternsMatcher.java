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

package org.springframework.boot.bind;

import org.springframework.beans.PropertyValues;
import org.springframework.core.env.MutablePropertySources;

import java.util.Set;

/**
 * Custom property name patterns matcher.
 */
public class CustomPropertyNamePatternsMatcher {

    private static final char[] EXACT_DELIMITERS = {'_', '.', '['};

    /**
     * Get property sources property values.
     *
     * @param names
     * @param propertySources
     * @return
     */
    public static PropertyValues getPropertySourcesPropertyValues(Set<String> names, MutablePropertySources propertySources) {
        PropertyNamePatternsMatcher includes = getPropertyNamePatternsMatcher(names);
        return new PropertySourcesPropertyValues(propertySources, names, includes, true);
    }

    private static PropertyNamePatternsMatcher getPropertyNamePatternsMatcher(Set<String> names) {
        return new DefaultPropertyNamePatternsMatcher(EXACT_DELIMITERS, true, names);
    }
}
