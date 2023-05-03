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

package cn.hippo4j.rpc.support;

import cn.hippo4j.common.toolkit.ThreadUtil;
import cn.hippo4j.rpc.client.*;
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
import cn.hippo4j.rpc.server.NettyServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import io.netty.channel.pool.ChannelPoolHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NettyClientSupportTest {

    @Test
    public void closeTest() throws IOException {
        int port = RandomPort.getSafeRandomPort();
        ServerPort serverPort = () -> port;
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
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", port);
        ChannelPoolHandler channelPoolHandler = new NettyClientPoolHandler(new NettyClientTakeHandler());
        ClientConnection clientConnection = new NettyClientConnection(address, channelPoolHandler);
        RPCClient rpcClient = new RPCClient(clientConnection);

        NettyClientSupport.closeClient(new InetSocketAddress("localhost", port));

        rpcClient.close();
        rpcServer.close();
    }

}
