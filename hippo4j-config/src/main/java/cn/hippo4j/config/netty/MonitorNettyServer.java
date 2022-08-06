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

package cn.hippo4j.config.netty;

import cn.hippo4j.config.config.ServerBootstrapProperties;
import cn.hippo4j.config.service.biz.HisRunDataService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Netty monitor netty server.
 */
@Slf4j
@AllArgsConstructor
public class MonitorNettyServer {

    private ServerBootstrapProperties serverBootstrapProperties;

    private HisRunDataService hisRunDataService;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workGroup;

    @PostConstruct
    public void nettyServerInit() {
        new Thread(() -> {
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup, workGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        // BossGroup the Thread group is responsible for connection events.
                        // WorkGroup the thread group is responsible for read and write events.
                        .childHandler(new ChannelInitializer<SocketChannel>() {

                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline pipeline = ch.pipeline();
                                pipeline.addLast(new ObjectEncoder());
                                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE,
                                        ClassResolvers.cacheDisabled(null)));
                                pipeline.addLast(new ServerHandler(hisRunDataService));
                            }
                        });
                ChannelFuture channelFuture = serverBootstrap.bind(Integer.parseInt(serverBootstrapProperties.getNettyServerPort())).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (Exception e) {
                log.error("nettyServerInit error", e);
            }
        }, "nettyServerInit thread").start();
    }

    @PreDestroy
    public void destroy() {
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
