package cn.hippo4j.common.executor.support;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author hongdan.qin
 * @date 2022/12/6 18:09
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
