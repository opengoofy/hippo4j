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

package cn.hippo4j.config.rpc.handler;

import cn.hippo4j.config.rpc.exception.ConnectionException;
import cn.hippo4j.config.rpc.process.ActivePostProcess;
import cn.hippo4j.config.rpc.response.DefaultResponse;
import cn.hippo4j.config.rpc.support.ClassRegistry;
import cn.hippo4j.config.rpc.support.Instance;
import cn.hippo4j.config.rpc.request.Request;
import cn.hippo4j.config.rpc.response.Response;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.ReflectUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * netty adaptation layer
 */
public class NettyServerTakeHandler extends ChannelInboundHandlerAdapter implements ConnectHandler {

    List<ActivePostProcess> processes;
    Instance instance;

    public NettyServerTakeHandler(List<ActivePostProcess> processes, Instance instance) {
        this.processes = processes;
        this.instance = instance;
    }

    public NettyServerTakeHandler(Instance instance) {
        this(new LinkedList<>(), instance);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (!(msg instanceof Request)) {
            return;
        }
        Request request = (Request) msg;
        Response response = handler(request);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        } else {
            throw new ConnectionException(cause);
        }
    }

    @Override
    public Response handler(Request request) {
        if (!preHandlers(request)) {
            return null;
        }
        try {
            Class<?> cls = ClassRegistry.get(request.getClassName());
            Method method = ReflectUtil.getMethodByName(cls, request.getMethodName(), request.getParameterTypes());
            Assert.notNull(method);
            Object invoke = ReflectUtil.invoke(instance.getInstance(cls), method, request.getParameters());
            Response response = new DefaultResponse(request.getKey(), invoke.getClass(), invoke);
            postHandlers(request, response);
            return response;
        } catch (Exception e) {
            Response response = new DefaultResponse(request.getKey(), e, e.getMessage());
            afterCompletions(request, response, e);
            return response;
        }
    }

    private boolean preHandlers(Request request) {
        for (ActivePostProcess process : processes) {
            if (!process.preHandler(request)) {
                return false;
            }
        }
        return true;
    }

    private void postHandlers(Request request, Response response) {
        for (ActivePostProcess process : processes) {
            process.postHandler(request, response);
        }
    }

    private void afterCompletions(Request request, Response response, Exception e) {
        for (ActivePostProcess process : processes) {
            process.afterCompletion(request, response, e);
        }
    }

}
