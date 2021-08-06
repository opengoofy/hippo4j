package com.github.dynamic.threadpool.starter.spi;

import java.util.concurrent.BlockingQueue;

/**
 * Custom BlockingQueue.
 *
 * @author chen.ma
 * @date 2021/7/11 00:51
 */
public interface CustomBlockingQueue {

    /**
     * Gets the custom blocking queue type.
     *
     * @return
     */
    Integer getType();

    /**
     * Get custom blocking queue.
     *
     * @return
     */
    BlockingQueue generateBlockingQueue();

}
