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

import cn.hippo4j.rpc.client.ClientSupport;
import cn.hippo4j.rpc.exception.ConnectionException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * This parameter applies only to the connection pool of netty
 *
 * @since 2.0.0
 */
@Slf4j
public class SimpleConnectPool {

    ChannelHealthChecker healthCheck = ChannelHealthChecker.ACTIVE;
    FixedChannelPool.AcquireTimeoutAction acquireTimeoutAction = FixedChannelPool.AcquireTimeoutAction.NEW;
    int maxPendingAcquires = Integer.MAX_VALUE;
    ChannelPoolHandler handler;
    ChannelPool pool;
    InetSocketAddress address;

    public SimpleConnectPool(InetSocketAddress address, int maxConnect,
                             long timeout, EventLoopGroup worker,
                             Class<? extends Channel> socketChannelCls,
                             ChannelPoolHandler handler) {
        Bootstrap bootstrap = new Bootstrap()
                .group(worker)
                .channel(socketChannelCls)
                .option(ChannelOption.TCP_NODELAY, true)
                .remoteAddress(address);
        this.address = address;
        this.handler = handler;
        this.pool = new FixedChannelPool(bootstrap, handler, healthCheck, acquireTimeoutAction,
                timeout, maxConnect, maxPendingAcquires, true, true);
        if (log.isDebugEnabled()) {
            log.info("The connection pool is established with the connection target {}:{}", address.getHostName(), address.getPort());
        }
        ConnectPoolHolder.createPool(address, this);
    }

    public Channel acquire(long timeoutMillis) {
        try {
            Future<Channel> fch = pool.acquire();
            return fch.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            ClientSupport.closeClient(address);
            throw new ConnectionException("Failed to get the connection", e);
        }
    }

    public Future<Channel> acquire() {
        try {
            return pool.acquire();
        } catch (Exception e) {
            ClientSupport.closeClient(address);
            throw new ConnectionException("Failed to get the connection", e);
        }
    }

    public void release(Channel channel) {
        Optional.ofNullable(channel)
                .ifPresent(c -> {
                    try {
                        pool.release(channel);
                    } catch (Exception e) {
                        ClientSupport.closeClient(address);
                        throw new ConnectionException("Failed to release the connection", e);
                    }
                });
    }

    public void close() {
        try {
            pool.close();
            ConnectPoolHolder.remove(address);
        } catch (Exception e) {
            ClientSupport.closeClient(address);
            throw new ConnectionException("Failed to close the connection pool", e);
        }
    }
}
