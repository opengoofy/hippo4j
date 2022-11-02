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

package cn.hippo4j.config.rpc.client;

import cn.hippo4j.config.rpc.exception.TimeOutException;
import cn.hippo4j.config.rpc.process.ActivePostProcess;
import cn.hippo4j.config.rpc.request.Request;
import cn.hippo4j.config.rpc.response.Response;
import cn.hippo4j.config.rpc.support.NettyConnectPool;
import cn.hippo4j.config.rpc.support.NettyConnectPoolHolder;
import cn.hippo4j.config.rpc.support.ResultHolder;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.web.exception.IllegalException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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
    Channel channel;
    EventLoopGroup worker = new NioEventLoopGroup();
    List<ActivePostProcess> activeProcesses;
    ChannelFuture future;
    NettyConnectPool connectionPool;

    public NettyClientConnection(String host, int port,
                                 List<ActivePostProcess> activeProcesses) {
        Assert.notNull(worker);
        this.host = host;
        this.port = port;
        this.activeProcesses = activeProcesses;
        this.connectionPool = NettyConnectPoolHolder.getPool(host, port, timeout, worker);
    }

    public NettyClientConnection(String host, int port) {
        this(host, port, new LinkedList<>());
    }

    @Override
    public Response connect(Request request) {
        preHandlers(request);
        this.channel = connectionPool.acquire(timeout);
        try {
            String key = request.getKey();
            this.future = channel.writeAndFlush(request);
            log.info("Call successful, target address is {}:{}, request key is {}", host, port, key);
            // Wait for execution to complete
            ResultHolder.put(key, Thread.currentThread());
            LockSupport.parkNanos(timeout() * 1000000);
            Response response = ResultHolder.get(key);
            if (response == null) {
                throw new TimeOutException("Timeout waiting for server-side response");
            }
            postHandlers(request, response);
            log.info("The response from {}:{} was received successfully with the response key {}.", host, port, key);
            return response;
        } catch (Exception ex) {
            afterCompletions(request, null, ex);
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
        if (this.channel == null) {
            return;
        }
        worker.shutdownGracefully();
        this.future.channel().close();
        this.channel.close();
    }

    private void preHandlers(Request request) {
        for (ActivePostProcess process : activeProcesses) {
            process.preHandler(request);
        }
    }

    private void postHandlers(Request request, Response response) {
        for (ActivePostProcess process : activeProcesses) {
            process.postHandler(request, response);
        }
    }

    private void afterCompletions(Request request, Response response, Exception e) {
        for (ActivePostProcess process : activeProcesses) {
            process.afterCompletion(request, response, e);
        }
    }

}
