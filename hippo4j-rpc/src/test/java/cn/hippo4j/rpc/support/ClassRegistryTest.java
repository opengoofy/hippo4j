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

package cn.hippo4j.rpc.support;

import org.junit.Assert;
import org.junit.Test;

public class ClassRegistryTest {

    @Test
    public void get() {
        String getStr = "GetModel";
        Class<?> cls = ClassRegistry.get(getStr);
        Assert.assertNull(cls);
        ClassRegistry.put(getStr, GetModel.class);
        Class<?> aClass = ClassRegistry.get(getStr);
        Assert.assertNotNull(aClass);
        ClassRegistry.clear();
    }

    @Test
    public void set() {
        String getStr = "GetModel";
        ClassRegistry.set(getStr, GetModel.class);
        Class<?> aClass = ClassRegistry.get(getStr);
        Assert.assertEquals(aClass, GetModel.class);
        ClassRegistry.set(getStr, SetModel.class);
        Class<?> aClass1 = ClassRegistry.get(getStr);
        Assert.assertEquals(aClass1, GetModel.class);
        ClassRegistry.clear();
    }

    @Test
    public void put() {
        String getStr = "GetModel";
        ClassRegistry.put(getStr, GetModel.class);
        Class<?> aClass = ClassRegistry.get(getStr);
        Assert.assertEquals(aClass, GetModel.class);
        ClassRegistry.put(getStr, SetModel.class);
        Class<?> aClass1 = ClassRegistry.get(getStr);
        Assert.assertEquals(aClass1, SetModel.class);
        ClassRegistry.clear();
    }

    public static class GetModel {

    }

    public static class SetModel {

    }
}