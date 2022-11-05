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

package cn.hippo4j.rpc.support;

import cn.hippo4j.common.toolkit.IdUtil;
import cn.hippo4j.common.web.exception.IllegalException;
import cn.hippo4j.rpc.client.NettyClientConnection;
import cn.hippo4j.rpc.request.DefaultRequest;
import cn.hippo4j.rpc.request.Request;
import cn.hippo4j.rpc.response.Response;
import io.netty.channel.pool.ChannelPoolHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Add a proxy for the request, {@link Proxy} and {@link InvocationHandler}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NettyProxyCenter {

    // cache
    static Map<Class<?>, Object> map = new HashMap<>();

    /**
     * A proxy object for PRC is obtained through an interface
     *
     * @param cls     The interface type
     * @param host    Request the address
     * @param port    port
     * @param <T>     Object type
     * @param handler the pool handler  for netty
     * @return Proxy objects
     */
    public static <T> T getProxy(Class<T> cls, String host, int port, ChannelPoolHandler handler) {
        NettyClientConnection connection = new NettyClientConnection(host, port, handler);
        return getProxy(connection, cls, host, port);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProxy(NettyClientConnection connection, Class<T> cls, String host, int port) {
        boolean b = cls.isInterface();
        if (!b) {
            throw new IllegalException(cls.getName() + "is not a Interface");
        }
        Object o = map.get(cls);
        if (o != null) {
            return (T) o;
        }
        T obj = (T) Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class[]{cls},
                (proxy, method, args) -> {
                    String clsName = cls.getName();
                    String methodName = method.getName();
                    String key = host + port + clsName + methodName + IdUtil.simpleUUID();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Request request = new DefaultRequest(key, clsName, methodName, parameterTypes, args);
                    Response response = connection.connect(request);
                    if (response == null) {
                        return null;
                    }
                    if (response.isErr()) {
                        throw new IllegalException(response.getErrMsg(), response.getThrowable());
                    }
                    return response.getObj();
                });
        map.put(cls, obj);
        return obj;
    }
}
