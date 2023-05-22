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

import io.netty.channel.ChannelHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Processor manager for ChannelHandler in netty
 *
 * @since 2.0.0
 */
public abstract class AbstractHandlerManager implements HandlerManager<ChannelHandler> {

    protected final List<HandlerEntity<ChannelHandler>> handlerEntities;

    AtomicLong firstIndex = new AtomicLong(-1);

    AtomicLong lastIndex = new AtomicLong(0);

    protected AbstractHandlerManager(List<ChannelHandler> handlerEntities) {
        this.handlerEntities = handlerEntities.stream()
                .filter(Objects::nonNull)
                .map(c -> getHandlerEntity(lastIndex.getAndIncrement(), c, null))
                .collect(Collectors.toList());
    }

    protected AbstractHandlerManager(ChannelHandler... handlerEntities) {
        this(handlerEntities != null ? Arrays.asList(handlerEntities) : Collections.emptyList());
    }

    protected AbstractHandlerManager() {
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
    public AbstractHandlerManager addLast(String name, ChannelHandler handler) {
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
    public AbstractHandlerManager addFirst(String name, ChannelHandler handler) {
        this.handlerEntities.add(getHandlerEntity(firstIndex.getAndIncrement(), handler, name));
        return this;
    }
}
