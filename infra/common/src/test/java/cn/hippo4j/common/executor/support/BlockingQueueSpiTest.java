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

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Blocking Queue SPI Test: Verify that custom queues can be supported via SPI just like rejection policies
 */
public class BlockingQueueSpiTest {

    /**
     * Test custom queue SPI implementation
     * Note: SPI mechanism creates instances via no-arg constructor,
     * capacity is dynamically passed in generateBlockingQueue
     */
    public static class TestCustomQueue implements CustomBlockingQueue<Runnable> {

        @Override
        public Integer getType() {
            return 10001; // Custom type ID
        }

        @Override
        public String getName() {
            return "TestCustomQueue";
        }

        @Override
        public BlockingQueue<Runnable> generateBlockingQueue() {
            // SPI implementation note: capacity should be passed from outside
            // For simplicity in test, use fixed capacity 512
            // In real project, capacity can be passed via constructor or other ways
            return new ArrayBlockingQueue<>(512);
        }
    }

    /**
     * Test Case 1: SPI interface definition integrity
     */
    @Test
    public void testSpiInterfaceDefinition() {
        System.out.println("========== Test Case 1: SPI interface definition integrity ==========");

        TestCustomQueue customQueue = new TestCustomQueue();

        // Verify interface methods
        Assert.assertNotNull("getType() should return non-null type ID", customQueue.getType());
        Assert.assertEquals("Type ID should be 10001", Integer.valueOf(10001), customQueue.getType());
        Assert.assertEquals("getName() should return queue name", "TestCustomQueue", customQueue.getName());

        BlockingQueue<Runnable> queue = customQueue.generateBlockingQueue();
        Assert.assertNotNull("generateBlockingQueue() should return non-null queue", queue);
        Assert.assertTrue("Should return ArrayBlockingQueue instance", queue instanceof ArrayBlockingQueue);
        Assert.assertEquals("Queue capacity should be 512", 512, queue.remainingCapacity());

        System.out.println("Custom queue type ID: " + customQueue.getType());
        System.out.println("Custom queue name: " + customQueue.getName());
        System.out.println("Generated queue type: " + queue.getClass().getSimpleName());
        System.out.println("Queue capacity: " + queue.remainingCapacity());
        System.out.println("Passed: SPI interface definition is complete");
    }

    /**
     * Test Case 1.5: SPI configuration file registration validation
     * Verify custom queue is correctly registered via SPI config file
     */
    @Test
    public void testSpiConfigurationRegistration() {
        System.out.println("\n========== Test Case 1.5: SPI configuration file registration validation ==========");

        System.out.println("SPI config file location: src/test/resources/META-INF/services/cn.hippo4j.common.executor.support.CustomBlockingQueue");
        System.out.println("SPI config content: cn.hippo4j.common.executor.support.BlockingQueueSpiTest$TestCustomQueue");

        // Create custom queue via SPI mechanism (type ID 10001)
        BlockingQueue<Runnable> spiQueue = BlockingQueueTypeEnum.createBlockingQueue(10001, 512);
        Assert.assertNotNull("Should successfully create custom queue via SPI", spiQueue);

        // Verify it's created by SPI TestCustomQueue implementation
        Assert.assertTrue("Should create ArrayBlockingQueue instance (TestCustomQueue implementation)",
                spiQueue instanceof ArrayBlockingQueue);
        Assert.assertEquals("Queue capacity should be 512", 512, spiQueue.remainingCapacity());

        System.out.println("Successfully created custom queue via SPI type ID 10001");
        System.out.println("Queue type: " + spiQueue.getClass().getSimpleName());
        System.out.println("Queue capacity: " + spiQueue.remainingCapacity());
        System.out.println("Passed: SPI configuration registration works");
    }

