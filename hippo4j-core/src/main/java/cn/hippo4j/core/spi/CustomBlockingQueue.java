package cn.hippo4j.core.spi;

import java.util.concurrent.BlockingQueue;

/**
 * Custom blockingQueue.
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
     * Adapt hippo4j core blocking queue.
     *
     * @return
     */
    default String getName() {
        return "";
    }

    /**
     * Get custom blocking queue.
     *
     * @return
     */
    BlockingQueue generateBlockingQueue();

}
