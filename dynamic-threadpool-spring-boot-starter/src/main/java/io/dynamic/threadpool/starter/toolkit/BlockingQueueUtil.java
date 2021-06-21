package io.dynamic.threadpool.starter.toolkit;

import io.dynamic.threadpool.starter.core.ResizableCapacityLinkedBlockIngQueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 阻塞队列工具类
 *
 * @author chen.ma
 * @date 2021/6/20 16:50
 */
public class BlockingQueueUtil {

    public static BlockingQueue createBlockingQueue(Integer type, Integer capacity) {
        BlockingQueue blockingQueue = null;
        switch (type) {
            case 1:
                blockingQueue = new ArrayBlockingQueue(capacity);
                break;
            case 2:
                blockingQueue = new LinkedBlockingQueue(capacity);
                break;
            case 3:
                blockingQueue = new ResizableCapacityLinkedBlockIngQueue(capacity);
            default:
                break;
        }
        return blockingQueue;
    }
}
