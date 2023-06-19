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

package cn.hippo4j.core.toolkit;

import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class FileUtilTest {

    @Test
    public void assertReadUtf8String() {
        String testFilePath = "test/test_utf8.txt";
        String contentByFileUtil = FileUtil.readUtf8String(testFilePath);
        Assert.assertFalse(contentByFileUtil.isEmpty());
    }

    @Test
    public void assertReadUtf8String2() {
        String linebreaks = System.getProperty("line.separator");
        String testText = "abcd简体繁体\uD83D\uDE04\uD83D\uDD25& *" + linebreaks +
                "second line" + linebreaks +
                "empty line next" + linebreaks;
        String testFilePath = "test/test_utf8.txt";
        String contentByFileUtil = FileUtil.readUtf8String(testFilePath);
        Assert.assertTrue(testText.equals(contentByFileUtil));
    }

    @Test
    public void assertReadLines() {
        String testFilePath = "test/test_utf8.txt";
        List<String> readLines = FileUtil.readLines(testFilePath, StandardCharsets.UTF_8);
        Assert.assertEquals(3, readLines.size());
    }
}
