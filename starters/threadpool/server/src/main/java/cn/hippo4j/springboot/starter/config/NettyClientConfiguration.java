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

import cn.hippo4j.common.constant.Constants;
import cn.hippo4j.springboot.starter.monitor.send.netty.NettyConnectSender;
import cn.hippo4j.springboot.starter.monitor.send.MessageSender;
import cn.hippo4j.springboot.starter.remote.ServerNettyAgent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Netty client configuration.
 */
@ConditionalOnProperty(prefix = Constants.CONFIGURATION_PROPERTIES_PREFIX, name = "report-type", havingValue = "netty")
public class NettyClientConfiguration {

    @Bean
    @SuppressWarnings("all")
    public ServerNettyAgent serverNettyAgent(BootstrapProperties properties) {
        return new ServerNettyAgent(properties);
    }

    @Bean
    public MessageSender messageSender(ServerNettyAgent serverNettyAgent) {
        return new NettyConnectSender(serverNettyAgent);
    }
}
