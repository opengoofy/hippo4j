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

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * To avoid creating multiple connection pools for the same host:port, save all connection pools of the client
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NettyConnectPoolHolder {

    static int maxConnect = 64;

    static Map<String, NettyConnectPool> connectPoolMap = new ConcurrentHashMap<>();

    private static NettyConnectPool initPool(String host, int port,
                                             long timeout, EventLoopGroup worker,
                                             ChannelPoolHandler handler) {
        return new NettyConnectPool(
                host, port, maxConnect,
                timeout, worker,
                NioSocketChannel.class,
                handler);
    }

    private static String getKey(String host, int port) {
        return host + ":" + port;
    }

    /**
     * The connection pool connectPoolMapping may already exist before the connection pool
     * connectPoolMapping is established. In this case, the connection pool is directly overwritten
     *
     * @param host the host
     * @param port the port
     * @param pool This parameter applies only to the connection pool of netty
     */
    public static void createPool(String host, int port, NettyConnectPool pool) {
        connectPoolMap.put(getKey(host, port), pool);
    }

    /**
     * Gets a connection pool, or null if there is no corresponding connectPoolMapping
     *
     * @param host the host
     * @param port the port
     * @return Map to the connection pool
     */
    public static NettyConnectPool getPool(String host, int port) {
        return connectPoolMap.get(getKey(host, port));
    }

    /**
     * Gets a connection pool, and if there is no connectPoolMapping, creates one with the values provided and joins the connectPoolMapping
     *
     * @param host     the host
     * @param port     the port
     * @param timeout  timeout
     * @param worker   Special {@link EventExecutorGroup} which allows registering {@link Channel}s
     *                 that get processed for later selection during the event loop.
     * @param handler the chandler for netty
     * @return Map to the connection pool
     */
    public static synchronized NettyConnectPool getPool(String host, int port,
                                                        long timeout, EventLoopGroup worker,
                                                        ChannelPoolHandler handler) {
        /*
         * this cannot use the computeIfAbsent method directly here because put is already used in init. Details refer to https://bugs.openjdk.java.net/browse/JDK-8062841
         */
        NettyConnectPool pool = getPool(host, port);
        return pool == null ? initPool(host, port, timeout, worker, handler) : pool;
    }

    /**
     * Disconnect a connection connectPoolMapping. This must take effect at the same time as the connection pool is closed
     *
     * @param host host
     * @param port port
     */
    public static void remove(String host, int port) {
        connectPoolMap.remove(getKey(host, port));
    }

    /**
     * clear
     */
    public static void clear() {
        connectPoolMap.clear();
    }
}
