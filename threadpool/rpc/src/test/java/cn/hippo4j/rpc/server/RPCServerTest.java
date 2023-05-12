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

package cn.hippo4j.rpc.server;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.rpc.client.CallManager;
import cn.hippo4j.rpc.client.ClientConnection;
import cn.hippo4j.rpc.client.NettyClientConnection;
import cn.hippo4j.rpc.client.RPCClient;
import cn.hippo4j.rpc.client.RandomPort;
import cn.hippo4j.rpc.discovery.ClassRegistry;
import cn.hippo4j.rpc.discovery.DefaultInstance;
import cn.hippo4j.rpc.discovery.Instance;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.handler.NettyClientPoolHandler;
import cn.hippo4j.rpc.handler.NettyClientTakeHandler;
import cn.hippo4j.rpc.handler.NettyServerTakeHandler;
import cn.hippo4j.rpc.handler.TestHandler;
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RPCServerTest {

    @Test
    public void bind() throws IOException {
        Instance instance = new DefaultInstance();
        NettyServerTakeHandler handler = new NettyServerTakeHandler(instance);
        ServerConnection connection = new NettyServerConnection(handler);
        RPCServer rpcServer = new RPCServer(connection, RandomPort::getSafeRandomPort);
        rpcServer.bind();
        while (!rpcServer.isActive()) {
            ThreadUtil.sleep(100L);
        }
        boolean active = rpcServer.isActive();
        Assert.assertTrue(active);
        rpcServer.close();
    }

    @Test
    public void bindTest() throws IOException {
        Instance instance = new DefaultInstance();
        EventLoopGroup leader = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        NettyServerTakeHandler handler = new NettyServerTakeHandler(instance);
        ServerConnection connection = new NettyServerConnection(leader, worker, handler);
        RPCServer rpcServer = new RPCServer(connection, RandomPort::getSafeRandomPort);
        rpcServer.bind();
        while (!rpcServer.isActive()) {
            ThreadUtil.sleep(100L);
        }
        boolean active = rpcServer.isActive();
        Assert.assertTrue(active);
        rpcServer.close();
    }

    @Test
    public void bindPipelineTest() throws IOException {
        ServerPort serverPort = new ServerPort() {

            final int port = RandomPort.getSafeRandomPort();

            @Override
            public int getPort() {
                return port;
            }
        };
        Class<CallManager> cls = CallManager.class;
        String className = cls.getName();
        ClassRegistry.put(className, cls);
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        NettyServerTakeHandler handler = new NettyServerTakeHandler(instance);
        NettyServerConnection connection = new NettyServerConnection(handler);
        connection.addLast("Test", new TestHandler());
        RPCServer rpcServer = new RPCServer(connection, serverPort);
        rpcServer.bind();
        while (!rpcServer.isActive()) {
            ThreadUtil.sleep(100L);
        }
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", serverPort.getPort());
        ChannelPoolHandler channelPoolHandler = new NettyClientPoolHandler(new NettyClientTakeHandler());
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

    @Test
    public void bindNegativeTest() {
        ServerPort serverPort = () -> -1;
        Class<CallManager> cls = CallManager.class;
        String className = cls.getName();
        ClassRegistry.put(className, cls);
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        NettyServerTakeHandler handler = new NettyServerTakeHandler(instance);
        NettyServerConnection connection = new NettyServerConnection(handler);
        RPCServer rpcServer = new RPCServer(connection, serverPort);
        rpcServer.bind();
    }
}