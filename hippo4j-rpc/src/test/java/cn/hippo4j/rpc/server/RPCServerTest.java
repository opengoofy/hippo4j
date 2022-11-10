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

import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.handler.NettyServerTakeHandler;
import cn.hippo4j.rpc.discovery.DefaultInstance;
import cn.hippo4j.rpc.discovery.Instance;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RPCServerTest {

    public static ServerPort port = new TestServerPort();

    @Test
    public void bind() throws IOException {
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
        boolean active = rpcServer.isActive();
        Assert.assertTrue(active);
        rpcServer.close();
        boolean serverActive = rpcServer.isActive();
        Assert.assertFalse(serverActive);
    }

    @Test
    public void bindTest() throws IOException {
        Instance instance = new DefaultInstance();
        EventLoopGroup leader = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        NettyServerTakeHandler handler = new NettyServerTakeHandler(instance);
        ServerConnection connection = new AbstractNettyServerConnection(leader, worker, handler);
        RPCServer rpcServer = new RPCServer(connection, port);
        CompletableFuture.runAsync(rpcServer::bind);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        rpcServer.close();
    }

    static class TestServerPort implements ServerPort {

        @Override
        public int getPort() {
            return 8888;
        }
    }
}