package cn.hippo4j.config.netty;

import cn.hippo4j.config.config.ServerBootstrapProperties;
import cn.hippo4j.config.monitor.QueryMonitorExecuteChoose;
import cn.hippo4j.config.service.biz.HisRunDataService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Netty MonitorNettyServer
 *
 * @author lk
 * @date 2022/06/18
 */
@Slf4j
@AllArgsConstructor
public class MonitorNettyServer {

    private ServerBootstrapProperties serverBootstrapProperties;

    private HisRunDataService hisRunDataService;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workGroup;

    @PostConstruct
    public void nettyServerInit(){
        new Thread(() -> {
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(bossGroup,workGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        //childHandler的任务由workGroup来执行
                        //如果是handler，则由bossGroup来执行
                        .childHandler(new ChannelInitializer<SocketChannel>(){
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
                log.error("nettyServerInit error",e);
            }
        },"nettyServerInit thread").start();
    }

    @PreDestroy
    public void destroy(){
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
