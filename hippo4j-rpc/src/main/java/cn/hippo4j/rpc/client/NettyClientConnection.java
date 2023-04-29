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

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.web.exception.IllegalException;
import cn.hippo4j.rpc.exception.TimeOutException;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import cn.hippo4j.rpc.support.NettyConnectPool;
import cn.hippo4j.rpc.support.NettyConnectPoolHolder;
import cn.hippo4j.rpc.support.ResultHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.locks.LockSupport;

/**
 * Client implemented using netty
 *
 * @since 1.5.1
 */
@Slf4j
public class NettyClientConnection implements ClientConnection {

    InetSocketAddress address;
    /**
     * Obtain the connection timeout period. The default value is 30s
     */
    long timeout = 30000L;
    EventLoopGroup worker = new NioEventLoopGroup();
    NettyConnectPool connectionPool;
    ChannelFuture future;
    Channel channel;

    public NettyClientConnection(InetSocketAddress address,
                                 ChannelPoolHandler handler) {
        Assert.notNull(worker);
        this.address = address;
        this.connectionPool = NettyConnectPoolHolder.getPool(address, timeout, worker, handler);
    }

    @Override
    public Response connect(Request request) {
        this.channel = connectionPool.acquire(timeout);
        boolean debugEnabled = log.isDebugEnabled();
        Response response;
        try {
            String key = request.getKey();
            this.future = channel.writeAndFlush(request);
            if (debugEnabled) {
                log.debug("Call successful, target address is {}:{}, request key is {}", address.getHostName(), address.getPort(), key);
            }
            // Wait for execution to complete
            ResultHolder.putThread(key, Thread.currentThread());
            LockSupport.parkNanos(timeout() * 1000000);
            response = ResultHolder.get(key);
            if (response == null) {
                throw new TimeOutException("Timeout waiting for server-side response");
            }
            if (debugEnabled) {
                log.debug("The response from {}:{} was received successfully with the response key {}.", address.getHostName(), address.getPort(), key);
            }
            return response;
        } catch (Exception ex) {
            throw new IllegalException(ex);
        } finally {
            connectionPool.release(this.channel);
        }
    }

    @Override
    public long timeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void close() {
        Optional.ofNullable(this.channel)
                .ifPresent(c -> {
                    worker.shutdownGracefully();
                    this.future.channel().close();
                    this.channel.close();
                });
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }
}
