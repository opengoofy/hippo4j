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

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test for field version registry.
 */
public class FieldVersionRegistryTest {

    @Before
    public void setUp() {
        // Clear registry before each test
        FieldVersionRegistry.clearForTest();
    }

    /**
     * Test basic field registration
     */
    @Test
    public void testBasicFieldRegistration() {
        FieldVersionRegistry.registerField("executeTimeOut", "2.0.0");
        FieldVersionRegistry.registerField("isAlarm", "2.0.0");
        FieldVersionRegistry.registerField("newField", "2.1.0");

        String version1 = FieldVersionRegistry.getFieldVersion("executeTimeOut");
        String version2 = FieldVersionRegistry.getFieldVersion("isAlarm");
        String version3 = FieldVersionRegistry.getFieldVersion("newField");

        Assert.isTrue("2.0.0".equals(version1), "executeTimeOut should be version 2.0.0");
        Assert.isTrue("2.0.0".equals(version2), "isAlarm should be version 2.0.0");
        Assert.isTrue("2.1.0".equals(version3), "newField should be version 2.1.0");

        System.out.println("Basic field registration test passed");
    }

    /**
     * Test that first registration wins (putIfAbsent behavior)
     */
    @Test
    public void testFirstRegistrationWins() {
        // First registration
        FieldVersionRegistry.registerField("testField", "1.0.0");

        // Try to register again with different version
        FieldVersionRegistry.registerField("testField", "2.0.0");

        // Should still be 1.0.0 (first registration wins)
        String version = FieldVersionRegistry.getFieldVersion("testField");
        Assert.isTrue("1.0.0".equals(version), "First registration should win (putIfAbsent)");

        System.out.println("First registration wins test passed");
    }

    /**
     * Test batch field registration
     */
    @Test
    public void testBatchFieldRegistration() {
        Map<String, String> fields = new HashMap<>();
        fields.put("field1", "2.0.0");
        fields.put("field2", "2.0.0");
        fields.put("field3", "2.1.0");

        FieldVersionRegistry.registerFields(fields);

        Assert.isTrue("2.0.0".equals(FieldVersionRegistry.getFieldVersion("field1")), "field1 version correct");
        Assert.isTrue("2.0.0".equals(FieldVersionRegistry.getFieldVersion("field2")), "field2 version correct");
        Assert.isTrue("2.1.0".equals(FieldVersionRegistry.getFieldVersion("field3")), "field3 version correct");

        System.out.println("Batch field registration test passed");
    }

    /**
     * Test getting all field versions
     */
    @Test
    public void testGetAllFieldVersions() {
        FieldVersionRegistry.registerField("field1", "2.0.0");
        FieldVersionRegistry.registerField("field2", "2.1.0");
        FieldVersionRegistry.registerField("field3", "2.2.0");

        Map<String, String> allVersions = FieldVersionRegistry.getAllFieldVersions();

        Assert.isTrue(allVersions.size() == 3, "Should have 3 registered fields");
        Assert.isTrue("2.0.0".equals(allVersions.get("field1")), "field1 version correct");
        Assert.isTrue("2.1.0".equals(allVersions.get("field2")), "field2 version correct");
        Assert.isTrue("2.2.0".equals(allVersions.get("field3")), "field3 version correct");

        System.out.println("Get all field versions test passed");
    }

    /**
     * Test that returned map is unmodifiable
     */
    @Test
    public void testReturnedMapIsUnmodifiable() {
        FieldVersionRegistry.registerField("testField", "2.0.0");

        Map<String, String> allVersions = FieldVersionRegistry.getAllFieldVersions();

        boolean exceptionThrown = false;
        try {
            allVersions.put("newField", "2.1.0");
        } catch (UnsupportedOperationException e) {
            exceptionThrown = true;
        }

        Assert.isTrue(exceptionThrown, "Returned map should be unmodifiable");

        System.out.println("Unmodifiable map test passed");
    }

    /**
     * Test getting version of unregistered field
     */
    @Test
    public void testGetVersionOfUnregisteredField() {
        String version = FieldVersionRegistry.getFieldVersion("nonExistentField");

        Assert.isTrue(version == null, "Unregistered field should return null");

        System.out.println("Unregistered field test passed");
    }

    /**
     * Test clear functionality
     */
    @Test
    public void testClearRegistry() {
        FieldVersionRegistry.registerField("field1", "2.0.0");
        FieldVersionRegistry.registerField("field2", "2.0.0");

        Map<String, String> allVersions = FieldVersionRegistry.getAllFieldVersions();
        Assert.isTrue(allVersions.size() == 2, "Should have 2 fields before clear");

        FieldVersionRegistry.clearForTest();

        allVersions = FieldVersionRegistry.getAllFieldVersions();
        Assert.isTrue(allVersions.size() == 0, "Should have 0 fields after clear");

        System.out.println("Clear registry test passed");
    }

    /**
     * Test null and blank field name handling
     */
    @Test
    public void testNullAndBlankFieldNames() {
        // Register null field name (should be ignored)
        FieldVersionRegistry.registerField(null, "2.0.0");

        // Register blank field name (should be ignored)
        FieldVersionRegistry.registerField("", "2.0.0");
        FieldVersionRegistry.registerField("   ", "2.0.0");

        Map<String, String> allVersions = FieldVersionRegistry.getAllFieldVersions();
        Assert.isTrue(allVersions.size() == 0, "Null/blank field names should be ignored");

        System.out.println("Null/blank field names test passed");
    }

    /**
     * Test null and blank version handling
     */
    @Test
    public void testNullAndBlankVersions() {
        // Register field with null version (should be ignored)
        FieldVersionRegistry.registerField("field1", null);

        // Register field with blank version (should be ignored)
        FieldVersionRegistry.registerField("field2", "");
        FieldVersionRegistry.registerField("field3", "   ");

        Map<String, String> allVersions = FieldVersionRegistry.getAllFieldVersions();
        Assert.isTrue(allVersions.size() == 0, "Fields with null/blank versions should be ignored");

        System.out.println("Null/blank versions test passed");
    }

    /**
     * Test version trimming
     */
    @Test
    public void testVersionTrimming() {
        FieldVersionRegistry.registerField(" field1 ", " 2.0.0 ");

        String version = FieldVersionRegistry.getFieldVersion("field1");
        Assert.isTrue("2.0.0".equals(version), "Version should be trimmed");

        System.out.println("Version trimming test passed");
    }

    /**
     * Test thread-safety (ConcurrentHashMap behavior)
     */
    @Test
    public void testConcurrentRegistration() throws InterruptedException {
        final int threadCount = 10;
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                FieldVersionRegistry.registerField("field" + index, "2." + index + ".0");
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        Map<String, String> allVersions = FieldVersionRegistry.getAllFieldVersions();
        Assert.isTrue(allVersions.size() == threadCount, "All threads should register successfully");

        System.out.println("Concurrent registration test passed");
    }
}
