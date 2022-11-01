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

package cn.hippo4j.config.rpc.client;

import cn.hippo4j.config.rpc.request.DefaultRequest;
import cn.hippo4j.config.rpc.request.Request;
import cn.hippo4j.config.rpc.response.Response;
import cn.hippo4j.config.rpc.server.NettyServerConnection;
import cn.hippo4j.config.rpc.server.RPCServer;
import cn.hippo4j.config.rpc.server.ServerConnection;
import cn.hippo4j.config.rpc.support.DefaultInstance;
import cn.hippo4j.config.rpc.support.Instance;
import cn.hippo4j.config.rpc.support.ClassRegistry;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RPCClientTest {

    String host = "localhost";
    int port = 8888;

    @Test
    public void connection() throws IOException {

        Class<CallManager> cls = CallManager.class;
        String className = cls.getName();
        ClassRegistry.put(className, cls);
        // The mode connection was denied when the server was started on the specified port
        Instance instance = new DefaultInstance();
        ServerConnection connection = new NettyServerConnection(instance);
        RPCServer rpcServer = new RPCServer(port, connection);
        CompletableFuture.runAsync(rpcServer::bind);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        NettyClientConnection clientConnection = new NettyClientConnection(host, port);
        RPCClient rpcClient = new RPCClient(clientConnection);
        Request request = new DefaultRequest("127.0.0.18888", className, "call", null, null);
        for (int i = 0; i < 100; i++) {
            Response response = rpcClient.connection(request);
            Assert.assertEquals(response.getObj(), 1);
        }
        rpcClient.close();
        rpcServer.close();
    }

}