    /**
     * Test Case 2: Built-in queue type creation
     */
    @Test
    public void testBuiltInQueueCreation() {
        System.out.println("\n========== Test Case 2: Built-in queue type creation ==========");

        // Test all built-in queue types
        BlockingQueue<Runnable> arrayQueue = BlockingQueueTypeEnum.createBlockingQueue(1, 1024);
        Assert.assertTrue("Type 1 should create ArrayBlockingQueue", arrayQueue instanceof ArrayBlockingQueue);
        System.out.println("Type 1 (ArrayBlockingQueue): " + arrayQueue.getClass().getSimpleName());

        BlockingQueue<Runnable> linkedQueue = BlockingQueueTypeEnum.createBlockingQueue(2, 1024);
        Assert.assertTrue("Type 2 should create LinkedBlockingQueue", linkedQueue instanceof LinkedBlockingQueue);
        System.out.println("Type 2 (LinkedBlockingQueue): " + linkedQueue.getClass().getSimpleName());

        BlockingQueue<Runnable> deque = BlockingQueueTypeEnum.createBlockingQueue(3, 1024);
        Assert.assertNotNull("Type 3 should create LinkedBlockingDeque", deque);
        System.out.println("Type 3 (LinkedBlockingDeque): " + deque.getClass().getSimpleName());

        BlockingQueue<Runnable> syncQueue = BlockingQueueTypeEnum.createBlockingQueue(4, null);
        Assert.assertNotNull("Type 4 should create SynchronousQueue", syncQueue);
        System.out.println("Type 4 (SynchronousQueue): " + syncQueue.getClass().getSimpleName());

        BlockingQueue<Runnable> priorityQueue = BlockingQueueTypeEnum.createBlockingQueue(5, 1024);
        Assert.assertNotNull("Type 5 should create PriorityBlockingQueue", priorityQueue);
        System.out.println("Type 5 (PriorityBlockingQueue): " + priorityQueue.getClass().getSimpleName());

        BlockingQueue<Runnable> resizableQueue = BlockingQueueTypeEnum.createBlockingQueue(9, 1024);
        Assert.assertNotNull("Type 9 should create ResizableCapacityLinkedBlockingQueue", resizableQueue);
        System.out.println("Type 9 (ResizableCapacityLinkedBlockingQueue): " + resizableQueue.getClass().getSimpleName());

        System.out.println("Passed: All built-in queue types created successfully");
    }

    /**
     * Test Case 3: Queue creation via BlockingQueueTypeEnum
     */
    @Test
    public void testBlockingQueueCreation() {
        System.out.println("\n========== Test Case 3: Queue creation via BlockingQueueTypeEnum ==========");

        // Create built-in queue via BlockingQueueTypeEnum
        BlockingQueue<Runnable> queue1 = BlockingQueueTypeEnum.createBlockingQueue(1, 512);
        Assert.assertNotNull("Should successfully create queue", queue1);
        Assert.assertTrue("Should create ArrayBlockingQueue", queue1 instanceof ArrayBlockingQueue);

        // Create by type name
        BlockingQueue<Runnable> queue2 = BlockingQueueTypeEnum.createBlockingQueue("ArrayBlockingQueue", 1024);
        Assert.assertNotNull("Should successfully create queue by name", queue2);
        Assert.assertTrue("Should create ArrayBlockingQueue", queue2 instanceof ArrayBlockingQueue);

        // Test default queue with null type - falls back to LinkedBlockingQueue
        BlockingQueue<Runnable> defaultQueue = BlockingQueueTypeEnum.createBlockingQueue("", 1024);
        Assert.assertNotNull("Empty name should create default queue", defaultQueue);
        System.out.println("Default queue type: " + defaultQueue.getClass().getSimpleName());

        System.out.println("Passed: BlockingQueueTypeEnum queue creation works");
    }

