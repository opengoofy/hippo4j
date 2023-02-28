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
import cn.hippo4j.rpc.client.Client;
import cn.hippo4j.rpc.exception.ConnectionException;
import cn.hippo4j.rpc.handler.NettyClientPoolHandler;
import cn.hippo4j.rpc.model.DefaultRequest;
import cn.hippo4j.rpc.model.Request;
import cn.hippo4j.rpc.model.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Add a proxy for the request, {@link Proxy} and {@link InvocationHandler}
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NettyProxyCenter {

    // cache
    static Map<String, Object> map = new ConcurrentHashMap<>();

    /**
     * A proxy object for PRC is obtained through an interface
     *
     * @param cls     The interface type
     * @param address address
     * @param <T>     Object type
     * @param handler the pool handler  for netty
     * @return Proxy objects
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> cls, InetSocketAddress address, NettyClientPoolHandler handler) {
        Client client = NettyClientSupport.getClient(address, handler);
        String s = address + cls.getName();
        Object o = map.get(s);
        if (o != null) {
            return (T) o;
        }
        return createProxy(client, cls, address);
    }

    /**
     * A proxy object for PRC is obtained through an interface
     *
     * @param cls     The interface type
     * @param address address String
     * @param <T>     Object type
     * @return Proxy objects
     */
    @SuppressWarnings("unchecked")
    public static <T> T getProxy(Class<T> cls, String address) {
        String[] addressStr = address.split(":");
        if (addressStr.length < 2) {
            throw new ConnectionException("Failed to connect to the server because the IP address is invalid. Procedure");
        }
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(addressStr[0], Integer.parseInt(addressStr[1]));
        String s = socketAddress + cls.getName();
        Object o = map.get(s);
        if (o != null) {
            return (T) o;
        }
        Client client = NettyClientSupport.getClient(socketAddress);
        return createProxy(client, cls, socketAddress);
    }

    /**
     * remove proxy object
     *
     * @param cls     the class
     * @param address address String
     */
    public static void removeProxy(Class<?> cls, String address) {
        String[] addressStr = address.split(":");
        if (addressStr.length < 2) {
            throw new ConnectionException("Failed to connect to the server because the IP address is invalid. Procedure");
        }
        InetSocketAddress socketAddress = InetSocketAddress.createUnresolved(addressStr[0], Integer.parseInt(addressStr[1]));
        String s = socketAddress + cls.getName();
        NettyClientSupport.closeClient(socketAddress);
        map.remove(s);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Client client, Class<T> cls, InetSocketAddress address) {
        boolean b = cls.isInterface();
        if (!b) {
            throw new IllegalException(cls.getName() + "is not a Interface");
        }
        String s = address.toString() + cls.getName();
        Object o = map.get(s);
        if (o != null) {
            return (T) o;
        }
        T obj = (T) Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class[]{cls},
                (proxy, method, args) -> {
                    String clsName = cls.getName();
                    String methodName = method.getName();
                    String key = address + clsName + methodName + IdUtil.simpleUUID();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    Request request = new DefaultRequest(key, clsName, methodName, parameterTypes, args);
                    Response response = client.connection(request);
                    if (response == null) {
                        return null;
                    }
                    if (response.isErr()) {
                        throw new IllegalException(response.getErrMsg(), response.getThrowable());
                    }
                    return response.getObj();
                });
        map.put(s, obj);
        return obj;
    }
}
