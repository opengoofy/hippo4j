package cn.hippo4j.config.config;

import cn.hippo4j.config.netty.MonitorNettyServer;
import cn.hippo4j.config.service.biz.HisRunDataService;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyServerConfig {

    @Bean
    public EventLoopGroup bossGroup(){
        return new NioEventLoopGroup();
    }

    @Bean
    public EventLoopGroup workGroup(){
        return new NioEventLoopGroup();
    }

    @Bean
    public MonitorNettyServer monitorNettyServer(ServerBootstrapProperties serverBootstrapProperties,
                                                 HisRunDataService hisRunDataService,
                                                 EventLoopGroup bossGroup,
                                                 EventLoopGroup workGroup){
        return new MonitorNettyServer(serverBootstrapProperties,hisRunDataService,bossGroup,workGroup);
    }
}


