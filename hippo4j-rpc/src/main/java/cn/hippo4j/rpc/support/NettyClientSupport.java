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
import cn.hippo4j.rpc.client.Client;
import cn.hippo4j.rpc.client.ClientConnection;
import cn.hippo4j.rpc.client.NettyClientConnection;
import cn.hippo4j.rpc.client.RPCClient;
import cn.hippo4j.rpc.handler.HandlerManager;
import cn.hippo4j.rpc.handler.NettyClientPoolHandler;
import cn.hippo4j.rpc.handler.NettyClientTakeHandler;
import io.netty.channel.ChannelHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.Map;
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
 * @see cn.hippo4j.rpc.client.NettyClientConnection
 * @see NettyServerSupport
 * @see ClientFactoryBean
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NettyClientSupport {

    /**
     * the cache for client
     */
    private static final Map<InetSocketAddress, Client> clientMap = new ConcurrentHashMap<>();

    /**
     * Obtain the client connected to the server through the server address. If the client does not exist, create one
     *
     * @param address        the address
     * @param handlerManager the handlerManager
     * @return Client
     */
    public static Client getClient(InetSocketAddress address, HandlerManager<ChannelHandler> handlerManager) {
        return clientMap.computeIfAbsent(address, a -> {
            NettyClientPoolHandler handler = (handlerManager instanceof NettyClientPoolHandler)
                    ? (NettyClientPoolHandler) handlerManager
                    : new NettyClientPoolHandler();
            if (handler.isEmpty()) {
                handler.addFirst(new NettyClientTakeHandler());
            }
            NettyClientConnection connection = new NettyClientConnection(address, handler);
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
        return getClient(address, new NettyClientPoolHandler());
    }

    /**
     * Close a client connected to a server address. The client may have been closed
     *
     * @param address the address
     */
    public static void closeClient(InetSocketAddress address) {
        Client client = clientMap.remove(address);
        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException e) {
            throw new IllegalException(e);
        }
    }
}
