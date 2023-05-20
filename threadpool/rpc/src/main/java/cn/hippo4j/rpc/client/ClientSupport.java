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
import cn.hippo4j.rpc.connection.SimpleClientConnection;
import cn.hippo4j.rpc.exception.OperationException;
import cn.hippo4j.rpc.handler.ErrorClientHandler;
import cn.hippo4j.rpc.handler.HandlerManager;
import cn.hippo4j.rpc.handler.ClientPoolHandler;
import cn.hippo4j.rpc.handler.ClientTakeHandler;
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.support.AddressUtil;
import cn.hippo4j.rpc.server.ServerSupport;
import io.netty.channel.ChannelHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Different from the management of the server side, in order not to waste resources, we pool the
 * connections of different addresses and turn the client into a one-time resource. If there is no
 * support from the container, the client is a resource that can be recovered after use. This is
 * similar to {@link WeakReference}, but the client needs the user to set the life cycle.<br>
 * <p>
 * Typically, the client is just a front for the direct connection between the client and the server,
 * and for any call to succeed, only the {@link ClientConnection} connection is required. In the
 * presence of a container, it is necessary to keep the client active for a long time, when the
 * client should be a specific resource in the container, following the resource lifecycle specified
 * by the container
 *
 * @see cn.hippo4j.rpc.client.RPCClient
 * @see SimpleClientConnection
 * @see ServerSupport
 * @since 2.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientSupport {

    /**
     * the cache for client
     */
    private static final Map<InetSocketAddress, Client> CLIENT_MAP = new ConcurrentHashMap<>();

    /**
     * Obtain the client connected to the server through the server address. If the client does not exist, create one
     *
     * @param address        the address
     * @param handlerManager the handlerManager
     * @return Client
     */
    public static Client getClient(InetSocketAddress address, HandlerManager<ChannelHandler> handlerManager) {
        return CLIENT_MAP.computeIfAbsent(address, a -> {
            ClientPoolHandler handler = (handlerManager instanceof ClientPoolHandler)
                    ? (ClientPoolHandler) handlerManager
                    : new ClientPoolHandler();
            if (handler.isEmpty()) {
                handler.addFirst(null, new ClientTakeHandler());
            }
            handler.addLast(null, new ErrorClientHandler());
            SimpleClientConnection connection = new SimpleClientConnection(address, handler);
            return new RPCClient(connection);
        });
    }

    /**
     * Obtain the client connected to the server through the server address. If the client does not exist, create one by default
     *
     * @param address the address
     * @return Client
     */
    public static Client getClient(InetSocketAddress address) {
        return getClient(address, new ClientPoolHandler());
    }

    /**
     * Find a suitable client and send a request to the server
     *
     * @param address     address
     * @param handlerName The handler that can handle this request
     * @param param       parameter
     * @return result
     */
    public static <R> R clientSend(String address, String handlerName, Object[] param) {
        InetSocketAddress socketAddress = AddressUtil.getInetAddress(address);
        Client client = getClient(socketAddress);
        Request request = new DefaultRequest(UUID.randomUUID().toString(), handlerName, param);
        return client.connect(request);
    }

    public static <R> R clientSend(String address, String handlerName, Object param) {
        Object[] params = {param};
        InetSocketAddress socketAddress = AddressUtil.getInetAddress(address);
        Client client = getClient(socketAddress);
        Request request = new DefaultRequest(UUID.randomUUID().toString(), handlerName, params);
        return client.connect(request);
    }

    /**
     * Find a suitable client and send a request to the server
     *
     * @param address     address
     * @param handlerName The handler that can handle this request
     * @return result
     */
    public static <R> R clientSend(String address, String handlerName) {
        InetSocketAddress socketAddress = AddressUtil.getInetAddress(address);
        Client client = getClient(socketAddress);
        Request request = new DefaultRequest(UUID.randomUUID().toString(), handlerName);
        return client.connect(request);
    }

    /**
     * Close a client connected to a server address. The client may have been closed
     *
     * @param address the address
     */
    public static void closeClient(InetSocketAddress address) {
        Client client = CLIENT_MAP.remove(address);
        Optional.ofNullable(client)
                .ifPresent(c -> {
                    try {
                        c.close();
                    } catch (IOException e) {
                        throw new OperationException(e);
                    }
                });
    }
}
