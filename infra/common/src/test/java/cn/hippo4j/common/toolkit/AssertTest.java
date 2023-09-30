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

import java.util.Collections;

import org.junit.Test;

/**
 * test {@link Assert}
 */
public final class AssertTest {

    @Test(expected = IllegalArgumentException.class)
    public void assertIsTrue() {
        Assert.isTrue(false, "test message");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIsTrueAndMessageIsNull() {
        Assert.isTrue(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIsNullAndMessageIsNull() {
        Assert.isNull("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertIsNull() {
        Assert.isNull("", "object is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotNull() {
        Assert.notNull(null, "object is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotNullAndMessageIsNull() {
        Assert.notNull(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotEmptyByList() {
        Assert.notEmpty(Collections.emptyList(), "object is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotEmptyByListAndMessageIsNull() {
        Assert.notEmpty(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotEmptyByMap() {
        Assert.notEmpty(Collections.emptyMap(), "map is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotEmptyByMapAndMessageIsNull() {
        Assert.notEmpty(Collections.emptyMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotEmptyByString() {
        Assert.notEmpty("", "string is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotEmptyByStringAndMessageIsNull() {
        Assert.notEmpty("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotBlankByString() {
        Assert.notBlank(" ", "string is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertNotBlankByStringAndMessageIsNull() {
        Assert.notBlank(" ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertHasText() {
        Assert.hasText(" ", "text is null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertHasTextAndMessageIsNull() {
        Assert.hasText(" ");
    }

}
