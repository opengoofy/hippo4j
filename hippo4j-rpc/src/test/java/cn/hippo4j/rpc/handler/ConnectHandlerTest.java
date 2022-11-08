package cn.hippo4j.rpc.handler;

import cn.hippo4j.rpc.client.NettyClientConnection;
import cn.hippo4j.rpc.client.RPCClient;
import cn.hippo4j.rpc.discovery.*;
import cn.hippo4j.rpc.server.AbstractNettyServerConnection;
import cn.hippo4j.rpc.server.RPCServer;
import cn.hippo4j.rpc.support.NettyProxyCenter;
import io.netty.channel.pool.ChannelPoolHandler;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ConnectHandlerTest {

    @Test
    public void handlerTest() {
        // server
        Class<InstanceServerLoader> cls = InstanceServerLoader.class;
        ClassRegistry.put(cls.getName(), cls);
        ServerPort port = () -> 8888;
        Instance instance = new DefaultInstance();
        NettyServerTakeHandler serverHandler = new NettyServerTakeHandler(instance);
        AbstractNettyServerConnection connection = new AbstractNettyServerConnection(serverHandler);
        RPCServer rpcServer = new RPCServer(connection, port);
        CompletableFuture.runAsync(rpcServer::bind);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ChannelPoolHandler channelPoolHandler = new AbstractNettyClientPoolHandler(new NettyClientTakeHandler());
        NettyClientConnection clientConnection = new NettyClientConnection("localhost", port, channelPoolHandler);
        RPCClient rpcClient = new RPCClient(clientConnection);
        InstanceServerLoader loader = NettyProxyCenter.getProxy(rpcClient, cls, "localhost", port);
        String name = loader.getName();
        Assert.assertEquals("name", name);
    }

}