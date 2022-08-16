/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.common.executor.support;

import cn.hutool.core.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resizable capacity linked-blocking-queue. Options Rabbitmq VariableLinkedBlockingQueue
 */
@Slf4j
public class ResizableCapacityLinkedBlockingQueue<E> extends LinkedBlockingQueue<E> {

    public ResizableCapacityLinkedBlockingQueue(int capacity) {
        super(capacity);
    }

    public synchronized boolean setCapacity(Integer capacity) {
        boolean successFlag = true;
        try {
            int oldCapacity = (int) ReflectUtil.getFieldValue(this, "capacity");
            AtomicInteger count = (AtomicInteger) ReflectUtil.getFieldValue(this, "count");
            int size = count.get();

            ReflectUtil.setFieldValue(this, "capacity", capacity);
            if (capacity > size && size >= oldCapacity) {
                ReflectUtil.invoke(this, "signalNotFull");
            }
        } catch (Exception ex) {
            log.error("Dynamic modification of blocking queue size failed.", ex);
            successFlag = false;
        }
        return successFlag;
    }

}
