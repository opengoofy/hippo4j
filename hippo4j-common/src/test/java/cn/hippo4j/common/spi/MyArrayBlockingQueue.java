package cn.hippo4j.common.spi;

import cn.hippo4j.common.executor.support.CustomBlockingQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * SPI whit generic type test.
 */
public class MyArrayBlockingQueue implements CustomBlockingQueue<Runnable> {

    @Override
    public Integer getType() {
        return null;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public BlockingQueue<Runnable> generateBlockingQueue() {
        return new LinkedBlockingQueue<>(20);
    }
}