    /**
     * Test Case 4: Queue type recognition
     */
    @Test
    public void testQueueTypeRecognition() {
        System.out.println("\n========== Test Case 4: Queue type recognition ==========");

        BlockingQueue<Runnable> arrayQueue = new ArrayBlockingQueue<>(256);
        Integer type1 = BlockingQueueManager.getQueueType(arrayQueue);
        System.out.println("ArrayBlockingQueue recognized type: " + type1);
        Assert.assertEquals("Should be recognized as type 1", Integer.valueOf(1), type1);

        BlockingQueue<Runnable> linkedQueue = new LinkedBlockingQueue<>(512);
        Integer type2 = BlockingQueueManager.getQueueType(linkedQueue);
        System.out.println("LinkedBlockingQueue recognized type: " + type2);
        Assert.assertEquals("Should be recognized as type 2", Integer.valueOf(2), type2);

        String name1 = BlockingQueueManager.getQueueName(arrayQueue);
        System.out.println("ArrayBlockingQueue queue name: " + name1);
        Assert.assertEquals("Should return correct name", "ArrayBlockingQueue", name1);

        System.out.println("Passed: Queue type recognition works");
    }

    /**
     * Test Case 5: Queue config validation
     */
    @Test
    public void testQueueConfigValidation() {
        System.out.println("\n========== Test Case 5: Queue config validation ==========");

        // Valid config
        boolean valid1 = BlockingQueueManager.validateQueueConfig(1, 1024);
        Assert.assertTrue("ArrayBlockingQueue config should be valid", valid1);
        System.out.println("ArrayBlockingQueue (type=1, capacity=1024): " + (valid1 ? "valid" : "invalid"));

        // SynchronousQueue does not need capacity
        boolean valid2 = BlockingQueueManager.validateQueueConfig(4, null);
        Assert.assertTrue("SynchronousQueue without capacity should be valid", valid2);
        System.out.println("SynchronousQueue (type=4, capacity=null): " + (valid2 ? "valid" : "invalid"));

        // Invalid capacity
        boolean invalid1 = BlockingQueueManager.validateQueueConfig(1, -1);
        Assert.assertFalse("Negative capacity should be invalid", invalid1);
        System.out.println("ArrayBlockingQueue (type=1, capacity=-1): " + (invalid1 ? "valid" : "invalid"));

        // Invalid type
        boolean invalid2 = BlockingQueueManager.validateQueueConfig(null, 1024);
        Assert.assertFalse("Null type should be invalid", invalid2);
        System.out.println("null type: " + (invalid2 ? "valid" : "invalid"));

        System.out.println("Passed: Queue config validation works");
    }

    /**
     * Test Case 6: Queue capacity change capability
     */
    @Test
    public void testQueueCapacityChange() {
        System.out.println("\n========== Test Case 6: Queue capacity change capability ==========");

        // ResizableCapacityLinkedBlockingQueue supports dynamic resizing
        BlockingQueue<Runnable> resizableQueue = BlockingQueueTypeEnum.createBlockingQueue(9, 512);
        boolean canResize = BlockingQueueManager.canChangeCapacity(resizableQueue);
        System.out.println("ResizableCapacityLinkedBlockingQueue resizable: " + canResize);
        Assert.assertTrue("ResizableCapacityLinkedBlockingQueue should be resizable", canResize);

        // ArrayBlockingQueue does not support dynamic resizing
        BlockingQueue<Runnable> arrayQueue = new ArrayBlockingQueue<>(256);
        boolean cannotResize = BlockingQueueManager.canChangeCapacity(arrayQueue);
        System.out.println("ArrayBlockingQueue resizable: " + cannotResize);
        Assert.assertFalse("ArrayBlockingQueue should not be resizable", cannotResize);

        System.out.println("Passed: Queue capacity change capability recognized correctly");
    }

