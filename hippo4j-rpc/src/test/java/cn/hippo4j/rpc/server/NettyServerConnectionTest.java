package cn.hippo4j.rpc.server;

import cn.hippo4j.rpc.handler.TestHandler;
import org.junit.Assert;
import org.junit.Test;

public class NettyServerConnectionTest {

    @Test
    public void addLast() {
        NettyServerConnection connection = new NettyServerConnection();
        Assert.assertTrue(connection.isEmpty());
        connection.addLast(new TestHandler());
        Assert.assertFalse(connection.isEmpty());
    }

    @Test
    public void addFirst() {
        NettyServerConnection connection = new NettyServerConnection();
        Assert.assertTrue(connection.isEmpty());
        connection.addFirst(new TestHandler());
        Assert.assertFalse(connection.isEmpty());
    }

    @Test
    public void testAddLast() {
        NettyServerConnection connection = new NettyServerConnection();
        Assert.assertTrue(connection.isEmpty());
        connection.addLast("Test", new TestHandler());
        Assert.assertFalse(connection.isEmpty());
    }

    @Test
    public void testAddFirst() {
        NettyServerConnection connection = new NettyServerConnection();
        Assert.assertTrue(connection.isEmpty());
        connection.addFirst("Test", new TestHandler());
        Assert.assertFalse(connection.isEmpty());
    }
}