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

package cn.hippo4j.rpc.client;

import cn.hippo4j.rpc.request.Request;
import cn.hippo4j.rpc.response.Response;

import java.io.IOException;

/**
 * The client, which provides a closing mechanism, maintains a persistent connection if not closed
 */
public class RPCClient implements Client {

    ClientConnection clientConnection;

    public RPCClient(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    @Override
    public Response connection(Request request) {
        return clientConnection.connect(request);
    }

    @Override
    public boolean isActive() {
        return clientConnection.isActive();
    }

    /**
     * Close the client and release all connections.
     *
     * @throws IOException exception
     */
    @Override
    public void close() throws IOException {
        clientConnection.close();
    }
}
