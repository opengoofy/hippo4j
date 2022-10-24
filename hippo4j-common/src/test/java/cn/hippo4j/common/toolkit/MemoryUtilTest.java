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

import org.junit.Assert;
import org.junit.Test;

public class MemoryUtilTest {

    @Test
    public void heapMemoryUsed() {
        long memoryUsed = MemoryUtil.heapMemoryUsed();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void heapMemoryMax() {
        long memoryUsed = MemoryUtil.heapMemoryMax();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void heapMemoryFree() {
        long memoryUsed = MemoryUtil.heapMemoryFree();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void noHeapMemoryUsed() {
        long memoryUsed = MemoryUtil.noHeapMemoryUsed();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void noHeapMemoryMax() {
        long memoryUsed = MemoryUtil.noHeapMemoryMax();
        Assert.assertNotEquals(0, memoryUsed);
    }

    @Test
    public void noHeapMemoryFree() {
        long memoryUsed = MemoryUtil.noHeapMemoryFree();
        Assert.assertNotEquals(0, memoryUsed);
    }
}