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

package cn.hippo4j.springboot.starter.config;

import cn.hippo4j.adapter.base.ThreadPoolAdapterApi;
import cn.hippo4j.common.api.WebThreadPoolApi;
import cn.hippo4j.common.api.WebThreadPoolRunStateApi;
import cn.hippo4j.rpc.discovery.ServerPort;
import cn.hippo4j.rpc.discovery.SpringContextInstance;
import cn.hippo4j.rpc.handler.NettyServerTakeHandler;
import cn.hippo4j.rpc.server.NettyServerConnection;
import cn.hippo4j.rpc.server.Server;
import cn.hippo4j.rpc.support.NettyServerSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyServerConfiguration implements ApplicationRunner, EnvironmentAware {

    Environment environment;

    private static final Class<?>[] classes = {
            WebThreadPoolApi.class,
            ThreadPoolAdapterApi.class,
            WebThreadPoolRunStateApi.class
    };

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Integer port = environment.getProperty("spring.dynamic.thread-pool.local-server-port", Integer.class);
        ServerPort serverPort = () -> port == null ? 16691 : port;
        NettyServerTakeHandler handler = new NettyServerTakeHandler(new SpringContextInstance());
        try (
                NettyServerConnection connection = new NettyServerConnection(handler);
                Server server = new NettyServerSupport(serverPort, connection, classes)) {
            if (log.isInfoEnabled()) {
                log.info("Netty server started, binding to port {}", serverPort.getPort());
            }
            server.bind();
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
