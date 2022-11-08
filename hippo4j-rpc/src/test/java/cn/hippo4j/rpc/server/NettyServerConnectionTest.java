package cn.hippo4j.rpc.server;

import cn.hippo4j.rpc.handler.TestHandler;
import org.junit.Assert;
import org.junit.Test;

public class NettyServerConnectionTest {

    @Test
    public void addLast() {
        AbstractNettyServerConnection connection = new AbstractNettyServerConnection();
        Assert.assertTrue(connection.isEmpty());
        connection.addLast(new TestHandler());
        Assert.assertFalse(connection.isEmpty());
    }

    @Test
    public void addFirst() {
        AbstractNettyServerConnection connection = new AbstractNettyServerConnection();
        Assert.assertTrue(connection.isEmpty());
        connection.addFirst(new TestHandler());
        Assert.assertFalse(connection.isEmpty());
    }

    @Test
    public void testAddLast() {
        AbstractNettyServerConnection connection = new AbstractNettyServerConnection();
        Assert.assertTrue(connection.isEmpty());
        connection.addLast("Test", new TestHandler());
        Assert.assertFalse(connection.isEmpty());
    }

    @Test
    public void testAddFirst() {
        AbstractNettyServerConnection connection = new AbstractNettyServerConnection();
        Assert.assertTrue(connection.isEmpty());
        connection.addFirst("Test", new TestHandler());
        Assert.assertFalse(connection.isEmpty());
    }
}