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

package cn.hippo4j.rpc.connection;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * To avoid creating multiple connection pools for the same host:port, save all connection pools of the client
 *
 * @since 2.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectPoolHolder {

    static int maxConnect = 256;

    static Map<String, SimpleConnectPool> connectPoolMap = new ConcurrentHashMap<>();

    private static SimpleConnectPool initPool(InetSocketAddress address,
                                              long timeout, EventLoopGroup worker,
                                              ChannelPoolHandler handler) {
        return new SimpleConnectPool(address, maxConnect, timeout, worker, NioSocketChannel.class, handler);
    }

    private static String getKey(InetSocketAddress address) {
        return address.getHostName() + ":" + address.getPort();
    }

    /**
     * The connection pool connectPoolMapping may already exist before the connection pool
     * connectPoolMapping is established. In this case, the connection pool is directly overwritten
     *
     * @param address the InetSocketAddress
     * @param pool    This parameter applies only to the connection pool of netty
     */
    public static void createPool(InetSocketAddress address, SimpleConnectPool pool) {
        connectPoolMap.put(getKey(address), pool);
    }

    /**
     * Gets a connection pool, or null if there is no corresponding connectPoolMapping
     *
     * @param address the InetSocketAddress
     * @return Map to the connection pool
     */
    public static SimpleConnectPool getPool(InetSocketAddress address) {
        return connectPoolMap.get(getKey(address));
    }

    /**
     * Gets a connection pool, and if there is no connectPoolMapping, creates one with the values provided and joins the connectPoolMapping
     *
     * @param address the InetSocketAddress
     * @param timeout timeout
     * @param worker  Special {@link EventExecutorGroup} which allows registering {@link Channel}s
     *                that get processed for later selection during the event loop.
     * @param handler the chandler for netty
     * @return Map to the connection pool
     */
    public static synchronized SimpleConnectPool getPool(InetSocketAddress address,
                                                         long timeout, EventLoopGroup worker,
                                                         ChannelPoolHandler handler) {
        /*
         * this cannot use the computeIfAbsent method directly here because put is already used in init. Details refer to https://bugs.openjdk.java.net/browse/JDK-8062841
         */
        SimpleConnectPool pool = getPool(address);
        return pool == null ? initPool(address, timeout, worker, handler) : pool;
    }

    /**
     * Disconnect a connection connectPoolMapping. This must take effect at the same time as the connection pool is closed
     *
     * @param address the InetSocketAddress
     */
    public static void remove(InetSocketAddress address) {
        connectPoolMap.remove(getKey(address));
    }

    /**
     * clear
     */
    public static void clear() {
        connectPoolMap.clear();
    }
}
