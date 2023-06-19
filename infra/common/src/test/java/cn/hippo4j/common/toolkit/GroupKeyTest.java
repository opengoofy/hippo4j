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

public class GroupKeyTest {

    @Test
    public void getKey() {
        String dataId = "dataId";
        String group = "group";
        String datumStr = "datumStr";
        String expected = "dataId+group+datumStr";
        String key = GroupKey.getKey(dataId, group, datumStr);
        Assert.isTrue(key.equals(expected));
    }

    @Test
    public void testGetKey() {
        String dataId = "dataId";
        String group = "group";
        String expected = "dataId+group";
        String key = GroupKey.getKey(dataId, group);
        Assert.isTrue(key.equals(expected));
    }

    @Test
    public void testGetKey1() {
        String[] strings = {"dataId", "group", "datumStr"};
        String expected = "dataId+group+datumStr";
        String key = GroupKey.getKey(strings);
        Assert.isTrue(key.equals(expected));
    }

    @Test
    public void getKeyTenant() {
        String dataId = "dataId";
        String group = "group";
        String datumStr = "datumStr";
        String expected = "dataId+group+datumStr";

        String keyTenant = GroupKey.getKeyTenant(dataId, group, datumStr);
        Assert.isTrue(keyTenant.equals(expected));
    }

    @Test
    public void parseKey() {
        String groupKey = "prescription+dynamic-threadpool-example+message-consume+12";
        String[] strings = GroupKey.parseKey(groupKey);
        Assert.isTrue(strings.length == 4);
    }

    @Test
    public void urlEncode() {
        String str = "hello+World%";
        String expected = "hello%2BWorld%25";
        StringBuilder stringBuilder = new StringBuilder();
        GroupKey.urlEncode(str, stringBuilder);
        Assert.isTrue(stringBuilder.toString().contains(expected));
    }
}