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

import cn.hippo4j.common.web.exception.IllegalException;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

public class DefaultResponseTest {

    @Test
    public void testReadObject() throws IOException, ClassNotFoundException {
        String key = "name";
        Object o = "obj";
        Class<?> cls = String.class;
        Response response = new DefaultResponse(key, cls, o);
        byte[] bytes;
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(response);
            outputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        }
        Response response1;
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            response1 = (Response) objectInputStream.readObject();
        }
        Assert.assertEquals(response1.hashCode(), response.hashCode());
        Assert.assertEquals(key, response1.getKey());
        Assert.assertEquals(o, response1.getObj());
        Assert.assertEquals(cls, response1.getCls());
        Assert.assertEquals(response1, response);
        Assert.assertFalse(response1.isErr());
    }

    @Test
    public void testWriteObject() throws IOException, ClassNotFoundException {
        String key = "name";
        Throwable throwable = new IllegalException("test throwable");
        String errMsg = "test throwable";
        Response response = new DefaultResponse(key, throwable, errMsg);
        byte[] bytes;
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(response);
            outputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        }
        Response response1;
        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            response1 = (Response) objectInputStream.readObject();
        }
        Assert.assertEquals(key, response1.getKey());
        Assert.assertThrows(IllegalException.class, () -> {
            throw response1.getThrowable();
        });
        Assert.assertEquals(response1.hashCode(), response.hashCode());
        Assert.assertEquals(errMsg, response1.getErrMsg());
        Assert.assertEquals(response1, response);
        Assert.assertTrue(response1.isErr());
    }

}