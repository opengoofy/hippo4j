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

import cn.hippo4j.rpc.client.CallManager;
import cn.hippo4j.rpc.client.RandomPort;
import cn.hippo4j.rpc.connection.SimpleConnectPool;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.connection.SimpleServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import cn.hippo4j.rpc.connection.ServerConnection;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class SimpleConnectPoolTest {

    String host = "127.0.0.1";
    int maxCount = 64;
    int timeout = 5000;
    EventLoopGroup group = new NioEventLoopGroup();
    Class<? extends Channel> cls = NioSocketChannel.class;

    static ServerPort port = new TestServerPort();

    static final String take = "serverTake";
    static final String biTake = "biTake";
    static final String bareTake = "bareTake";
    static final String timeoutTake = "timeout";
    static RPCServer rpcServer;

    @BeforeClass
    public static void startServer() {
        CallManager manager = new CallManager();
        ServerTakeHandler<Integer, Integer> takeHandler = new ServerTakeHandler<>(biTake, manager::call);
        ServerBiTakeHandler<Integer, Integer, Integer> biTakeHandler = new ServerBiTakeHandler<>(take, manager::call);
        ServerBareTakeHandler<Integer> bareTakeHandler = new ServerBareTakeHandler<>(bareTake, manager::call);
        ServerBareTakeHandler<Integer> timeoutHandler = new ServerBareTakeHandler<>(timeoutTake, manager::callTestTimeout);
        ServerConnection connection = new SimpleServerConnection(takeHandler, bareTakeHandler, biTakeHandler, timeoutHandler);
        rpcServer = new RPCServer(connection, port);
        rpcServer.bind();
        while (!rpcServer.isActive()) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1L));
        }
    }

    @AfterClass
    public static void stopServer() throws IOException {
        if (rpcServer.isActive()) {
            rpcServer.close();
        }
    }

    @Test
    public void acquire() {
        InetSocketAddress address = InetSocketAddress.createUnresolved(host, port.getPort());
        ClientPoolHandler poolHandler = new ClientPoolHandler(new ClientTakeHandler());
        SimpleConnectPool pool = new SimpleConnectPool(address, maxCount, timeout, group, cls, poolHandler);
        Channel acquire = pool.acquire(timeout);
        Assert.assertNotNull(acquire);
        pool.release(acquire);
    }

    @Test
    public void testAcquire() {
        InetSocketAddress address = InetSocketAddress.createUnresolved(host, port.getPort());
        ClientPoolHandler poolHandler = new ClientPoolHandler(new ClientTakeHandler());
        SimpleConnectPool pool = new SimpleConnectPool(address, maxCount, timeout, group, cls, poolHandler);
        Future<Channel> acquire = pool.acquire();
        Assert.assertNotNull(acquire);
    }

    @Test
    public void close() {
        InetSocketAddress address = InetSocketAddress.createUnresolved(host, port.getPort());
        ClientPoolHandler poolHandler = new ClientPoolHandler(new ClientTakeHandler());
        SimpleConnectPool pool = new SimpleConnectPool(address, maxCount, timeout, group, cls, poolHandler);
        Channel acquire = pool.acquire(timeout);
        Assert.assertNotNull(acquire);
        pool.release(acquire);
        pool.close();
    }

    static class TestServerPort implements ServerPort {

        int port = RandomPort.getSafeRandomPort();

        @Override
        public int getPort() {
            return port;
        }
    }
}