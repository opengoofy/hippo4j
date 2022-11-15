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

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.rpc.client.Client;
import cn.hippo4j.rpc.discovery.DiscoveryAdapter;
import cn.hippo4j.rpc.exception.ConnectionException;
import cn.hippo4j.rpc.handler.NettyClientPoolHandler;
import io.netty.channel.ChannelHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetSocketAddress;
import java.util.Optional;

/**
 * A FactoryBean that builds interfaces to invoke proxy objects
 * is responsible for managing the entire life cycle of the proxy objects<br>
 * <h3>APPLICATION START</h3>
 * When the application is started, the request initiator needs to complete the proxy of the calling interface,
 * which ensures that the method can be requested to the server side when the method is called, rather than simply
 * request an interface that cannot be instantiated. The classes involved in adding proxy to the interface are:
 * <ul>
 *     <li>{@link NettyClientSupport}</li>
 *     <li>{@link NettyProxyCenter}</li>
 *     <li>{@link NettyClientPoolHandler}</li>
 * </ul>
 * <h3>AND SPRING</h3>
 * In order to fully integrate {@link ClientFactoryBean} into the life cycle of spring beans,
 * {@link ClientFactoryBean} also needs to implement the following interfaces:
 * <ul>
 *     <li>{@link InitializingBean}</li>
 *     <li>{@link ApplicationContextAware}</li>
 *     <li>{@link DisposableBean}</li>
 * </ul>
 *
 * @since 1.5.1
 * @deprecated With {@link cn.hippo4j.config.service.ThreadPoolAdapterService} structure, FactoryBean is not the best choice
 */
@Deprecated
public class ClientFactoryBean implements FactoryBean<Object>, InitializingBean, ApplicationContextAware, DisposableBean {

    /**
     * Application name or address string. If it is an address string, it must be in ip:port format
     */
    private String applicationName;

    /**
     * The adapter name in the container needs to be used with applicationName
     * to get the real server address. If it is null or the address information
     * cannot be found, applicationName is treated as an address string
     */
    private String discoveryAdapterName;

    /**
     * The adaptation interface for obtaining ip information in the registry is used together with
     * {@link #discoveryAdapterName}, so that the adapter implementation can be obtained in the container
     * during the initialization phase
     */
    private DiscoveryAdapter discoveryAdapter;

    /**
     * the channel handler, To ensure the security and reliability of netty calls,
     * {@link ChannelHandler} must be identified by {@link ChannelHandler.Sharable}
     */
    private ChannelHandler[] handlers;

    /**
     * Type of the proxy interface
     */
    private Class<?> cls;

    /**
     * Container Context
     */
    private ApplicationContext applicationContext;

    /**
     * InetSocketAddress, It is usually converted from {@link #applicationName} and {@link #discoveryAdapter}
     */
    InetSocketAddress address;

    public ClientFactoryBean(String applicationName, String discoveryAdapterName, Class<?> cls) {
        Assert.notNull(applicationName);
        Assert.notNull(cls);
        this.applicationName = applicationName;
        this.discoveryAdapterName = discoveryAdapterName;
        this.cls = cls;
    }

    @Override
    public Object getObject() throws Exception {
        this.address = Optional.ofNullable(applicationName)
                .map(a -> discoveryAdapter.getSocketAddress(a))
                .map(a -> {
                    String[] addressStr = applicationName.split(":");
                    if (addressStr.length < 2) {
                        throw new ConnectionException("Failed to connect to the server because the IP address is invalid. Procedure");
                    }
                    return InetSocketAddress.createUnresolved(addressStr[0], Integer.parseInt(addressStr[1]));
                })
                .orElseThrow(() -> new ConnectionException("Failed to connect to the server because the IP address is invalid. Procedure"));
        Client client = NettyClientSupport.getClient(this.address, new NettyClientPoolHandler(handlers));
        return NettyProxyCenter.createProxy(client, cls, this.address);
    }

    @Override
    public Class<?> getObjectType() {
        return cls;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.discoveryAdapter = Optional.ofNullable(discoveryAdapterName)
                .map(s -> (DiscoveryAdapter) applicationContext.getBean(discoveryAdapterName))
                .orElse(null);
    }

    @Override
    public void destroy() throws Exception {
        Optional.ofNullable(this.address)
                .ifPresent(a -> NettyClientSupport.closeClient(this.address));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ClientFactoryBean applicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public ClientFactoryBean discoveryAdapterName(String discoveryAdapterName) {
        this.discoveryAdapterName = discoveryAdapterName;
        return this;
    }

    public ClientFactoryBean cls(Class<?> cls) {
        this.cls = cls;
        return this;
    }

    public ClientFactoryBean handlers(ChannelHandler[] handlers) {
        this.handlers = handlers;
        return this;
    }

}
