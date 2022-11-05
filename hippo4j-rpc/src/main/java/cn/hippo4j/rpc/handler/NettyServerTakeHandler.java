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
import cn.hippo4j.common.toolkit.ReflectUtil;
import cn.hippo4j.rpc.process.ActivePostProcess;
import cn.hippo4j.rpc.process.ActiveProcessChain;
import cn.hippo4j.rpc.request.Request;
import cn.hippo4j.rpc.response.DefaultResponse;
import cn.hippo4j.rpc.response.Response;
import cn.hippo4j.rpc.support.ClassRegistry;
import cn.hippo4j.rpc.support.Instance;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * netty adaptation layer
 */
@ChannelHandler.Sharable
public class NettyServerTakeHandler extends AbstractNettyTakeHandler implements ConnectHandler {

    ActiveProcessChain activeProcessChain;
    Instance instance;

    public NettyServerTakeHandler(List<ActivePostProcess> processes, Instance instance) {
        this.activeProcessChain = new ActiveProcessChain(processes);
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
        Response response = sendHandler(request);
        ctx.writeAndFlush(response);
    }

    @Override
    public Response sendHandler(Request request) {
        if (!activeProcessChain.applyPreHandle(request)) {
            return null;
        }
        Response response = null;
        try {
            Class<?> cls = ClassRegistry.get(request.getClassName());
            Method method = ReflectUtil.getMethodByName(cls, request.getMethodName(), request.getParameterTypes());
            Assert.notNull(method);
            Object invoke = ReflectUtil.invoke(instance.getInstance(cls), method, request.getParameters());
            response = new DefaultResponse(request.getKey(), invoke.getClass(), invoke);
            activeProcessChain.applyPostHandle(request, response);
            return response;
        } catch (Exception e) {
            response = new DefaultResponse(request.getKey(), e, e.getMessage());
            activeProcessChain.afterCompletion(request, response, e);
            return response;
        } finally {
            activeProcessChain.afterCompletion(request, response, null);
        }
    }

}
