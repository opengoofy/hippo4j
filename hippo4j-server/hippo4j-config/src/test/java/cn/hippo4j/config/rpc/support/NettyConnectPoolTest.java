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

package cn.hippo4j.config.rpc.support;

import cn.hippo4j.config.rpc.server.NettyServerConnection;
import cn.hippo4j.config.rpc.server.RPCServer;
import cn.hippo4j.config.rpc.server.ServerConnection;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class NettyConnectPoolTest {

    String host = "127.0.0.1";
    int port = 8888;
    int maxCount = 64;
    int timeout = 5000;
    EventLoopGroup group = new NioEventLoopGroup();
    Class<? extends Channel> cls = NioSocketChannel.class;

    @Test
    public void acquire() throws IOException {
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        ServerConnection connection = new NettyServerConnection(instance);
        RPCServer rpcServer = new RPCServer(port, connection);
        CompletableFuture.runAsync(rpcServer::bind);
        // Given the delay in starting the server, wait here
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        NettyConnectPool pool = new NettyConnectPool(host, port, maxCount, timeout, group, cls);
        Channel acquire = pool.acquire(timeout);
        Assert.assertNotNull(acquire);
        pool.release(acquire);
        rpcServer.close();
    }

    @Test
    public void testAcquire() throws IOException {
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        ServerConnection connection = new NettyServerConnection(instance);
        RPCServer rpcServer = new RPCServer(port, connection);
        CompletableFuture.runAsync(rpcServer::bind);
        // Given the delay in starting the server, wait here
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        NettyConnectPool pool = new NettyConnectPool(host, port, maxCount, timeout, group, cls);
        Future<Channel> acquire = pool.acquire();
        Assert.assertNotNull(acquire);
        rpcServer.close();
    }

    @Test
    public void close() throws IOException {
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        ServerConnection connection = new NettyServerConnection(instance);
        RPCServer rpcServer = new RPCServer(port, connection);
        CompletableFuture.runAsync(rpcServer::bind);
        // Given the delay in starting the server, wait here
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        NettyConnectPool pool = new NettyConnectPool(host, port, maxCount, timeout, group, cls);
        Channel acquire = pool.acquire(timeout);
        Assert.assertNotNull(acquire);
        pool.release(acquire);
        pool.close();
        rpcServer.close();
    }
}