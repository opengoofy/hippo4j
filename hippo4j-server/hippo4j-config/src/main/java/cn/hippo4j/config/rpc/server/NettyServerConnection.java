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

package cn.hippo4j.config.rpc.server;

import cn.hippo4j.config.rpc.coder.NettyDecoder;
import cn.hippo4j.config.rpc.coder.NettyEncoder;
import cn.hippo4j.config.rpc.handler.NettyServerTakeHandler;
import cn.hippo4j.config.rpc.process.ActivePostProcess;
import cn.hippo4j.config.rpc.support.Instance;
import cn.hippo4j.common.toolkit.Assert;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

/**
 * adapter to the netty server
 */
@Slf4j
public class NettyServerConnection implements ServerConnection {

    Integer port;
    EventLoopGroup leader;
    EventLoopGroup worker;
    Class<? extends ServerChannel> socketChannelCls = NioServerSocketChannel.class;
    List<ActivePostProcess> processes;
    Instance instance;
    ChannelFuture future;

    public NettyServerConnection(EventLoopGroup leader, EventLoopGroup worker, List<ActivePostProcess> processes, Instance instance) {
        Assert.notNull(processes);
        Assert.notNull(instance);
        Assert.notNull(leader);
        Assert.notNull(worker);
        this.leader = leader;
        this.worker = worker;
        this.processes = processes;
        this.instance = instance;
    }

    public NettyServerConnection(EventLoopGroup leader, EventLoopGroup worker, Instance instance) {
        this(leader, worker, new LinkedList<>(), instance);
    }

    public NettyServerConnection(List<ActivePostProcess> processes, Instance instance) {
        this(new NioEventLoopGroup(), new NioEventLoopGroup(), processes, instance);
    }

    public NettyServerConnection(Instance instance) {
        this(new NioEventLoopGroup(), new NioEventLoopGroup(), new LinkedList<>(), instance);
    }

    @Override
    public void bind(int port) {
        ServerBootstrap server = new ServerBootstrap();
        server.group(leader, worker)
                .channel(socketChannelCls)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyDecoder(ClassResolvers.cacheDisabled(null)));
                        ch.pipeline().addLast(new NettyEncoder());
                        ch.pipeline().addLast(new NettyServerTakeHandler(processes, instance));
                    }
                });
        try {
            this.future = server.bind(port);
            log.info("The server is started and can receive requests. The listening port is {}", port);
            this.port = port;
            this.future.channel().closeFuture().sync();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() {
        if (port == null) {
            return;
        }
        leader.shutdownGracefully();
        worker.shutdownGracefully();
        this.future.channel().close();
        log.info("The server is shut down and no more requests are received. The release port is {}", port);
    }
}
