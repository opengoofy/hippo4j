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
import org.junit.Assert;

public class StringUtilTest {

    @Test
    public void replace() {
        String url = "http://localhost:8088/hippo4j_manager?";
        String replace = StringUtil.replace(url, "/hippo4j_manager?", "?");
        Assert.assertEquals(replace, "http://localhost:8088?");
    }

    /**
     * <p>Splits the provided text into an array, separators specified.
     *
     * <pre>
     * StringUtils.split(null, *)         = null
     * StringUtils.split("", *)           = []
     * StringUtils.split("abc def", null) = ["abc", "def"]
     * StringUtils.split("abc def", " ")  = ["abc", "def"]
     * StringUtils.split("ab:cd:ef", ":") = ["ab", "cd", "ef"]
     * </pre>
     */
    @Test
    public void split() {
        String str1 = null;
        String separator1 = "*";
        String[] res1 = StringUtil.split(str1, separator1);
        assert res1 == null;

        String str2 = "";
        String separator2 = "*";
        String[] res2 = StringUtil.split(str2, separator2);
        Assert.assertArrayEquals(res2, new String[0]);

        String str3 = "abc def";
        String separator3 = null;
        String[] res3 = StringUtil.split(str3, separator3);
        Assert.assertArrayEquals(res3, new String[]{"abc", "def"});

        String str4 = "abc def";
        String separator4 = " ";
        String[] res4 = StringUtil.split(str4, separator4);
        Assert.assertArrayEquals(res4, new String[]{"abc", "def"});

        String str5 = "ab:cd:ef";
        String separator5 = ":";
        String[] res5 = StringUtil.split(str5, separator5);
        Assert.assertArrayEquals(res5, new String[]{"ab", "cd", "ef"});

    }

    @Test
    public void assertIsEmpty() {
        String string = "";
        Assert.assertTrue(StringUtil.isEmpty(string));
    }

    @Test
    public void assertIsNotEmpty() {
        String string = "string";
        Assert.assertTrue(StringUtil.isNotEmpty(string));
    }

    @Test
    public void emptyToNull() {
        String string = "";
        Assert.assertNull(StringUtil.emptyToNull(string));
    }

    @Test
    public void nullToEmpty() {
        String string = "null";
        Assert.assertEquals("null", StringUtil.nullToEmpty(string));
    }

    @Test
    public void isNullOrEmpty() {
        String string = "null";
        Assert.assertFalse(StringUtil.isEmpty(string));
    }

    @Test
    public void isBlank() {
        String string = "";
        Assert.assertTrue(StringUtil.isBlank(string));
    }

    @Test
    public void isNotBlank() {
        String string = "null";
        Assert.assertTrue(StringUtil.isNotBlank(string));
    }

    @Test
    public void isAllNotEmpty() {
        String strings = "str";
        Assert.assertTrue(StringUtil.isAllNotEmpty(strings));
    }

    @Test
    public void hasEmpty() {
        String strings = "";
        Assert.assertTrue(StringUtil.hasEmpty(strings));
    }

    @Test
    public void toUnderlineCase() {
        String string = "str";
        String s = StringUtil.toUnderlineCase(string);
        Assert.assertEquals("str", s);
    }

    @Test
    public void toSymbolCase() {
        String string = "str";
        String s = StringUtil.toSymbolCase(string, StringUtil.UNDERLINE);
        Assert.assertEquals("str", s);
    }

    @Test
    public void toCamelCase() {
        String string = "str_str";
        String s = StringUtil.toCamelCase(string, StringUtil.UNDERLINE);
        Assert.assertEquals("strStr", s);
    }

    @Test
    public void newBuilder() {
        String s1 = StringUtil.newBuilder(null);
        Assert.assertEquals("", s1);
        String s2 = StringUtil.newBuilder("H", "ippo", "4j");
        Assert.assertEquals("Hippo4j", s2);
    }

    @Test
    public void createBuilder() {
        StringBuilder s1 = StringUtil.createBuilder(null);
        Assert.assertEquals("", s1.toString());
        StringBuilder s2 = StringUtil.createBuilder("H", "ippo", "4j");
        Assert.assertEquals("Hippo4j", s2.toString());
    }

    @Test
    public void appends() {
        StringBuilder sb1 = StringUtil.appends(null, "H", "ippo", "4j");
        Assert.assertEquals("Hippo4j", sb1.toString());
        StringBuilder sb2 = StringUtil.appends(StringUtil.createBuilder("To "), null);
        Assert.assertEquals("To ", sb2.toString());
        StringBuilder sb3 = StringUtil.appends(StringUtil.createBuilder("To "), "H", "ippo", "4j");
        Assert.assertEquals("To Hippo4j", sb3.toString());
    }
}
