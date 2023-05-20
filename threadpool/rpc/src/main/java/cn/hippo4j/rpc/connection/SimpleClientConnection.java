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

import cn.hippo4j.rpc.exception.ConnectionException;
import cn.hippo4j.rpc.exception.TimeOutException;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import cn.hippo4j.rpc.support.ResultHolder;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.LockSupport;

/**
 * Client implemented using netty
 *
 * @since 2.0.0
 */
@Slf4j
public class SimpleClientConnection implements ClientConnection {

    InetSocketAddress address;
    /**
     * Obtain the connection timeout period. The default value is 30s
     */
    long timeout = 30000L;
    EventLoopGroup worker = new NioEventLoopGroup();
    SimpleConnectPool connectionPool;
    static final String TIME_OUT_MSG = "Timeout waiting for server-side response";

    public SimpleClientConnection(InetSocketAddress address,
                                  ChannelPoolHandler handler) {
        this.address = address;
        this.connectionPool = ConnectPoolHolder.getPool(address, timeout, worker, handler);
    }

    @Override
    public <R> R connect(Request request) {
        Channel channel = connectionPool.acquire(timeout);
        try {
            channel.writeAndFlush(request);
            return wait(request.getRID());
        } finally {
            connectionPool.release(channel);
        }
    }

    /**
     * wait the Response
     *
     * @param requestId RID
     * @return Response
     */
    @SuppressWarnings("unchecked")
    public <R> R wait(String requestId) {
        Response response;
        if (log.isDebugEnabled()) {
            log.debug("Call successful, target address is {}:{}, request key is {}", address.getHostName(), address.getPort(), requestId);
        }
        // Wait for execution to complete
        ResultHolder.putThread(requestId, Thread.currentThread());
        LockSupport.parkNanos(timeout() * 1000000);
        response = ResultHolder.get(requestId);
        if (response == null) {
            throw new TimeOutException(TIME_OUT_MSG);
        }
        if (response.isErr()) {
            throw new ConnectionException(response.getErrMsg());
        }
        if (log.isDebugEnabled()) {
            log.debug("The response from {}:{} was received successfully with the response key {}.", address.getHostName(), address.getPort(), requestId);
        }
        return (R) response.getObj();
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
        worker.shutdownGracefully();
        connectionPool.close();
    }

}
