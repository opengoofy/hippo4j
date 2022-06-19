package cn.hippo4j.springboot.starter.config;

import cn.hippo4j.springboot.starter.monitor.send.netty.NettyConnectSender;
import cn.hippo4j.springboot.starter.monitor.send.MessageSender;
import cn.hippo4j.springboot.starter.remote.ServerNettyAgent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

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
    public MessageSender messageSender(ServerNettyAgent serverNettyAgent){
        return new NettyConnectSender(serverNettyAgent);
    }
}
