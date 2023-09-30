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

import cn.hippo4j.rpc.client.RandomPort;
import cn.hippo4j.rpc.connection.ServerConnection;
import cn.hippo4j.rpc.connection.SimpleServerConnection;
import cn.hippo4j.rpc.handler.ServerTakeHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class RPCServerTest {

    static final String instance = "instance";

    @Test
    public void bind() throws IOException {
        ServerTakeHandler handler = new ServerTakeHandler(instance, o -> 1);
        ServerConnection connection = new SimpleServerConnection(handler);
        RPCServer rpcServer = new RPCServer(connection, RandomPort::getSafeRandomPort);
        rpcServer.bind();
        while (!rpcServer.isActive()) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1L));
        }
        boolean active = rpcServer.isActive();
        Assert.assertTrue(active);
        rpcServer.close();
    }

    @Test
    public void bindTest() throws IOException {
        EventLoopGroup leader = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerTakeHandler handler = new ServerTakeHandler(instance, o -> 1);
        ServerConnection connection = new SimpleServerConnection(leader, worker, handler);
        RPCServer rpcServer = new RPCServer(connection, RandomPort::getSafeRandomPort);
        rpcServer.bind();
        while (!rpcServer.isActive()) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1L));
        }
        boolean active = rpcServer.isActive();
        Assert.assertTrue(active);
        rpcServer.close();
    }

}