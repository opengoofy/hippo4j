package cn.hippo4j.console.netty;

import cn.hippo4j.config.monitor.QueryMonitorExecuteChoose;
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

/**
 * Netty MonitorNettyServer
 *
 * @author lk
 * @date 2022/06/18
 */
@Slf4j
@AllArgsConstructor
@Component
public class MonitorNettyServer {

    private final QueryMonitorExecuteChoose queryMonitorExecuteChoose;

    private final ThreadPoolTaskExecutor monitorThreadPoolTaskExecutor;

    @PostConstruct
    public void nettyServerInit(){
        new Thread(() -> {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workGroup = new NioEventLoopGroup();

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
                                pipeline.addLast(new ServerHandler(queryMonitorExecuteChoose,
                                        monitorThreadPoolTaskExecutor));
                            }
                        });
                ChannelFuture channelFuture = serverBootstrap.bind(8899).sync();
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        },"netty-server-thread").start();
    }
}
