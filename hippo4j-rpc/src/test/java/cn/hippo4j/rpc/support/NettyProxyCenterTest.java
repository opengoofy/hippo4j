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

import cn.hippo4j.common.web.exception.IllegalException;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.handler.NettyClientPoolHandler;
import cn.hippo4j.rpc.handler.NettyClientTakeHandler;
import org.junit.Assert;
import org.junit.Test;

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
    }

    @Test(expected = IllegalException.class)
    public void getProxyTest() {
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", port.getPort());
        NettyClientPoolHandler handler = new NettyClientPoolHandler(new NettyClientTakeHandler());
        ProxyClass localhost = NettyProxyCenter.getProxy(ProxyClass.class, address, handler);
        Assert.assertNotNull(localhost);
    }

    interface ProxyInterface {

        void hello();
    }

    static class ProxyClass {

    }

    static class TestServerPort implements ServerPort {

        @Override
        public int getPort() {
            return 8894;
        }
    }
}