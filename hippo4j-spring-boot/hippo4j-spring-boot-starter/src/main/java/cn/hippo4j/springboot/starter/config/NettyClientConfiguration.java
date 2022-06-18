package cn.hippo4j.springboot.starter.config;

import cn.hippo4j.springboot.starter.monitor.netty.NettyConnectSender;
import cn.hippo4j.springboot.starter.monitor.send.MessageSender;
import cn.hippo4j.springboot.starter.remote.ServerNettyAgent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Netty ClientCon figuration
 *
 * @author lk
 * @date 2022/6/18
 */
@ConditionalOnProperty(prefix = BootstrapProperties.PREFIX, name = "report-type", matchIfMissing = false, havingValue = "netty")
public class NettyClientConfiguration {

    @Bean
    @SuppressWarnings("all")
    public ServerNettyAgent serverNettyAgent(BootstrapProperties properties) {
        return new ServerNettyAgent(properties);
    }

    @Bean
    public MessageSender nettyConnectSender(ServerNettyAgent serverNettyAgent){
        return new NettyConnectSender(serverNettyAgent);
    }
}
