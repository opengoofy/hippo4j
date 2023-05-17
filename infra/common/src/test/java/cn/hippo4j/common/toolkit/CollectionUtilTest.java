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

import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CollectionUtilTest {

    @Test
    public void assertGetFirst() {
        Assert.isNull(CollectionUtil.getFirst(null));
        String first = CollectionUtil.getFirst(Lists.newArrayList("1", "2"));
        Assert.notEmpty(first);
    }

    @Test
    public void assertIsEmpty() {
        List list = null;
        Assert.isTrue(CollectionUtil.isEmpty(list));
        list = Lists.newArrayList();
        Assert.isTrue(CollectionUtil.isEmpty(list));
        list = Lists.newArrayList("1");
        Assert.isTrue(!CollectionUtil.isEmpty(list));
        Map map = null;
        Assert.isTrue(CollectionUtil.isEmpty(map));
        map = new HashMap<>();
        Assert.isTrue(CollectionUtil.isEmpty(map));
        map.put("key", "value");
        Assert.isTrue(!CollectionUtil.isEmpty(map));
        Iterator iterator = null;
        Assert.isTrue(CollectionUtil.isEmpty(iterator));
        iterator = Lists.emptyList().iterator();
        Assert.isTrue(CollectionUtil.isEmpty(iterator));
        iterator = Lists.newArrayList("1").iterator();
        Assert.isTrue(!CollectionUtil.isEmpty(iterator));
    }

    @Test
    public void assertIsNotEmpty() {
        List list = null;
        Assert.isTrue(!CollectionUtil.isNotEmpty(list));
        list = Lists.newArrayList();
        Assert.isTrue(!CollectionUtil.isNotEmpty(list));
        list = Lists.newArrayList("1");
        Assert.isTrue(CollectionUtil.isNotEmpty(list));
        Map map = null;
        Assert.isTrue(!CollectionUtil.isNotEmpty(map));
        map = new HashMap<>();
        Assert.isTrue(!CollectionUtil.isNotEmpty(map));
        map.put("key", "value");
        Assert.isTrue(CollectionUtil.isNotEmpty(map));
        Iterator iterator = null;
        Assert.isTrue(!CollectionUtil.isNotEmpty(iterator));
        iterator = Lists.emptyList().iterator();
        Assert.isTrue(!CollectionUtil.isNotEmpty(iterator));
        iterator = Lists.newArrayList("1").iterator();
        Assert.isTrue(CollectionUtil.isNotEmpty(iterator));
    }
}
