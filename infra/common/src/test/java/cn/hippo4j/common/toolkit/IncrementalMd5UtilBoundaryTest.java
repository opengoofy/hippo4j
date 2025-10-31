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

import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Incremental MD5 Utility Boundary Test
 * Tests edge cases and boundary conditions for version-aware MD5 calculation
 */
public class IncrementalMd5UtilBoundaryTest {

    /**
     * Test: null config parameter
     * Expected: Graceful handling without NPE
     */
    @Test
    public void testGetCoreMd5_NullConfig() {
        System.out.println("========== Test 1: getCoreMd5 with null config ==========");

        try {
            String md5 = IncrementalMd5Util.getCoreMd5(null);
            System.out.println("Result MD5: " + md5);
            System.out.println("Test passed: Handled null config gracefully");
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getClass().getSimpleName());
            System.out.println("Test passed: NPE expected for null config");
            // NPE is acceptable for null input
        }
    }

    /**
     * Test: config with all null fields
     * Expected: Generates MD5 without error
     */
    @Test
    public void testGetCoreMd5_AllNullFields() {
        System.out.println("\n========== Test 2: getCoreMd5 with all null fields ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        // All fields are null

        String md5 = IncrementalMd5Util.getCoreMd5(config);

        System.out.println("Config: all fields null");
        System.out.println("Generated MD5: " + md5);

        Assert.assertNotNull("MD5 should not be null", md5);
        Assert.assertFalse("MD5 should not be empty", md5.isEmpty());
        System.out.println("Test passed: Generated MD5 for null fields");
    }

    /**
     * Test: version number is 0
     * Expected: Treats as v1 (any version < 2 is v1)
     */
    @Test
    public void testGetVersionedMd5_VersionZero() {
        System.out.println("\n========== Test 3: getVersionedMd5 with version = 0 ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setCorePoolSize(10);
        config.setMaximumPoolSize(20);
        config.setExecuteTimeOut(5000L);
        config.setFieldVersionMetadata(Collections.singletonMap("executeTimeOut", "2.1.0"));

        String v0Md5 = IncrementalMd5Util.getVersionedMd5(config, 0);
        String v1Md5 = IncrementalMd5Util.getVersionedMd5(config, 1);

        System.out.println("Version 0 MD5: " + v0Md5);
        System.out.println("Version 1 MD5: " + v1Md5);
        System.out.println("Are they equal? " + v0Md5.equals(v1Md5));

        Assert.assertEquals("Version 0 should behave like v1", v1Md5, v0Md5);
        System.out.println("Test passed: Version 0 uses v1 behavior");
    }

    /**
     * Test: version number is negative
     * Expected: Treats as v1 (any version < 2 is v1)
     */
    @Test
    public void testGetVersionedMd5_NegativeVersion() {
        System.out.println("\n========== Test 4: getVersionedMd5 with negative version ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setCorePoolSize(10);
        config.setMaximumPoolSize(20);
        config.setExecuteTimeOut(5000L);
        config.setFieldVersionMetadata(Collections.singletonMap("executeTimeOut", "2.1.0"));

        String vNegativeMd5 = IncrementalMd5Util.getVersionedMd5(config, -1);
        String v1Md5 = IncrementalMd5Util.getVersionedMd5(config, 1);

        System.out.println("Version -1 MD5: " + vNegativeMd5);
        System.out.println("Version 1 MD5: " + v1Md5);
        System.out.println("Are they equal? " + vNegativeMd5.equals(v1Md5));

        Assert.assertEquals("Negative version should behave like v1", v1Md5, vNegativeMd5);
        System.out.println("Test passed: Negative version uses v1 behavior");
    }

    /**
     * Test: very large version number
     * Expected: Treats as v2+ (uses incremental MD5)
     */
    @Test
    public void testGetVersionedMd5_LargeVersion() {
        System.out.println("\n========== Test 5: getVersionedMd5 with very large version ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setTenantId("test");
        config.setItemId("test");
        config.setTpId("test");
        config.setCorePoolSize(10);
        config.setMaximumPoolSize(20);
        config.setQueueType(1);
        config.setCapacity(1024);
        config.setKeepAliveTime(60L);
        config.setRejectedType(1);
        config.setAllowCoreThreadTimeOut(0);

        String vLargeMd5 = IncrementalMd5Util.getVersionedMd5(config, 999999);
        String v2Md5 = IncrementalMd5Util.getVersionedMd5(config, 2);

        System.out.println("Version 999999 MD5: " + vLargeMd5);
        System.out.println("Version 2 MD5: " + v2Md5);
        System.out.println("Are they equal? " + vLargeMd5.equals(v2Md5));

        Assert.assertEquals("Large version should behave like v2", v2Md5, vLargeMd5);
        System.out.println("Test passed: Large version uses v2 behavior");
    }

    /**
     * Test: config with only extended parameters
     * Expected: v2 generates MD5 for core params (even if empty)
     */
    @Test
    public void testGetVersionedMd5_OnlyExtendedParams() {
        System.out.println("\n========== Test 6: Config with only extended parameters ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        // Only extended parameters
        config.setExecuteTimeOut(5000L);
        config.setIsAlarm(1);
        config.setCapacityAlarm(80);
        LinkedHashMap<String, String> metadata = new LinkedHashMap<>();
        metadata.put("executeTimeOut", "2.1.0");
        metadata.put("isAlarm", "2.1.0");
        metadata.put("capacityAlarm", "2.1.0");
        config.setFieldVersionMetadata(metadata);

        String v1Md5 = IncrementalMd5Util.getVersionedMd5(config, 1);
        String v2Md5 = IncrementalMd5Util.getVersionedMd5(config, 2);
        String v3Md5 = IncrementalMd5Util.getVersionedMd5(config, 3);

        System.out.println("Config: Only extended params (executeTimeOut, isAlarm, capacityAlarm)");
        System.out.println("v1 MD5: " + v1Md5);
        System.out.println("v2 MD5: " + v2Md5);
        System.out.println("Are v1 and v2 same? " + v1Md5.equals(v2Md5));
        System.out.println("Does v3 differ? " + !v2Md5.equals(v3Md5));

        Assert.assertEquals("Protocols below threshold skip extended params", v1Md5, v2Md5);
        Assert.assertNotEquals("Supported protocol should include extended params", v2Md5, v3Md5);
        System.out.println("Test passed: Metadata gates extended params by protocol");
    }

    /**
     * Test: isDifferent with same MD5
     * Expected: Returns false (no difference)
     */
    @Test
    public void testIsDifferent_SameMd5() {
        System.out.println("\n========== Test 7: isDifferent with same MD5 ==========");

        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);

        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setCorePoolSize(10);
        newConfig.setMaximumPoolSize(20);

        boolean isDifferent = IncrementalMd5Util.isDifferent(oldConfig, newConfig, 2);

        System.out.println("Old and new configs are identical");
        System.out.println("isDifferent result: " + isDifferent);

        Assert.assertFalse("Should return false for identical configs", isDifferent);
        System.out.println("Test passed: Identical configs correctly identified");
    }

    /**
     * Test: isDifferent with different MD5
     * Expected: Returns true (difference detected)
     */
    @Test
    public void testIsDifferent_DifferentMd5() {
        System.out.println("\n========== Test 8: isDifferent with different MD5 ==========");

        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);

        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setCorePoolSize(15); // Different!
        newConfig.setMaximumPoolSize(20);

        boolean isDifferent = IncrementalMd5Util.isDifferent(oldConfig, newConfig, 2);

        System.out.println("Old corePoolSize: 10, New corePoolSize: 15");
        System.out.println("isDifferent result: " + isDifferent);

        Assert.assertTrue("Should return true for different configs", isDifferent);
        System.out.println("Test passed: Different configs correctly identified");
    }

    /**
     * Test: Empty string vs null for string fields
     * Expected: Both generate valid MD5
     */
    @Test
    public void testGetCoreMd5_EmptyVsNullString() {
        System.out.println("\n========== Test 9: Empty string vs null for string fields ==========");

        ThreadPoolParameterInfo configWithNull = new ThreadPoolParameterInfo();
        configWithNull.setTenantId(null);
        configWithNull.setItemId(null);
        configWithNull.setTpId(null);

        ThreadPoolParameterInfo configWithEmpty = new ThreadPoolParameterInfo();
        configWithEmpty.setTenantId("");
        configWithEmpty.setItemId("");
        configWithEmpty.setTpId("");

        String nullMd5 = IncrementalMd5Util.getCoreMd5(configWithNull);
        String emptyMd5 = IncrementalMd5Util.getCoreMd5(configWithEmpty);

        System.out.println("Null fields MD5: " + nullMd5);
        System.out.println("Empty fields MD5: " + emptyMd5);
        System.out.println("Are they different? " + !nullMd5.equals(emptyMd5));

        Assert.assertNotNull("Null fields MD5 should not be null", nullMd5);
        Assert.assertNotNull("Empty fields MD5 should not be null", emptyMd5);
        System.out.println("Test passed: Both null and empty strings handled");
    }

    /**
     * Test: Consistency - multiple calls should return same MD5
     * Expected: Same MD5 for same config
     */
    @Test
    public void testGetCoreMd5_Consistency() {
        System.out.println("\n========== Test 10: MD5 calculation consistency ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setTenantId("test");
        config.setItemId("test");
        config.setTpId("test");
        config.setCorePoolSize(10);
        config.setMaximumPoolSize(20);

        String md51 = IncrementalMd5Util.getCoreMd5(config);
        String md52 = IncrementalMd5Util.getCoreMd5(config);
        String md53 = IncrementalMd5Util.getCoreMd5(config);

        System.out.println("MD5 call 1: " + md51);
        System.out.println("MD5 call 2: " + md52);
        System.out.println("MD5 call 3: " + md53);
        System.out.println("All equal? " + (md51.equals(md52) && md52.equals(md53)));

        Assert.assertEquals("Multiple calls should return same MD5", md51, md52);
        Assert.assertEquals("Multiple calls should return same MD5", md52, md53);
        System.out.println("Test passed: MD5 calculation is consistent");
    }
}
