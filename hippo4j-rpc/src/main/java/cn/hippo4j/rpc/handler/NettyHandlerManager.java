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

import cn.hippo4j.common.toolkit.Assert;
import io.netty.channel.ChannelHandler;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Processor manager for ChannelHandler in netty
 */
public abstract class NettyHandlerManager implements HandlerManager<ChannelHandler> {

    protected final List<HandlerEntity<ChannelHandler>> handlers;

    AtomicLong firstIndex = new AtomicLong(-1);

    AtomicLong lastIndex = new AtomicLong(0);

    protected NettyHandlerManager(List<ChannelHandler> handlers) {
        this.handlers = handlers.stream()
                .filter(Objects::nonNull)
                .map(c -> getHandlerEntity(lastIndex.getAndIncrement(), c, null))
                .collect(Collectors.toList());
    }

    protected NettyHandlerManager(ChannelHandler... handlers) {
        this(handlers != null ? Arrays.asList(handlers) : Collections.emptyList());
    }

    protected NettyHandlerManager() {
        this.handlers = new LinkedList<>();
    }

    /**
     * {@inheritDoc}
     *
     * @param name    name
     * @param handler handler
     * @return NettyHandlerManager
     */
    public NettyHandlerManager addLast(String name, ChannelHandler handler) {
        Assert.notNull(handler);
        this.handlers.add(getHandlerEntity(lastIndex.getAndIncrement(), handler, name));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param name    name
     * @param handler handler
     * @return NettyHandlerManager
     */
    public NettyHandlerManager addFirst(String name, ChannelHandler handler) {
        Assert.notNull(handler);
        this.handlers.add(getHandlerEntity(firstIndex.getAndIncrement(), handler, name));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param handler handler
     * @return NettyHandlerManager
     */
    public NettyHandlerManager addLast(ChannelHandler handler) {
        Assert.notNull(handler);
        this.handlers.add(getHandlerEntity(lastIndex.getAndIncrement(), handler, null));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param handler handler
     * @return NettyHandlerManager
     */
    public NettyHandlerManager addFirst(ChannelHandler handler) {
        Assert.notNull(handler);
        this.handlers.add(getHandlerEntity(firstIndex.getAndDecrement(), handler, null));
        return this;
    }
}
