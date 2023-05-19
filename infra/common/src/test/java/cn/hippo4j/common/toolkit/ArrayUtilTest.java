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
import org.junit.Test;
import org.springframework.util.StringUtils;

/**
 * test {@link ArrayUtil}
 */
public class ArrayUtilTest {

    @Test
    public void assertIsEmpty() {
        String[] array = new String[0];
        Assert.isTrue(ArrayUtil.isEmpty(array));
    }

    @Test
    public void assertIsNotEmpty() {
        String[] array = new String[0];
        Assert.isTrue(!ArrayUtil.isNotEmpty(array));
    }

    @Test
    public void assertFirstMatch() {
        Matcher<String> matcher = (str) -> "1".equalsIgnoreCase(str);
        String[] array = new String[0];
        Assert.isTrue(StringUtils.isEmpty(ArrayUtil.firstMatch(matcher, array)));
        array = new String[]{"0"};
        Assert.isTrue(StringUtils.isEmpty(ArrayUtil.firstMatch(matcher, array)));
        array = new String[]{"1"};
        Assert.isTrue(!StringUtils.isEmpty(ArrayUtil.firstMatch(matcher, array)));
    }

    @Test
    public void assertAddAll() {
        String[] array = new String[]{"1"};
        Assert.isTrue(ArrayUtil.addAll(array, null).length == 1);
        Assert.isTrue(ArrayUtil.addAll(null, array).length == 1);
        Assert.isTrue(ArrayUtil.addAll(array, new String[]{"1"}).length == 2);
    }

    @Test
    public void assertClone() {
        Assert.isNull(ArrayUtil.clone(null));
        String[] array = new String[0];
        Assert.isTrue(array != ArrayUtil.clone(array));
        Assert.isTrue(array.length == ArrayUtil.clone(array).length);
    }
}
