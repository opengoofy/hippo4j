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

package cn.hippo4j.springboot.starter.remote;

import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Server netty agent.
 */
public class ServerNettyAgent {

    private final BootstrapProperties dynamicThreadPoolProperties;

    private final ServerListManager serverListManager;

    private EventLoopGroup eventLoopGroup;

    public ServerNettyAgent(BootstrapProperties properties) {
        this.dynamicThreadPoolProperties = properties;
        this.serverListManager = new ServerListManager(dynamicThreadPoolProperties);
        this.eventLoopGroup = new NioEventLoopGroup();
    }

    public EventLoopGroup getEventLoopGroup() {
        return eventLoopGroup;
    }

    public String getNettyServerAddress() {
        return serverListManager.getCurrentServerAddr().split(":")[1].replace("//", "");
    }

    public Integer getNettyServerPort() {
        return Integer.parseInt(serverListManager.getNettyServerPort());
    }
}
