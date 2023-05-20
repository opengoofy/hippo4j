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

import cn.hippo4j.rpc.client.ClientSupport;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.handler.ErrorServerHandler;
import cn.hippo4j.rpc.handler.HandlerManager;
import cn.hippo4j.rpc.connection.ServerConnection;
import cn.hippo4j.rpc.connection.SimpleServerConnection;
import io.netty.channel.ChannelHandler;

import java.io.IOException;

/**
 * This is a server-side build class that allows you to quickly prepare data on the server side and start the server side.<br>
 * <p>
 * The composite pattern is adopted, which means that it is itself a server-side implementation, so it is stateless.
 *
 * @see RPCServer
 * @see ServerConnection
 * @see ClientSupport
 * @since 2.0.0
 */
public class ServerSupport implements Server {

    /**
     * Extract the port number of the web container,
     * which is the port information exposed by the server
     */
    protected ServerPort serverPort;

    /**
     * ChannelHandler
     */
    protected HandlerManager<ChannelHandler> handlerManager;

    protected Server server;

    public ServerSupport(ServerPort serverPort) {
        this(serverPort, new SimpleServerConnection());
    }

    public ServerSupport(ServerPort serverPort, HandlerManager<ChannelHandler> handlerManager) {
        this.serverPort = serverPort;
        this.handlerManager = handlerManager;
        initServer();
    }

    /**
     * Initializes the entire server side, which includes interface registration, processors, and ports.<br>
     * Only interfaces are registered during registration. Classes and abstract classes are not registered.
     * If no processor is available, a default processor is provided
     */
    protected void initServer() {
        SimpleServerConnection connection = (handlerManager instanceof SimpleServerConnection)
                ? (SimpleServerConnection) handlerManager
                : new SimpleServerConnection();
        // Assign a default handler if no handler exists
        connection.addLast(null, new ErrorServerHandler());
        server = new RPCServer(connection, serverPort);
    }

    @Override
    public void bind() {
        server.bind();
    }

    @Override
    public boolean isActive() {
        return server.isActive();
    }

    @Override
    public void close() throws IOException {
        server.close();
    }
}
