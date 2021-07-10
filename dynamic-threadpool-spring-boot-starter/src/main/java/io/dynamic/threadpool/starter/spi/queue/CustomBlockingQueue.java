package io.dynamic.threadpool.starter.spi.queue;

import java.util.concurrent.BlockingQueue;

/**
 * 自定义阻塞队列
 *
 * @author chen.ma
 * @date 2021/7/11 00:51
 */
public interface CustomBlockingQueue {

    /**
     * 获取类型
     *
     * @return
     */
    Integer getType();

    /**
     * 生成阻塞队列
     *
     * @return
     */
    BlockingQueue generateBlockingQueue();

}
