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

import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;

/**
 * The handler located on the server side provides unified operations for the server side
 *
 * @since 2.0.0
 */
abstract class ServerHandler extends AbstractTakeHandler {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof DefaultRequest)) {
            ctx.fireChannelRead(msg);
            return;
        }
        Request request = (Request) msg;
        if (!Objects.equals(request.getKey(), getName())) {
            ctx.fireChannelRead(msg);
            return;
        }
        Response response = sendHandler(request);
        ctx.writeAndFlush(response);
    }

    /**
     * Get the name of the current handler
     *
     * @return name
     */
    abstract String getName();
}
