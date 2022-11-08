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

package cn.hippo4j.rpc.client;

import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.handler.AbstractNettyClientPoolHandler;
import cn.hippo4j.rpc.handler.NettyClientTakeHandler;
import cn.hippo4j.rpc.handler.NettyServerTakeHandler;
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import cn.hippo4j.rpc.server.AbstractNettyServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import cn.hippo4j.rpc.server.ServerConnection;
import cn.hippo4j.rpc.discovery.ClassRegistry;
import cn.hippo4j.rpc.discovery.DefaultInstance;
import cn.hippo4j.rpc.discovery.Instance;
import io.netty.channel.pool.ChannelPoolHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RPCClientTest {

    String host = "localhost";
    ServerPort port = new TestServerPort();
    ServerPort portTest = new TestPortServerPort();

    @Test
    public void connection() throws IOException {
        Class<CallManager> cls = CallManager.class;
        String className = cls.getName();
        ClassRegistry.put(className, cls);
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        NettyServerTakeHandler handler = new NettyServerTakeHandler(instance);
        ServerConnection connection = new AbstractNettyServerConnection(handler);
        RPCServer rpcServer = new RPCServer(connection, port);
        CompletableFuture.runAsync(rpcServer::bind);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ChannelPoolHandler channelPoolHandler = new AbstractNettyClientPoolHandler(new NettyClientTakeHandler());
        NettyClientConnection clientConnection = new NettyClientConnection(host, port, channelPoolHandler);
        RPCClient rpcClient = new RPCClient(clientConnection);
        Request request = new DefaultRequest("127.0.0.18888", className, "call", null, null);
        for (int i = 0; i < 100; i++) {
            Response response = rpcClient.connection(request);
            boolean active = rpcClient.isActive();
            Assert.assertTrue(active);
            Assert.assertEquals(response.getObj(), 1);
        }
        rpcClient.close();
        rpcServer.close();
    }

    /**
     * This test case can be overridden under the handler and coder packages
     */
    @Test
    public void connectionTest() throws IOException {
        Class<CallManager> cls = CallManager.class;
        String className = cls.getName();
        ClassRegistry.put(className, cls);
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        NettyServerTakeHandler handler = new NettyServerTakeHandler(instance);
        ServerConnection connection = new AbstractNettyServerConnection(handler);
        RPCServer rpcServer = new RPCServer(connection, portTest);
        CompletableFuture.runAsync(rpcServer::bind);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ChannelPoolHandler channelPoolHandler = new AbstractNettyClientPoolHandler(new NettyClientTakeHandler());
        NettyClientConnection clientConnection = new NettyClientConnection(host, portTest, channelPoolHandler);
        RPCClient rpcClient = new RPCClient(clientConnection);
        Class<?>[] classes = new Class<?>[2];
        classes[0] = Integer.class;
        classes[1] = Integer.class;
        Object[] objects = new Object[2];
        objects[0] = 1;
        objects[1] = 2;
        Request request = new DefaultRequest("127.0.0.18889", className, "callTest", classes, objects);
        Response response = rpcClient.connection(request);
        boolean active = rpcClient.isActive();
        Assert.assertTrue(active);
        Assert.assertEquals(response.getObj(), 3);
        rpcClient.close();
        rpcServer.close();
    }

    static class TestServerPort implements ServerPort {

        @Override
        public int getPort() {
            return 8888;
        }
    }

    static class TestPortServerPort implements ServerPort {

        @Override
        public int getPort() {
            return 8889;
        }
    }
}