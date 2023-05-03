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

package cn.hippo4j.rpc.handler;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.common.web.exception.IllegalException;
import cn.hippo4j.rpc.client.*;
import cn.hippo4j.rpc.discovery.ClassRegistry;
import cn.hippo4j.rpc.discovery.DefaultInstance;
import cn.hippo4j.rpc.discovery.Instance;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import cn.hippo4j.rpc.server.NettyServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import cn.hippo4j.rpc.server.ServerConnection;
import io.netty.channel.ChannelHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class NettyClientPoolHandlerTest {

    @Test
    public void testGetHandlerEntity() {
        TestHandler handler = new TestHandler();
        long order = 0;
        String name = "Test";
        NettyClientPoolHandler poolHandler = new NettyClientPoolHandler();
        HandlerManager.HandlerEntity<ChannelHandler> entity = poolHandler.getHandlerEntity(order, handler, name);
        Assert.assertEquals(entity.getName(), name);
        Assert.assertEquals(entity.getOrder(), order);
        Assert.assertEquals(entity.getHandler(), handler);
    }

    @Test
    public void testCompareTo() {
        TestHandler handler = new TestHandler();
        long order = 0;
        String name = "Test";
        TestHandler handler1 = new TestHandler();
        long order1 = 1;
        String name1 = "Test1";
        NettyClientPoolHandler poolHandler = new NettyClientPoolHandler();
        HandlerManager.HandlerEntity<ChannelHandler> entity = poolHandler.getHandlerEntity(order, handler, name);
        HandlerManager.HandlerEntity<ChannelHandler> entity1 = poolHandler.getHandlerEntity(order1, handler1, name1);
        int compare = entity.compareTo(entity1);
        Assert.assertTrue(compare < 0);
    }

    @Test
    public void addLast() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addLast(null, new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void addFirst() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addFirst(null, new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void testAddLast() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addLast("Test", new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test
    public void testAddFirst() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler();
        Assert.assertTrue(handler.isEmpty());
        handler.addFirst("Test", new TestHandler());
        Assert.assertFalse(handler.isEmpty());
    }

    @Test(expected = IllegalException.class)
    public void testGetHandlerEntityFalse() {
        TestFalseHandler handler = new TestFalseHandler();
        long order = 0;
        String name = "Test";
        NettyClientPoolHandler poolHandler = new NettyClientPoolHandler();
        poolHandler.getHandlerEntity(order, handler, name);
    }

    @Test
    public void connectionTest() throws IOException {
        ServerPort port = new ServerPort() {

            final int a = RandomPort.getSafeRandomPort();

            @Override
            public int getPort() {
                return a;
            }
        };
        Class<CallManager> cls = CallManager.class;
        String className = cls.getName();
        ClassRegistry.put(className, cls);
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        NettyServerTakeHandler handler = new NettyServerTakeHandler(instance);
        ServerConnection connection = new NettyServerConnection(handler);
        RPCServer rpcServer = new RPCServer(connection, port);
        rpcServer.bind();
        while (!rpcServer.isActive()) {
            ThreadUtil.sleep(100L);
        }
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", port.getPort());
        List<ChannelHandler> handlers = new ArrayList<>();
        handlers.add(new NettyClientTakeHandler());
        NettyClientPoolHandler channelPoolHandler = new NettyClientPoolHandler(handlers);
        channelPoolHandler.addLast("test", new TestHandler());
        ClientConnection clientConnection = new NettyClientConnection(address, channelPoolHandler);
        RPCClient rpcClient = new RPCClient(clientConnection);
        Request request = new DefaultRequest("127.0.0.18888", className, "call", null, null);
        for (int i = 0; i < 50; i++) {
            Response response = rpcClient.connection(request);
            boolean active = rpcClient.isActive();
            Assert.assertTrue(active);
            Assert.assertEquals(response.getObj(), 1);
        }
        rpcClient.close();
        rpcServer.close();
    }
}