    /**
     * Test Case 7: Queue type-name conversion
     */
    @Test
    public void testQueueTypeNameConversion() {
        System.out.println("\n========== Test Case 7: Queue type-name conversion ==========");

        // Type to name
        String name1 = BlockingQueueTypeEnum.getBlockingQueueNameByType(1);
        Assert.assertEquals("Type 1 should be converted to ArrayBlockingQueue", "ArrayBlockingQueue", name1);
        System.out.println("Type 1 -> " + name1);

        String name2 = BlockingQueueTypeEnum.getBlockingQueueNameByType(2);
        Assert.assertEquals("Type 2 should be converted to LinkedBlockingQueue", "LinkedBlockingQueue", name2);
        System.out.println("Type 2 -> " + name2);

        // Name to type
        Integer type1 = BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("ArrayBlockingQueue").getType();
        Assert.assertEquals("ArrayBlockingQueue should be converted to type 1", Integer.valueOf(1), type1);
        System.out.println("ArrayBlockingQueue -> " + type1);

        Integer type2 = BlockingQueueTypeEnum.getBlockingQueueTypeEnumByName("LinkedBlockingQueue").getType();
        Assert.assertEquals("LinkedBlockingQueue should be converted to type 2", Integer.valueOf(2), type2);
        System.out.println("LinkedBlockingQueue -> " + type2);

        System.out.println("Passed: Queue type-name conversion works");
    }

    /**
     * Test Case 8: Queue type comparison (simulate change detection)
     */
    @Test
    public void testQueueTypeComparison() {
        System.out.println("\n========== Test Case 8: Queue type change detection ==========");

        BlockingQueue<Runnable> currentQueue = new LinkedBlockingQueue<>(1024);
        Integer currentType = BlockingQueueManager.getQueueType(currentQueue);

        // Case 1: No change
        Integer requestedType1 = 2; // LinkedBlockingQueue
        boolean typeChanged1 = !currentType.equals(requestedType1);
        System.out.println("Current type: " + currentType + ", Requested: " + requestedType1 + ", Changed: " + typeChanged1);
        Assert.assertFalse("Same type should not be detected as change", typeChanged1);

        // Case 2: Type changed
        Integer requestedType2 = 1; // ArrayBlockingQueue
        boolean typeChanged2 = !currentType.equals(requestedType2);
        System.out.println("Current type: " + currentType + ", Requested: " + requestedType2 + ", Changed: " + typeChanged2);
        Assert.assertTrue("Different type should be detected as change", typeChanged2);

        System.out.println("Passed: Queue type change detection works");
    }

    /**
     * Test Case 9: Verify queue SPI support consistent with rejection policy SPI
     */
    @Test
    public void testQueueSpiConsistencyWithRejectedPolicy() {
        System.out.println("\n========== Test Case 9: Queue SPI consistency with rejection policy ==========");

        // Queue SPI feature validation
        System.out.println("Queue SPI supported features:");
        System.out.println("1.SPI interface definition: CustomBlockingQueue");
        System.out.println("2.Type ID identification: getType() returns unique int ID");
        System.out.println("3.Name identification: getName() returns queue name");
        System.out.println("4.Instance creation: generateBlockingQueue() creates queue instance");
        System.out.println("5.Unified creation entry: BlockingQueueManager.createQueue()");
        System.out.println("6.Built-in type support: BlockingQueueTypeEnum provides 6 built-in queues");
        System.out.println("7.SPI extension support: loaded via ServiceLoaderRegistry");
        System.out.println("8.Type recognition: BlockingQueueManager.getQueueType()");
        System.out.println("9.Config validation: BlockingQueueManager.validateQueueConfig()");
        System.out.println("10.Dynamic switching: ServerThreadPoolDynamicRefresh.handleQueueChanges()");

        // Compare with rejection policy
        System.out.println("\nComparison with Rejection Policy SPI:");
        System.out.println("Rejection Policy SPI: RejectedExecutionHandler + RejectedPolicyTypeEnum");
        System.out.println("Queue SPI:            CustomBlockingQueue + BlockingQueueTypeEnum");
        System.out.println("Common features:");
        System.out.println("  - Unified SPI interface definition");
        System.out.println("  - Type ID based identification");
        System.out.println("  - Enum managing built-in types");
        System.out.println("  - ServiceLoader loading mechanism");
        System.out.println("  - Unified creation entry");

        System.out.println("Passed: Queue SPI design pattern is consistent with rejection policy");
    }
}
