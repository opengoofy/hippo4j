package io.dynamic.threadpool.starter.toolkit.thread;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 可调整大小的阻塞队列
 *
 * @author chen.ma
 * @date 2021/6/20 14:24
 */
@Slf4j
public class ResizableCapacityLinkedBlockIngQueue<E> extends LinkedBlockingQueue<E> {

    public ResizableCapacityLinkedBlockIngQueue(int capacity) {
        super(capacity);
    }

    public synchronized boolean setCapacity(Integer capacity) {
        boolean successFlag = true;
        try {
            ReflectUtil.setFieldValue(this, "capacity", capacity);
        } catch (Exception ex) {
            // ignore
            log.error("动态修改阻塞队列大小失败.", ex);
            successFlag = false;
        }
        return successFlag;
    }
}
