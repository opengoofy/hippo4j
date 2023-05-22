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

import cn.hippo4j.rpc.coder.ObjectEncoder;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Processing by the client connection pool handler to clean the buffer and define new connection properties
 *
 * @since 2.0.0
 */
@Slf4j
public class ClientPoolHandler extends AbstractHandlerManager implements ChannelPoolHandler {

    public ClientPoolHandler(List<ChannelHandler> handlers) {
        super(handlers);
    }

    public ClientPoolHandler(ChannelHandler... handlers) {
        super(handlers);
    }

    public ClientPoolHandler() {
        super();
    }

    @Override
    public ClientPoolHandler addLast(String name, ChannelHandler handler) {
        super.addLast(name, handler);
        return this;
    }

    @Override
    public ClientPoolHandler addFirst(String name, ChannelHandler handler) {
        super.addFirst(name, handler);
        return this;
    }

    @Override
    public void channelReleased(Channel ch) {
        ch.writeAndFlush(Unpooled.EMPTY_BUFFER);
        if (log.isDebugEnabled()) {
            log.debug("The connection buffer has been emptied of data");
        }
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
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new ObjectEncoder());
        pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
        this.handlerEntities.stream()
                .sorted()
                .forEach(h -> {
                    if (h.getName() == null) {
                        pipeline.addLast(h.getHandler());
                    } else {
                        pipeline.addLast(h.getName(), h.getHandler());
                    }
                });
    }

}
