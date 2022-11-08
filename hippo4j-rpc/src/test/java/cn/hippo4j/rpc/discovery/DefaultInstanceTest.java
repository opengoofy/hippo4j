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

package cn.hippo4j.rpc.discovery;

import cn.hippo4j.common.web.exception.IllegalException;
import org.junit.Assert;
import org.junit.Test;

public class DefaultInstanceTest {

    Instance instance = new DefaultInstance();

    @Test
    public void getInstance() {
        Class<InstanceModel> cls = InstanceModel.class;
        Object instanceInstance = instance.getInstance(cls);
        Assert.assertNotNull(instanceInstance);
        Assert.assertEquals(cls, instanceInstance.getClass());
    }

    @Test
    public void testGetInstance() {
        String className = "cn.hippo4j.rpc.discovery.InstanceModel";
        Object instanceInstance = instance.getInstance(className);
        Assert.assertNotNull(instanceInstance);
        Assert.assertEquals(className, instanceInstance.getClass().getName());
    }

    @Test(expected = IllegalException.class)
    public void testGetInstanceTest() {
        String className = "cn.hippo4j.rpc.discovery.InstanceModelTest";
        Object instanceInstance = instance.getInstance(className);
        Assert.assertNotNull(instanceInstance);
        Assert.assertEquals(className, instanceInstance.getClass().getName());
    }

    @Test
    public void getInstanceTest() {
        Class<InstanceServerLoader> cls = InstanceServerLoader.class;
        Object instanceInstance = instance.getInstance(cls);
        Assert.assertNotNull(instanceInstance);
        Assert.assertEquals(InstanceServerLoaderImpl.class, instanceInstance.getClass());
    }

}