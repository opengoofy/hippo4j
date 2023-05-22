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

import cn.hippo4j.rpc.exception.HandlerNotFoundException;
import cn.hippo4j.rpc.model.DefaultResponse;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import io.netty.channel.ChannelHandler;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

/**
 * This is a processor that does not support parameters but can get the return value. <br>
 * Even if the parameters passed by the user will not be recognized, it will even become an error
 *
 * @since 2.0.0
 */
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class ServerBareTakeHandler<T> extends ServerHandler {

    final String name;
    final Supplier<T> fun;

    @Override
    String getName() {
        return name;
    }

    @Override
    public Response sendHandler(Request request) {
        try {
            Object[] parameters = request.getParameters();
            if (parameters.length != 0) {
                throw new HandlerNotFoundException("no handler found that matches the pair " + name + " and function");
            }
            T t = fun.get();
            return new DefaultResponse(request.getRID(), t);
        } catch (Exception e) {
            return new DefaultResponse(request.getRID(), e.getMessage());
        }
    }

}
