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
import cn.hippo4j.rpc.discovery.*;
import cn.hippo4j.rpc.handler.NettyClientPoolHandler;
import cn.hippo4j.rpc.handler.NettyClientTakeHandler;
import cn.hippo4j.rpc.handler.NettyServerTakeHandler;
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import cn.hippo4j.rpc.server.NettyServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import cn.hippo4j.rpc.server.ServerConnection;
import io.netty.channel.pool.ChannelPoolHandler;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class NettyServerSupportTest {

    @Test
    public void bind() throws IOException {
        NettyServerSupport support = new NettyServerSupport(RandomPort::getSafeRandomPort, InstanceServerLoader.class);
        support.bind();
        while (!support.isActive()) {
            ThreadUtil.sleep(100L);
        }
        Assert.assertTrue(support.isActive());
        support.close();
    }

    @Test
    public void bindTest() throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        classes.add(InstanceServerLoader.class);
        NettyServerSupport support = new NettyServerSupport(RandomPort::getSafeRandomPort, classes);
        support.bind();
        while (!support.isActive()) {
            ThreadUtil.sleep(100L);
        }
        Assert.assertTrue(support.isActive());
        support.close();
    }

}