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

import cn.hippo4j.rpc.handler.NettyClientPoolHandler;
import cn.hippo4j.rpc.handler.NettyClientTakeHandler;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Assert;
import org.junit.Test;

public class NettyConnectPoolHolderTest {

    String host = "127.0.0.1";
    int port = 8888;
    int maxCount = 8;
    int timeout = 5000;
    EventLoopGroup group = new NioEventLoopGroup();
    Class<? extends Channel> cls = NioSocketChannel.class;

    @Test
    public void createPool() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler(new NettyClientTakeHandler());
        NettyConnectPool pool = new NettyConnectPool(host, port, maxCount, timeout, group, cls, handler);
        NettyConnectPool connectPool = NettyConnectPoolHolder.getPool(host, port);
        Assert.assertEquals(pool, connectPool);
        NettyConnectPoolHolder.clear();
        NettyConnectPool connectPool1 = NettyConnectPoolHolder.getPool(host, port);
        Assert.assertNull(connectPool1);
    }

    @Test
    public void testGetPool() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler(new NettyClientTakeHandler());
        NettyConnectPool connectPool = NettyConnectPoolHolder.getPool(host, port, timeout, group, handler);
        NettyConnectPool connectPool1 = NettyConnectPoolHolder.getPool(host, port);
        Assert.assertEquals(connectPool1, connectPool);
        NettyConnectPoolHolder.clear();
        NettyConnectPool connectPool2 = NettyConnectPoolHolder.getPool(host, port);
        Assert.assertNull(connectPool2);
    }

    @Test
    public void remove() {
        NettyClientPoolHandler handler = new NettyClientPoolHandler(new NettyClientTakeHandler());
        NettyConnectPool connectPool = NettyConnectPoolHolder.getPool(host, port, timeout, group, handler);
        NettyConnectPool connectPool1 = NettyConnectPoolHolder.getPool(host, port);
        Assert.assertEquals(connectPool1, connectPool);
        NettyConnectPoolHolder.remove(host, port);
        NettyConnectPool connectPool2 = NettyConnectPoolHolder.getPool(host, port);
        Assert.assertNull(connectPool2);
    }
}