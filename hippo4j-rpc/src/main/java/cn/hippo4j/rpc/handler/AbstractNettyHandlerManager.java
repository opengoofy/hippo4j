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
public abstract class AbstractNettyHandlerManager implements HandlerManager<ChannelHandler> {

    protected final List<HandlerEntity<ChannelHandler>> handlerEntities;

    AtomicLong firstIndex = new AtomicLong(-1);

    AtomicLong lastIndex = new AtomicLong(0);

    protected AbstractNettyHandlerManager(List<ChannelHandler> handlerEntities) {
        Assert.notNull(handlerEntities);
        this.handlerEntities = handlerEntities.stream()
                .filter(Objects::nonNull)
                .map(c -> getHandlerEntity(lastIndex.getAndIncrement(), c, null))
                .collect(Collectors.toList());
    }

    protected AbstractNettyHandlerManager(ChannelHandler... handlerEntities) {
        this(handlerEntities != null ? Arrays.asList(handlerEntities) : Collections.emptyList());
    }

    protected AbstractNettyHandlerManager() {
        this.handlerEntities = new LinkedList<>();
    }

    @Override
    public boolean isEmpty() {
        return handlerEntities.isEmpty();
    }

    /**
     * {@inheritDoc}
     *
     * @param name    name
     * @param handler handler
     * @return NettyHandlerManager
     */
    public AbstractNettyHandlerManager addLast(String name, ChannelHandler handler) {
        Assert.notNull(handler);
        this.handlerEntities.add(getHandlerEntity(lastIndex.getAndIncrement(), handler, name));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param name    name
     * @param handler handler
     * @return NettyHandlerManager
     */
    public AbstractNettyHandlerManager addFirst(String name, ChannelHandler handler) {
        Assert.notNull(handler);
        this.handlerEntities.add(getHandlerEntity(firstIndex.getAndIncrement(), handler, name));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param handler handler
     * @return NettyHandlerManager
     */
    public AbstractNettyHandlerManager addLast(ChannelHandler handler) {
        Assert.notNull(handler);
        this.handlerEntities.add(getHandlerEntity(lastIndex.getAndIncrement(), handler, null));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param handler handler
     * @return NettyHandlerManager
     */
    public AbstractNettyHandlerManager addFirst(ChannelHandler handler) {
        Assert.notNull(handler);
        this.handlerEntities.add(getHandlerEntity(firstIndex.getAndDecrement(), handler, null));
        return this;
    }
}
