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

package cn.hippo4j.config.service.biz.impl;

import cn.hippo4j.config.model.ConfigAllInfo;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

import static cn.hippo4j.common.executor.support.BlockingQueueTypeEnum.*;

/**
 * Test for ConfigServiceImpl.getQueueCapacityByType method
 * Verifies queue capacity handling for both built-in and custom queue types
 */
public class ConfigServiceQueueCapacityTest {

    private static final int DEFAULT_QUEUE_CAPACITY = 1024;

    /**
     * Use reflection to invoke private method getQueueCapacityByType
     */
    private Integer invokeGetQueueCapacityByType(ConfigAllInfo config) throws Exception {
        ConfigServiceImpl configService = new ConfigServiceImpl(null, null, null, null);
        Method method = ConfigServiceImpl.class.getDeclaredMethod("getQueueCapacityByType", ConfigAllInfo.class);
        method.setAccessible(true);
        return (Integer) method.invoke(configService, config);
    }

    /**
     * Test Case 1: Built-in queue with valid capacity
     * Expected: Return the configured capacity
     */
    @Test
    public void testBuiltInQueueWithValidCapacity() throws Exception {
        System.out.println("========== Test Case 1: Built-in queue with valid capacity ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(ARRAY_BLOCKING_QUEUE.getType());
        config.setCapacity(2048);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return configured capacity", Integer.valueOf(2048), result);
        System.out.println("Built-in queue with capacity 2048 returns: " + result);
    }

    /**
     * Test Case 2: Built-in queue with null capacity
     * Expected: Return default capacity (1024)
     */
    @Test
    public void testBuiltInQueueWithNullCapacity() throws Exception {
        System.out.println("\n========== Test Case 2: Built-in queue with null capacity ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(LINKED_BLOCKING_QUEUE.getType());
        config.setCapacity(null);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return default capacity", Integer.valueOf(DEFAULT_QUEUE_CAPACITY), result);
        System.out.println("Built-in queue with null capacity returns default: " + result);
    }

    /**
     * Test Case 3: Built-in queue with zero capacity
     * Expected: Return default capacity (1024)
     */
    @Test
    public void testBuiltInQueueWithZeroCapacity() throws Exception {
        System.out.println("\n========== Test Case 3: Built-in queue with zero capacity ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(LINKED_BLOCKING_DEQUE.getType());
        config.setCapacity(0);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return default capacity", Integer.valueOf(DEFAULT_QUEUE_CAPACITY), result);
        System.out.println("Built-in queue with zero capacity returns default: " + result);
    }

    /**
     * Test Case 4: LinkedTransferQueue with any capacity
     * Expected: Return Integer.MAX_VALUE (LinkedTransferQueue doesn't support capacity limit)
     */
    @Test
    public void testLinkedTransferQueueReturnsMaxValue() throws Exception {
        System.out.println("\n========== Test Case 4: LinkedTransferQueue returns MAX_VALUE ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(LINKED_TRANSFER_QUEUE.getType());
        config.setCapacity(1024);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return Integer.MAX_VALUE", Integer.valueOf(Integer.MAX_VALUE), result);
        System.out.println("LinkedTransferQueue returns MAX_VALUE: " + result);
    }

    /**
     * Test Case 5: ResizableLinkedBlockingQueue with valid capacity
     * Expected: Return the configured capacity
     */
    @Test
    public void testResizableQueueWithValidCapacity() throws Exception {
        System.out.println("\n========== Test Case 5: ResizableLinkedBlockingQueue with valid capacity ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(RESIZABLE_LINKED_BLOCKING_QUEUE.getType());
        config.setCapacity(4096);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return configured capacity", Integer.valueOf(4096), result);
        System.out.println("ResizableLinkedBlockingQueue with capacity 4096 returns: " + result);
    }

    /**
     * Test Case 6: Custom queue type with valid capacity
     * Expected: Return the configured capacity
     */
    @Test
    public void testCustomQueueWithValidCapacity() throws Exception {
        System.out.println("\n========== Test Case 6: Custom queue with valid capacity ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(10001); // Custom queue type ID
        config.setCapacity(512);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return configured capacity", Integer.valueOf(512), result);
        System.out.println("Custom queue with capacity 512 returns: " + result);
    }

    /**
     * Test Case 7: Custom queue type with null capacity
     * Expected: Return default capacity (1024)
     * This is the FIX - previously this would return null
     */
    @Test
    public void testCustomQueueWithNullCapacity() throws Exception {
        System.out.println("\n========== Test Case 7: Custom queue with null capacity (FIX) ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(10001); // Custom queue type ID
        config.setCapacity(null);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return default capacity for custom queue",
                Integer.valueOf(DEFAULT_QUEUE_CAPACITY), result);
        System.out.println("Custom queue with null capacity returns default: " + result);
        System.out.println("   This is the FIX - previously would return null!");
    }

    /**
     * Test Case 8: Custom queue type with zero capacity
     * Expected: Return default capacity (1024)
     * This is the FIX - previously this would return 0
     */
    @Test
    public void testCustomQueueWithZeroCapacity() throws Exception {
        System.out.println("\n========== Test Case 8: Custom queue with zero capacity (FIX) ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(20001); // Another custom queue type ID
        config.setCapacity(0);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return default capacity for custom queue",
                Integer.valueOf(DEFAULT_QUEUE_CAPACITY), result);
        System.out.println("Custom queue with zero capacity returns default: " + result);
        System.out.println("   This is the FIX - previously would return 0!");
    }

    /**
     * Test Case 9: Multiple custom queue types
     * Expected: All custom queue types should be handled consistently
     */
    @Test
    public void testMultipleCustomQueueTypes() throws Exception {
        System.out.println("\n========== Test Case 9: Multiple custom queue types consistency ==========");

        int[] customQueueTypes = {10001, 20001, 30001, 99999};

        for (int queueType : customQueueTypes) {
            ConfigAllInfo config = new ConfigAllInfo();
            config.setQueueType(queueType);
            config.setCapacity(null);

            Integer result = invokeGetQueueCapacityByType(config);

            Assert.assertEquals("All custom queues should return default capacity",
                    Integer.valueOf(DEFAULT_QUEUE_CAPACITY), result);
            System.out.println("Custom queue type " + queueType + " with null capacity returns: " + result);
        }
    }

    /**
     * Test Case 10: Boundary test - very large capacity
     * Expected: Return the configured large capacity
     */
    @Test
    public void testVeryLargeCapacity() throws Exception {
        System.out.println("\n========== Test Case 10: Very large capacity ==========");

        ConfigAllInfo config = new ConfigAllInfo();
        config.setQueueType(ARRAY_BLOCKING_QUEUE.getType());
        config.setCapacity(1000000);

        Integer result = invokeGetQueueCapacityByType(config);

        Assert.assertEquals("Should return configured large capacity", Integer.valueOf(1000000), result);
        System.out.println("Built-in queue with capacity 1000000 returns: " + result);
    }

    /**
     * Test Case 11: Verify consistency - all built-in queues with null capacity
     * Expected: All should return default capacity (1024)
     */
    @Test
    public void testAllBuiltInQueuesWithNullCapacity() throws Exception {
        System.out.println("\n========== Test Case 11: All built-in queues with null capacity ==========");

        Integer[] builtInQueueTypes = {
                ARRAY_BLOCKING_QUEUE.getType(),
                LINKED_BLOCKING_QUEUE.getType(),
                LINKED_BLOCKING_DEQUE.getType(),
                PRIORITY_BLOCKING_QUEUE.getType(),
                RESIZABLE_LINKED_BLOCKING_QUEUE.getType()
        };

        for (Integer queueType : builtInQueueTypes) {
            ConfigAllInfo config = new ConfigAllInfo();
            config.setQueueType(queueType);
            config.setCapacity(null);

            Integer result = invokeGetQueueCapacityByType(config);

            Assert.assertEquals("All built-in queues should return default capacity",
                    Integer.valueOf(DEFAULT_QUEUE_CAPACITY), result);
            System.out.println("Queue type " + queueType + " with null capacity returns default: " + result);
        }
    }
}
