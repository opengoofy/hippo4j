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

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DefaultResponseTest {

    static final String rid = "name";
    static final Object o = "obj";
    static final String errMsg = "test throwable";

    @Test
    public void testReadObject() throws IOException, ClassNotFoundException {
        Response response = new DefaultResponse(rid, o);
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
        Assert.assertEquals(rid, response1.getRID());
        Assert.assertEquals(o, response1.getObj());
        Assert.assertEquals(response1, response);
        Assert.assertFalse(response1.isErr());
    }

    @Test
    public void testWriteObject() throws IOException, ClassNotFoundException {
        Response response = new DefaultResponse(rid, errMsg);
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
        Assert.assertEquals(rid, response1.getRID());
        Assert.assertEquals(response1.hashCode(), response.hashCode());
        Assert.assertEquals(errMsg, response1.getErrMsg());
        Assert.assertEquals(response1, response);
        Assert.assertTrue(response1.isErr());
    }

    @Test
    public void testEquals() {
        Response response = new DefaultResponse(rid, o);
        Assert.assertTrue(response.equals(response));
        Assert.assertFalse(response.equals(null));
    }
}