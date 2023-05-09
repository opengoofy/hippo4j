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
import cn.hippo4j.common.web.exception.IllegalException;
import cn.hippo4j.rpc.client.Client;
import cn.hippo4j.rpc.client.NettyClientConnection;
import cn.hippo4j.rpc.client.RPCClient;
import cn.hippo4j.rpc.client.RandomPort;
import cn.hippo4j.rpc.discovery.ClassRegistry;
import cn.hippo4j.rpc.discovery.DefaultInstance;
import cn.hippo4j.rpc.discovery.Instance;
import cn.hippo4j.rpc.discovery.InstanceServerLoader;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.exception.ConnectionException;
import cn.hippo4j.rpc.handler.NettyClientPoolHandler;
import cn.hippo4j.rpc.handler.NettyClientTakeHandler;
import cn.hippo4j.rpc.handler.NettyServerTakeHandler;
import cn.hippo4j.rpc.server.NettyServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import io.netty.channel.pool.ChannelPoolHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

public class NettyProxyCenterTest {

    ServerPort port = new TestServerPort();

    @Test
    public void getProxy() {
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", port.getPort());
        NettyClientPoolHandler handler = new NettyClientPoolHandler(new NettyClientTakeHandler());
        ProxyInterface localhost = NettyProxyCenter.getProxy(ProxyInterface.class, address, handler);
        Assert.assertNotNull(localhost);
    }

    @Test
    public void createProxy() {
        ProxyInterface localhost = NettyProxyCenter.getProxy(ProxyInterface.class, "localhost:8894");
        Assert.assertNotNull(localhost);
        NettyProxyCenter.getProxy(ProxyInterface.class, "localhost:8894");
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved("localhost", 8894);
        Client client = NettyClientSupport.getClient(socketAddress);
        ProxyInterface proxy = NettyProxyCenter.createProxy(client, ProxyInterface.class, socketAddress);
        Assert.assertNotNull(proxy);
    }

    @Test(expected = ConnectionException.class)
    public void createProxyException() {
        NettyProxyCenter.getProxy(ProxyInterface.class, "localhost8894");
    }

    @Test
    public void removeProxy() {
        NettyProxyCenter.getProxy(ProxyInterface.class, "localhost:8894");
        NettyProxyCenter.removeProxy(ProxyInterface.class, "localhost:8894");
    }

    @Test(expected = ConnectionException.class)
    public void removeProxyException() {
        NettyProxyCenter.removeProxy(ProxyInterface.class, "localhost8894");
    }

    @Test(expected = IllegalException.class)
    public void getProxyTest() {
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", port.getPort());
        NettyClientPoolHandler handler = new NettyClientPoolHandler(new NettyClientTakeHandler());
        ProxyClass localhost = NettyProxyCenter.getProxy(ProxyClass.class, address, handler);
        Assert.assertNotNull(localhost);
    }

    @Test
    public void bindPipelineTest() throws IOException {
        // server
        Class<InstanceServerLoader> cls = InstanceServerLoader.class;
        ClassRegistry.put(cls.getName(), cls);
        ServerPort port = new TestServerPort();
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

    interface ProxyInterface {

        void hello();
    }

    static class ProxyClass {

    }

    static class TestServerPort implements ServerPort {

        int port = RandomPort.getSafeRandomPort();

        @Override
        public int getPort() {
            return port;
        }
    }
}