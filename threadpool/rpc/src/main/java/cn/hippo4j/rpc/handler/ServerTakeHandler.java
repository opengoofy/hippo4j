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
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.DefaultResponse;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import io.netty.channel.ChannelHandler;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * netty adaptation layer about {@link DefaultRequest}<br><br>
 * Parse the parameters in the request to execute the corresponding method. <br>
 * This is a relatively flexible processor at present, but there are still great defects. <br>
 * <br>For example:<br>
 * <ul>
 *     <li>This handler only supports requests with one parameters, it will not work if the number of parameters does not match</li>
 *     <li>If you want to pass multiple parameters please wrap them, or customize the processor</li>
 *     <li>This processor does not consider whether the types match when parsing parameters, and an error occurs if the conversion fails</li>
 * </ul>
 *
 * @since 2.0.0
 */
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class ServerTakeHandler<T, R> extends ServerHandler {

    final String name;
    final Function<T, R> fun;

    @Override
    String getName() {
        return name;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Response sendHandler(Request request) {
        try {
            Object[] parameters = request.getParameters();
            if (parameters.length != 1) {
                throw new HandlerNotFoundException("no handler found that matches the pair " + name + " and function");
            }
            T t = (T) parameters[0];
            R r = fun.apply(t);
            return new DefaultResponse(request.getRID(), r);
        } catch (Exception e) {
            return new DefaultResponse(request.getRID(), e.getMessage());
        }
    }

}
