package cn.hippo4j.starter.toolkit.thread;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resizable capacity linkedBlockIngQueue.
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
        /**
         * TODO：后续考虑切换 Rabbitmq VariableLinkedBlockingQueue
         */
        try {
            int oldCapacity = (int) ReflectUtil.getFieldValue(this, "capacity");
            AtomicInteger count = (AtomicInteger) ReflectUtil.getFieldValue(this, "count");
            int size = count.get();

            ReflectUtil.setFieldValue(this, "capacity", capacity);
            if (capacity > size && size >= oldCapacity) {
                ReflectUtil.invoke(this, "signalNotFull");
            }
        } catch (Exception ex) {
            // ignore
            log.error("Dynamic modification of blocking queue size failed.", ex);
            successFlag = false;
        }

        return successFlag;
    }

}
