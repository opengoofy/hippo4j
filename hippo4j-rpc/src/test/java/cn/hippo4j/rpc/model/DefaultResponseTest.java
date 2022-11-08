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
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(response);
            outputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        }
        Response response1;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
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
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            outputStream.writeObject(response);
            outputStream.flush();
            bytes = byteArrayOutputStream.toByteArray();
        }
        Response response1;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
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