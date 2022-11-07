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

package cn.hippo4j.rpc.handler;

import cn.hippo4j.rpc.coder.NettyDecoder;
import cn.hippo4j.rpc.coder.NettyEncoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Processing by the client connection pool handler to clean the buffer and define new connection properties
 */
@Slf4j
public class NettyClientPoolHandler extends NettyHandlerManager implements ChannelPoolHandler {

    public NettyClientPoolHandler(List<ChannelHandler> handlers) {
        super(handlers);
    }

    public NettyClientPoolHandler(ChannelHandler... handlers) {
        super(handlers);
    }

    public NettyClientPoolHandler() {
        super();
    }

    public NettyClientPoolHandler addLast(String name, ChannelHandler handler) {
        super.addLast(name, handler);
        return this;
    }

    public NettyClientPoolHandler addFirst(String name, ChannelHandler handler) {
        super.addFirst(name, handler);
        return this;
    }

    public NettyClientPoolHandler addLast(ChannelHandler handler) {
        super.addLast(handler);
        return this;
    }

    public NettyClientPoolHandler addFirst(ChannelHandler handler) {
        super.addFirst(handler);
        return this;
    }

    @Override
    public void channelReleased(Channel ch) {
        ch.writeAndFlush(Unpooled.EMPTY_BUFFER);
        log.info("The connection buffer has been emptied of data");
    }

    @Override
    public void channelAcquired(Channel ch) {
        // NO SOMETHING
    }

    @Override
    public void channelCreated(Channel ch) {
        NioSocketChannel channel = (NioSocketChannel) ch;
        channel.config()
                .setTcpNoDelay(false);
        ch.pipeline().addLast(new NettyDecoder(ClassResolvers.cacheDisabled(null)));
        ch.pipeline().addLast(new NettyEncoder());
        this.handlers.stream()
                .sorted()
                .forEach(h -> {
                    if (h.getName() == null) {
                        ch.pipeline().addLast(h.getHandler());
                    } else {
                        ch.pipeline().addLast(h.getName(), h.getHandler());
                    }
                });
    }

}
