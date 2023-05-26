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

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Collections;

/**
 * test for {@link Assert}
 */
public final class AssertTest {

    @Test
    public void testIsTrue() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.isTrue(false));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Assert.isTrue(false, "test message"));
    }

    @Test
    public void testIsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.isNull(""));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Assert.isNull("", "object is null"));
    }

    @Test
    public void testNotNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.notNull(null));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Assert.notNull(null, "object is null"));
    }

    @Test
    public void testNotEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(Collections.emptyList()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty(Collections.emptyList(), "object is null"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(Collections.emptyMap()));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty(Collections.emptyMap(), "map is null"));
        Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.notEmpty(""));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty("", "string is null"));
    }

    @Test
    public void testNotBlank() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.notBlank(" "));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Assert.notBlank(" ", "string is null"));
    }

    @Test
    public void testHasText() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> Assert.hasText(" "));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Assert.hasText(" ", "text is null"));
    }
}
