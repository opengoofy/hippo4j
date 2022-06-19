package cn.hippo4j.springboot.starter.remote;

import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Server Netty Agent
 *
 * @author lk
 * @date 2022/6/18
 */
public class ServerNettyAgent {

    private final BootstrapProperties dynamicThreadPoolProperties;

    private final ServerListManager serverListManager;

    private EventLoopGroup eventLoopGroup;

    public ServerNettyAgent(BootstrapProperties properties){
        this.dynamicThreadPoolProperties = properties;
        this.serverListManager = new ServerListManager(dynamicThreadPoolProperties);
        this.eventLoopGroup = new NioEventLoopGroup();
    }

    public EventLoopGroup getEventLoopGroup(){
        return eventLoopGroup;
    }

    public String getNettyServerAddress() {
        return serverListManager.getCurrentServerAddr().split(":")[1].replace("//","");
    }

    public Integer getNettyServerPort(){
        return Integer.parseInt(serverListManager.getNettyServerPort());
    }
}
