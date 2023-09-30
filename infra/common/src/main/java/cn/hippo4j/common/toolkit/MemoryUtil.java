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

package cn.hippo4j.common.toolkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * memory util<br>
 * the obtained information is not real time effective, after a long wait, please get it again
 *
 * @author liuwenhao
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryUtil {

    static MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    static MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
    static MemoryUsage noHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

    /**
     * get used memory in heap
     *
     * @return long bytes
     */
    public static long heapMemoryUsed() {
        return heapMemoryUsage.getUsed();
    }

    /**
     * get max memory in heap
     *
     * @return long bytes
     */
    public static long heapMemoryMax() {
        return heapMemoryUsage.getMax();
    }

    /**
     * get free memory in heap
     *
     * @return long bytes
     */
    public static long heapMemoryFree() {
        return Math.subtractExact(heapMemoryMax(), heapMemoryUsed());
    }

    /**
     * get used memory in no-heap
     *
     * @return long bytes
     */
    public static long noHeapMemoryUsed() {
        return noHeapMemoryUsage.getUsed();
    }

    /**
     * get max memory in no-heap
     *
     * @return long bytes
     */
    public static long noHeapMemoryMax() {
        return noHeapMemoryUsage.getMax();
    }

    /**
     * get free memory in no-heap
     *
     * @return long bytes
     */
    public static long noHeapMemoryFree() {
        return Math.subtractExact(noHeapMemoryMax(), noHeapMemoryUsed());
    }

}
