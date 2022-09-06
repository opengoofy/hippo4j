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

import java.util.Objects;

public class StringUtilTest {

    @Test
    public void assertIsEmpty() {
        String string = "";
        Assert.isTrue(StringUtil.isEmpty(string));
    }

    @Test
    public void assertIsNotEmpty() {
        String string = "string";
        Assert.isTrue(StringUtil.isNotEmpty(string));
    }

    @Test
    public void emptyToNull() {
        String string = "";
        Assert.isNull(StringUtil.emptyToNull(string));
    }

    @Test
    public void nullToEmpty() {
        String string = "null";
        Assert.notEmpty(StringUtil.nullToEmpty(string));
    }

    @Test
    public void isNullOrEmpty() {
        String string = "null";
        Assert.isTrue(!StringUtil.isNullOrEmpty(string));
    }

    @Test
    public void isBlank() {
        String string = "";
        Assert.isTrue(StringUtil.isBlank(string));
    }

    @Test
    public void isNotBlank() {
        String string = "null";
        Assert.isTrue(StringUtil.isNotBlank(string));
    }

    @Test
    public void isAllNotEmpty() {
        String strings = "str";
        Assert.isTrue(StringUtil.isAllNotEmpty(strings));
    }

    @Test
    public void hasEmpty() {
        String strings = "";
        Assert.isTrue(StringUtil.hasEmpty(strings));
    }

    @Test
    public void toUnderlineCase() {
        String string = "str";
        String s = StringUtil.toUnderlineCase(string);
        Assert.isTrue(Objects.equals(s, "str"));
    }

    @Test
    public void toSymbolCase() {
        String string = "str";
        String s = StringUtil.toSymbolCase(string, StringUtil.UNDERLINE);
        Assert.isTrue(Objects.equals(s, "str"));
    }
}
