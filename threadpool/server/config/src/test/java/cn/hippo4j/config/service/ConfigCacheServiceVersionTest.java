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

package cn.hippo4j.config.service;

import cn.hippo4j.common.model.ThreadPoolParameterInfo;
import cn.hippo4j.common.toolkit.IncrementalMd5Util;
import org.junit.Assert;
import org.junit.Test;

/**
 * ConfigCacheService Version-Aware Test
 * Tests the version-aware MD5 comparison logic for cross-version compatibility
 * 
 * Note: Tests focus on the MD5 calculation logic used by ConfigCacheService
 * Integration tests for ConfigCacheService.isUpdateData() are covered separately
 * due to dependencies on configuration cache infrastructure
 */
public class ConfigCacheServiceVersionTest {

    /**
     * Test: Version-aware MD5 comparison logic
     * Verifies that v1 and v2 produce different MD5 for same config with extended params
     */
    @Test
    public void testVersionedMd5_Difference() {
        System.out.println("========== Test 1: v1 vs v2 MD5 difference ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setTenantId("test-tenant");
        config.setItemId("test-item");
        config.setTpId("test-pool");
        config.setCorePoolSize(10);
        config.setMaximumPoolSize(20);
        config.setQueueType(2);
        config.setCapacity(1024);
        config.setKeepAliveTime(60L);
        config.setRejectedType(1);
        config.setAllowCoreThreadTimeOut(0);
        config.setExecuteTimeOut(5000L); // Extended parameter
        config.setIsAlarm(1); // Extended parameter

        String v1Md5 = IncrementalMd5Util.getVersionedMd5(config, 1);
        String v2Md5 = IncrementalMd5Util.getVersionedMd5(config, 2);

        System.out.println("Config includes extended parameters: executeTimeOut, isAlarm");
        System.out.println("v1 MD5 (full): " + v1Md5);
        System.out.println("v2 MD5 (incremental): " + v2Md5);
        System.out.println("Are they different? " + !v1Md5.equals(v2Md5));

        Assert.assertNotEquals("v1 and v2 should produce different MD5", v1Md5, v2Md5);
        System.out.println("Test passed: v1 and v2 use different MD5 strategies");
    }

    /**
     * Test: v2 client should not refresh when only extended params change
     * This is the core extensibility improvement
     */
    @Test
    public void testV2Client_ExtendedParamChange_NoRefresh() {
        System.out.println("\n========== Test 2: v2 client - extended param change ==========");

        // Old config
        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);
        oldConfig.setQueueType(2);
        oldConfig.setCapacity(1024);
        oldConfig.setKeepAliveTime(60L);
        oldConfig.setRejectedType(1);
        oldConfig.setAllowCoreThreadTimeOut(0);
        oldConfig.setExecuteTimeOut(3000L); // Extended

        // New config (only extended param changed)
        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setCorePoolSize(10);
        newConfig.setMaximumPoolSize(20);
        newConfig.setQueueType(2);
        newConfig.setCapacity(1024);
        newConfig.setKeepAliveTime(60L);
        newConfig.setRejectedType(1);
        newConfig.setAllowCoreThreadTimeOut(0);
        newConfig.setExecuteTimeOut(5000L); // Extended param changed!

        String oldV2Md5 = IncrementalMd5Util.getVersionedMd5(oldConfig, 2);
        String newV2Md5 = IncrementalMd5Util.getVersionedMd5(newConfig, 2);

        System.out.println("Extended param change: executeTimeOut 3000 -> 5000");
        System.out.println("Old v2 MD5: " + oldV2Md5);
        System.out.println("New v2 MD5: " + newV2Md5);
        System.out.println("Are they same? " + oldV2Md5.equals(newV2Md5));

        Assert.assertEquals("v2 MD5 should remain same (no core param change)", oldV2Md5, newV2Md5);
        System.out.println("Test passed: v2 client won't refresh for extended param change");
    }

    /**
     * Test: v1 client SHOULD refresh when extended params change
     * This verifies backward compatibility
     */
    @Test
    public void testV1Client_ExtendedParamChange_ShouldRefresh() {
        System.out.println("\n========== Test 3: v1 client - extended param change ==========");

        // Old config
        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);
        oldConfig.setQueueType(2);
        oldConfig.setCapacity(1024);
        oldConfig.setExecuteTimeOut(3000L);

        // New config (only extended param changed)
        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setCorePoolSize(10);
        newConfig.setMaximumPoolSize(20);
        newConfig.setQueueType(2);
        newConfig.setCapacity(1024);
        newConfig.setExecuteTimeOut(5000L); // Extended param changed!

        String oldV1Md5 = IncrementalMd5Util.getVersionedMd5(oldConfig, 1);
        String newV1Md5 = IncrementalMd5Util.getVersionedMd5(newConfig, 1);

        System.out.println("Extended param change: executeTimeOut 3000 -> 5000");
        System.out.println("Old v1 MD5: " + oldV1Md5);
        System.out.println("New v1 MD5: " + newV1Md5);
        System.out.println("Are they different? " + !oldV1Md5.equals(newV1Md5));

        Assert.assertNotEquals("v1 MD5 should differ (uses full comparison)", oldV1Md5, newV1Md5);
        System.out.println("Test passed: v1 client will refresh for extended param change");
    }

    /**
     * Test: Both v1 and v2 should refresh when core params change
     * This ensures core functionality still works for both versions
     */
    @Test
    public void testBothVersions_CoreParamChange_ShouldRefresh() {
        System.out.println("\n========== Test 4: Both versions - core param change ==========");

        // Old config
        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);
        oldConfig.setQueueType(2);

        // New config (core param changed)
        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setCorePoolSize(15); // Core param changed!
        newConfig.setMaximumPoolSize(20);
        newConfig.setQueueType(2);

        String oldV1Md5 = IncrementalMd5Util.getVersionedMd5(oldConfig, 1);
        String newV1Md5 = IncrementalMd5Util.getVersionedMd5(newConfig, 1);
        String oldV2Md5 = IncrementalMd5Util.getVersionedMd5(oldConfig, 2);
        String newV2Md5 = IncrementalMd5Util.getVersionedMd5(newConfig, 2);

        System.out.println("Core param change: corePoolSize 10 -> 15");
        System.out.println("v1: " + oldV1Md5 + " -> " + newV1Md5 + " (different? " + !oldV1Md5.equals(newV1Md5) + ")");
        System.out.println("v2: " + oldV2Md5 + " -> " + newV2Md5 + " (different? " + !oldV2Md5.equals(newV2Md5) + ")");

        Assert.assertNotEquals("v1 should detect core param change", oldV1Md5, newV1Md5);
        Assert.assertNotEquals("v2 should detect core param change", oldV2Md5, newV2Md5);
        System.out.println("Test passed: Both versions detect core param changes");
    }
}
