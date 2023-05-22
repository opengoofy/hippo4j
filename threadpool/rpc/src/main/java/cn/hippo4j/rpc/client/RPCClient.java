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

import cn.hippo4j.rpc.connection.ClientConnection;
import cn.hippo4j.rpc.model.Request;

import java.io.IOException;

/**
 * The client, which provides a closing mechanism, maintains a persistent connection if not closed<br>
 * Delegate the method to the {@link ClientConnection} for implementation
 *
 * @since 2.0.0
 */
public class RPCClient implements Client {

    ClientConnection clientConnection;

    public RPCClient(ClientConnection clientConnection) {
        this.clientConnection = clientConnection;
    }

    @Override
    public <R> R connect(Request request) {
        return clientConnection.connect(request);
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
