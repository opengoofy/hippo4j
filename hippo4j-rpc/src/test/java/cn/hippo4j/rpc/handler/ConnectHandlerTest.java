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
import cn.hippo4j.rpc.client.NettyClientConnection;
import cn.hippo4j.rpc.client.RPCClient;
import cn.hippo4j.rpc.discovery.*;
import cn.hippo4j.rpc.server.NettyServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import cn.hippo4j.rpc.support.NettyProxyCenter;
import io.netty.channel.pool.ChannelPoolHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ConnectHandlerTest {

    @Test
    public void handlerTest() throws IOException {
        // server
        Class<InstanceServerLoader> cls = InstanceServerLoader.class;
        ClassRegistry.put(cls.getName(), cls);
        ServerPort port = () -> 8892;
        Instance instance = new DefaultInstance();
        NettyServerTakeHandler serverHandler = new NettyServerTakeHandler(instance);
        NettyServerConnection connection = new NettyServerConnection(serverHandler);
        RPCServer rpcServer = new RPCServer(connection, port);
        rpcServer.bind();
        while (!rpcServer.isActive()) {
            ThreadUtil.sleep(100L);
        }
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", port.getPort());
        ChannelPoolHandler channelPoolHandler = new NettyClientPoolHandler(new NettyClientTakeHandler());
        NettyClientConnection clientConnection = new NettyClientConnection(address, channelPoolHandler);
        RPCClient rpcClient = new RPCClient(clientConnection);

        InstanceServerLoader loader = NettyProxyCenter.createProxy(rpcClient, cls, address);
        String name = loader.getName();
        Assert.assertEquals("name", name);
        rpcClient.close();
        rpcServer.close();
    }

}