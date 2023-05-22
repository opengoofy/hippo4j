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
import cn.hippo4j.rpc.connection.ServerConnection;
import cn.hippo4j.rpc.client.RandomPort;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.DefaultResponse;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import cn.hippo4j.rpc.connection.SimpleServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ConnectHandlerTest {

    static final String take = "serverTake";
    static final String biTake = "biTake";
    static final String bareTake = "bareTake";
    static final String timeout = "timeout";
    static final String key = "key";
    static final String test = "test";
    static RPCServer rpcServer;
    static ServerPort port = new TestServerPort();

    @BeforeClass
    public static void startServer() {
        CallManager manager = new CallManager();
        ServerTakeHandler<Integer, Integer> takeHandler = new ServerTakeHandler<>(biTake, manager::call);
        ServerBiTakeHandler<Integer, Integer, Integer> biTakeHandler = new ServerBiTakeHandler<>(take, manager::call);
        ServerBareTakeHandler<Integer> bareTakeHandler = new ServerBareTakeHandler<>(bareTake, manager::call);
        ServerBareTakeHandler<Integer> timeoutHandler = new ServerBareTakeHandler<>(timeout, manager::callTestTimeout);
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
    public void testConnectHandlerDefault() {
        ConnectHandler handler = new TestConnectHandler();
        Request request = new DefaultRequest(key, take, new Object[0]);
        Response response = handler.sendHandler(request);
        Assert.assertNull(response);
        Response response1 = new DefaultResponse(key, test);
        String key = response1.getRID();
        Object obj = response1.getObj();
        handler.handler(response1);
        Assert.assertEquals(key, response1.getRID());
        Assert.assertEquals(obj, response1.getObj());
    }

    static class TestConnectHandler implements ConnectHandler {

    }

    static class TestServerPort implements ServerPort {

        int port = RandomPort.getSafeRandomPort();

        @Override
        public int getPort() {
            return port;
        }
    }

}