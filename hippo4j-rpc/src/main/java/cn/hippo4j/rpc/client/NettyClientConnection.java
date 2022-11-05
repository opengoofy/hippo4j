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
import cn.hippo4j.rpc.process.ActivePostProcess;
import cn.hippo4j.rpc.process.ActiveProcessChain;
import cn.hippo4j.rpc.request.Request;
import cn.hippo4j.rpc.response.Response;
import cn.hippo4j.rpc.support.NettyConnectPool;
import cn.hippo4j.rpc.support.NettyConnectPoolHolder;
import cn.hippo4j.rpc.support.ResultHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * Client implemented using netty
 */
@Slf4j
public class NettyClientConnection implements ClientConnection {

    String host;
    Integer port;
    // Obtain the connection timeout period. The default value is 30s
    long timeout = 30000L;
    EventLoopGroup worker = new NioEventLoopGroup();
    ActiveProcessChain activeProcessChain;
    NettyConnectPool connectionPool;
    ChannelFuture future;
    Channel channel;

    public NettyClientConnection(String host, int port,
                                 List<ActivePostProcess> activeProcesses,
                                 ChannelPoolHandler handler) {
        Assert.notNull(worker);
        this.host = host;
        this.port = port;
        this.activeProcessChain = new ActiveProcessChain(activeProcesses);
        this.connectionPool = NettyConnectPoolHolder.getPool(host, port, timeout, worker, handler);
    }

    public NettyClientConnection(String host, int port, ChannelPoolHandler handler) {
        this(host, port, new LinkedList<>(), handler);
    }

    @Override
    public Response connect(Request request) {
        activeProcessChain.applyPreHandle(request);
        this.channel = connectionPool.acquire(timeout);
        Response response = null;
        try {
            String key = request.getKey();
            this.future = channel.writeAndFlush(request);
            log.info("Call successful, target address is {}:{}, request key is {}", host, port, key);
            // Wait for execution to complete
            ResultHolder.putThread(key, Thread.currentThread());
            LockSupport.parkNanos(timeout() * 1000000);
            response = ResultHolder.get(key);
            if (response == null) {
                throw new TimeOutException("Timeout waiting for server-side response");
            }
            activeProcessChain.applyPostHandle(request, response);
            log.info("The response from {}:{} was received successfully with the response key {}.", host, port, key);
            return response;
        } catch (Exception ex) {
            activeProcessChain.afterCompletion(request, response, ex);
            throw new IllegalException(ex);
        } finally {
            activeProcessChain.afterCompletion(request, response, null);
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
        if (this.channel == null) {
            return;
        }
        worker.shutdownGracefully();
        this.future.channel().close();
        this.channel.close();
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }
}
