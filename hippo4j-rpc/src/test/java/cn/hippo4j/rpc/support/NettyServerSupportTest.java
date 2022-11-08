package cn.hippo4j.rpc.support;

import cn.hippo4j.rpc.discovery.InstanceServerLoader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class NettyServerSupportTest {

    @Test
    public void bind() throws IOException {
        NettyServerSupport support = new NettyServerSupport(() -> 8888, InstanceServerLoader.class);
        CompletableFuture.runAsync(support::bind);
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertTrue(support.isActive());
        support.close();
        Assert.assertFalse(support.isActive());
    }

}