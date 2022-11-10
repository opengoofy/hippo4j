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

package cn.hippo4j.rpc.model;

import cn.hippo4j.rpc.discovery.InstanceServerLoaderImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.lang.reflect.Method;

public class DefaultRequestTest {

    @Test
    public void testReadObject() throws IOException, ClassNotFoundException, NoSuchMethodException {
        String key = "name";
        String clsName = InstanceServerLoaderImpl.class.getName();
        Method method = InstanceServerLoaderImpl.class.getMethod("setName", String.class);
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] parameters = new Object[1];
        parameters[0] = "hippo4j";
        Request request = new DefaultRequest(key, clsName, methodName, parameterTypes, parameters);
        byte[] bytes;
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(request);
            outputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        }
        Request request1;
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            request1 = (Request) objectInputStream.readObject();
        }
        Assert.assertEquals(request1.hashCode(), request1.hashCode());
        Assert.assertEquals(key, request1.getKey());
        Assert.assertEquals(clsName, request1.getClassName());
        Assert.assertEquals(methodName, request1.getMethodName());
        Assert.assertArrayEquals(parameterTypes, request1.getParameterTypes());
        Assert.assertArrayEquals(parameters, request1.getParameters());
        Assert.assertEquals(request1, request);
    }

}