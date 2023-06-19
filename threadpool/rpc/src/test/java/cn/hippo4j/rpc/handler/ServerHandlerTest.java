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
import cn.hippo4j.rpc.client.ClientSupport;
import cn.hippo4j.rpc.client.RPCClient;
import cn.hippo4j.rpc.client.RandomPort;
import cn.hippo4j.rpc.connection.ServerConnection;
import cn.hippo4j.rpc.connection.SimpleClientConnection;
import cn.hippo4j.rpc.connection.SimpleServerConnection;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.exception.ConnectionException;
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.server.RPCServer;
import cn.hippo4j.rpc.support.AddressUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class ServerHandlerTest {

    static final String host = "localhost";
    static ServerPort port = new TestServerPort();

    static final String take = "serverTakeServer";
    static final String biTake = "biTakeServer";
    static final String bareTake = "bareTakeServer";
    static final String timeout = "timeoutServer";
    static final String error = "errorServer";
    static RPCServer rpcServer;

    @BeforeClass
    public static void startServer() {
        CallManager manager = new CallManager();
        ServerTakeHandler<Integer, Integer> takeHandler = new ServerTakeHandler<>(take, manager::call);
        ServerBiTakeHandler<Integer, Integer, Integer> biTakeHandler = new ServerBiTakeHandler<>(biTake, manager::call);
        ServerBareTakeHandler<Integer> bareTakeHandler = new ServerBareTakeHandler<>(bareTake, manager::call);
        ServerBareTakeHandler<Integer> timeoutHandler = new ServerBareTakeHandler<>(timeout, manager::callTestTimeout);
        ErrorServerHandler error = new ErrorServerHandler();
        ServerConnection connection = new SimpleServerConnection(takeHandler, bareTakeHandler, biTakeHandler, timeoutHandler, error);
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

    /**
     * This test case can be overridden under the handler and coder packages
     */
    @Test
    public void connection() {
        String s = host + ":" + port.getPort();
        int send = ClientSupport.clientSend(s, take, 1);
        Assert.assertEquals(send, 1);
        InetSocketAddress socketAddress = AddressUtil.getInetAddress(s);
        ClientSupport.closeClient(socketAddress);
    }

    @Test(expected = ConnectionException.class)
    public void connectionError() {
        String s = host + ":" + port.getPort();
        int send = ClientSupport.clientSend(s, error, 1);
        Assert.assertEquals(1, send);
        InetSocketAddress socketAddress = AddressUtil.getInetAddress(s);
        ClientSupport.closeClient(socketAddress);
    }

    @Test
    public void connectionTest() {
        Integer[] params = {1, 6};
        String s = host + ":" + port.getPort();
        int send = ClientSupport.clientSend(s, biTake, params);
        Assert.assertEquals(7, send);
        InetSocketAddress socketAddress = AddressUtil.getInetAddress(s);
        ClientSupport.closeClient(socketAddress);
    }

    @Test
    public void connectionTestBare() {
        String s = host + ":" + port.getPort();
        int send = ClientSupport.clientSend(s, bareTake);
        Assert.assertEquals(1, send);
        InetSocketAddress socketAddress = AddressUtil.getInetAddress(s);
        ClientSupport.closeClient(socketAddress);
    }

    @Test(expected = Exception.class)
    public void responseNullExceptionTest() {
        String s = host + ":" + port.getPort();
        ClientPoolHandler handler = new ClientPoolHandler(new ClientTakeHandler());
        InetSocketAddress socketAddress = AddressUtil.getInetAddress(s);
        SimpleClientConnection connection = new SimpleClientConnection(socketAddress, handler);
        connection.setTimeout(1L);
        RPCClient client = new RPCClient(connection);
        Request request = new DefaultRequest(UUID.randomUUID().toString(), timeout);
        client.connect(request);
    }

    static class TestServerPort implements ServerPort {

        int port = RandomPort.getSafeRandomPort();

        @Override
        public int getPort() {
            return port;
        }
    }

}