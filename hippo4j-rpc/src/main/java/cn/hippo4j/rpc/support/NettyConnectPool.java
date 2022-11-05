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

import cn.hippo4j.rpc.exception.ConnectionException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * This parameter applies only to the connection pool of netty
 */
@Slf4j
public class NettyConnectPool {

    ChannelHealthChecker healthCheck = ChannelHealthChecker.ACTIVE;
    FixedChannelPool.AcquireTimeoutAction acquireTimeoutAction = FixedChannelPool.AcquireTimeoutAction.NEW;
    int maxPendingAcquires = Integer.MAX_VALUE;
    ChannelPoolHandler handler;
    ChannelPool pool;
    String host;
    int port;

    public NettyConnectPool(String host, int port, int maxConnect,
                            long timeout, EventLoopGroup worker,
                            Class<? extends Channel> socketChannelCls,
                            ChannelPoolHandler handler) {
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(host, port);
        Bootstrap bootstrap = new Bootstrap()
                .group(worker)
                .channel(socketChannelCls)
                .remoteAddress(socketAddress);
        this.host = host;
        this.port = port;
        this.handler = handler;
        this.pool = new FixedChannelPool(bootstrap, handler, healthCheck, acquireTimeoutAction,
                timeout, maxConnect, maxPendingAcquires, true, true);
        log.info("The connection pool is established with the connection target {}:{}", host, port);
        NettyConnectPoolHolder.createPool(host, port, this);
    }

    public Channel acquire(long timeoutMillis) {
        try {
            Future<Channel> fch = pool.acquire();
            return fch.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new ConnectionException("Failed to get the connection", e);
        }
    }

    public Future<Channel> acquire() {
        try {
            return pool.acquire();
        } catch (Exception e) {
            throw new ConnectionException("Failed to get the connection", e);
        }
    }

    public void release(Channel channel) {
        try {
            if (channel != null) {
                pool.release(channel);
            }
        } catch (Exception e) {
            throw new ConnectionException("Failed to release the connection", e);
        }
    }

    public void close() {
        try {
            pool.close();
            NettyConnectPoolHolder.remove(host, port);
        } catch (Exception e) {
            throw new ConnectionException("Failed to close the connection pool", e);
        }
    }
}
