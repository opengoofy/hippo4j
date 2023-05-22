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

import cn.hippo4j.rpc.client.RandomPort;
import cn.hippo4j.rpc.connection.ConnectPoolHolder;
import cn.hippo4j.rpc.connection.SimpleConnectPool;
import cn.hippo4j.rpc.discovery.ServerPort;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetSocketAddress;

public class ConnectPoolHolderTest {

    String host = "127.0.0.1";
    ServerPort port = new TestServerPort();
    int maxCount = 8;
    int timeout = 5000;
    EventLoopGroup group = new NioEventLoopGroup();
    Class<? extends Channel> cls = NioSocketChannel.class;

    @Test
    public void createPool() {
        ClientPoolHandler handler = new ClientPoolHandler(new ClientTakeHandler());
        InetSocketAddress address = InetSocketAddress.createUnresolved(host, port.getPort());
        SimpleConnectPool pool = new SimpleConnectPool(address, maxCount, timeout, group, cls, handler);
        SimpleConnectPool connectPool = ConnectPoolHolder.getPool(address);
        Assert.assertEquals(pool, connectPool);
        ConnectPoolHolder.clear();
        SimpleConnectPool connectPool1 = ConnectPoolHolder.getPool(address);
        Assert.assertNull(connectPool1);
    }

    @Test
    public void testGetPool() {
        ClientPoolHandler handler = new ClientPoolHandler(new ClientTakeHandler());
        InetSocketAddress address = InetSocketAddress.createUnresolved(host, port.getPort());
        SimpleConnectPool connectPool = ConnectPoolHolder.getPool(address, timeout, group, handler);
        SimpleConnectPool connectPool1 = ConnectPoolHolder.getPool(address);
        Assert.assertEquals(connectPool1, connectPool);
        ConnectPoolHolder.clear();
        SimpleConnectPool connectPool2 = ConnectPoolHolder.getPool(address);
        Assert.assertNull(connectPool2);
    }

    @Test
    public void remove() {
        ClientPoolHandler handler = new ClientPoolHandler(new ClientTakeHandler());
        InetSocketAddress address = InetSocketAddress.createUnresolved(host, port.getPort());
        SimpleConnectPool connectPool = ConnectPoolHolder.getPool(address, timeout, group, handler);
        SimpleConnectPool connectPool1 = ConnectPoolHolder.getPool(address);
        Assert.assertEquals(connectPool1, connectPool);
        ConnectPoolHolder.remove(address);
        SimpleConnectPool connectPool2 = ConnectPoolHolder.getPool(address);
        Assert.assertNull(connectPool2);
    }

    static class TestServerPort implements ServerPort {

        @Override
        public int getPort() {
            return RandomPort.getSafeRandomPort();
        }
    }
}