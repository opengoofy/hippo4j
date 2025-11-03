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
import org.junit.Before;
import org.junit.Test;

/**
 * Test for incremental content util with version-aware filtering.
 */
public class IncrementalContentUtilTest {

    @Before
    public void setUp() {
        // Clear and setup field version registry
        FieldVersionRegistry.clearForTest();

        // Register fields for testing (simulating FieldVersionInitializer)
        FieldVersionRegistry.registerField("executeTimeOut", "2.0.0");
        FieldVersionRegistry.registerField("isAlarm", "2.0.0");
        FieldVersionRegistry.registerField("capacityAlarm", "2.0.0");
        FieldVersionRegistry.registerField("livenessAlarm", "2.0.0");
        FieldVersionRegistry.registerField("allowCoreThreadTimeOut", "2.0.0");
    }

    /**
     * Test that old clients (1.5.0) cannot see fields introduced in 2.0.0
     */
    @Test
    public void testOldClientFilteringNewFields() {
        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(10);
        param.setMaxSize(20);
        param.setQueueType(1);
        param.setCapacity(1024);
        param.setKeepAliveTime(60L);
        param.setRejectedType(1);
        // 2.0.0 fields
        param.setExecuteTimeOut(5000L);
        param.setIsAlarm(1);
        param.setCapacityAlarm(80);
        param.setLivenessAlarm(80);
        param.setAllowCoreThreadTimeOut(0);

        // Test old client (1.5.0) - should NOT see 2.0.0 fields
        String content15 = IncrementalContentUtil.getVersionedContent(param, "1.5.0");

        // Assert: core fields should be present
        Assert.isTrue(content15.contains("\"tenantId\":\"tenant-001\""), "Should contain tenantId");
        Assert.isTrue(content15.contains("\"tpId\":\"test-pool\""), "Should contain tpId");
        Assert.isTrue(content15.contains("\"coreSize\":10"), "Should contain coreSize");
        Assert.isTrue(content15.contains("\"maxSize\":20"), "Should contain maxSize");

        // Assert: 2.0.0 fields should NOT be present for 1.5.0 client
        Assert.isTrue(!content15.contains("executeTimeOut"), "Client 1.5.0 should NOT see executeTimeOut");
        Assert.isTrue(!content15.contains("isAlarm"), "Client 1.5.0 should NOT see isAlarm");
        Assert.isTrue(!content15.contains("capacityAlarm"), "Client 1.5.0 should NOT see capacityAlarm");
        Assert.isTrue(!content15.contains("livenessAlarm"), "Client 1.5.0 should NOT see livenessAlarm");
        Assert.isTrue(!content15.contains("allowCoreThreadTimeOut"), "Client 1.5.0 should NOT see allowCoreThreadTimeOut");

        System.out.println("Old client (1.5.0) content: " + content15);
    }

    /**
     * Test that new clients (2.0.0) can see all fields including 2.0.0 fields
     */
    @Test
    public void testNewClientSeeingAllFields() {
        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(10);
        param.setMaxSize(20);
        param.setQueueType(1);
        param.setCapacity(1024);
        param.setKeepAliveTime(60L);
        param.setRejectedType(1);
        // 2.0.0 fields
        param.setExecuteTimeOut(5000L);
        param.setIsAlarm(1);
        param.setCapacityAlarm(80);
        param.setLivenessAlarm(80);
        param.setAllowCoreThreadTimeOut(0);

        // Test new client (2.0.0) - should see ALL fields
        String content20 = IncrementalContentUtil.getVersionedContent(param, "2.0.0");

        // Assert: core fields should be present
        Assert.isTrue(content20.contains("\"tenantId\":\"tenant-001\""), "Should contain tenantId");
        Assert.isTrue(content20.contains("\"tpId\":\"test-pool\""), "Should contain tpId");
        Assert.isTrue(content20.contains("\"coreSize\":10"), "Should contain coreSize");

        // Assert: 2.0.0 fields SHOULD be present for 2.0.0 client
        Assert.isTrue(content20.contains("executeTimeOut"), "Client 2.0.0 should see executeTimeOut");
        Assert.isTrue(content20.contains("isAlarm"), "Client 2.0.0 should see isAlarm");
        Assert.isTrue(content20.contains("capacityAlarm"), "Client 2.0.0 should see capacityAlarm");
        Assert.isTrue(content20.contains("livenessAlarm"), "Client 2.0.0 should see livenessAlarm");
        Assert.isTrue(content20.contains("allowCoreThreadTimeOut"), "Client 2.0.0 should see allowCoreThreadTimeOut");

        System.out.println("New client (2.0.0) content: " + content20);
    }

    /**
     * Test that UNKNOWN_VERSION clients (0.0.0) can see core fields but not 2.0.0 fields
     */
    @Test
    public void testUnknownVersionClientSeesOnlyCoreFields() {
        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(10);
        param.setMaxSize(20);
        param.setExecuteTimeOut(5000L);

        // Test UNKNOWN_VERSION client (0.0.0)
        String contentUnknown = IncrementalContentUtil.getVersionedContent(param, null);

        // Assert: should see core fields but NOT 2.0.0 fields (0.0.0 < 2.0.0)
        Assert.isTrue(contentUnknown.contains("\"coreSize\":10"), "Should contain core field coreSize");
        Assert.isTrue(!contentUnknown.contains("executeTimeOut"), "UNKNOWN_VERSION (0.0.0) should NOT see 2.0.0 fields");

        System.out.println("UNKNOWN_VERSION client content: " + contentUnknown);
    }

    /**
     * Test incremental version compatibility (2.1.0 adds new field)
     */
    @Test
    public void testIncrementalVersionCompatibility() {
        // Register a 2.1.0 field
        FieldVersionRegistry.registerField("newFeatureField", "2.1.0");

        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(10);
        param.setMaxSize(20);
        param.setExecuteTimeOut(5000L); // 2.0.0 field

        // Client 1.5.0: should only see core fields
        String content15 = IncrementalContentUtil.getVersionedContent(param, "1.5.0");
        Assert.isTrue(!content15.contains("executeTimeOut"), "1.5.0 should NOT see 2.0.0 fields");

        // Client 2.0.0: should see core + 2.0.0 fields, but NOT 2.1.0 fields
        String content20 = IncrementalContentUtil.getVersionedContent(param, "2.0.0");
        Assert.isTrue(content20.contains("executeTimeOut"), "2.0.0 should see 2.0.0 fields");
        Assert.isTrue(!content20.contains("newFeatureField"), "2.0.0 should NOT see 2.1.0 fields");

        // Client 2.1.0: should see all fields
        String content21 = IncrementalContentUtil.getVersionedContent(param, "2.1.0");
        Assert.isTrue(content21.contains("executeTimeOut"), "2.1.0 should see 2.0.0 fields");

        System.out.println("Incremental version test passed");
    }

    /**
     * Test that core parameters are visible to all versions
     */
    @Test
    public void testCoreParametersVisibleToAllVersions() {
        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(5);
        param.setMaxSize(10);
        param.setQueueType(2);
        param.setCapacity(512);
        param.setKeepAliveTime(30L);
        param.setRejectedType(2);

        // Test very old client (0.1.0)
        String content01 = IncrementalContentUtil.getVersionedContent(param, "0.1.0");

        // Assert: all core parameters should be visible
        Assert.isTrue(content01.contains("\"tenantId\":\"tenant-001\""), "Core field tenantId visible to 0.1.0");
        Assert.isTrue(content01.contains("\"itemId\":\"item-001\""), "Core field itemId visible to 0.1.0");
        Assert.isTrue(content01.contains("\"tpId\":\"test-pool\""), "Core field tpId visible to 0.1.0");
        Assert.isTrue(content01.contains("\"coreSize\":5"), "Core field coreSize visible to 0.1.0");
        Assert.isTrue(content01.contains("\"maxSize\":10"), "Core field maxSize visible to 0.1.0");
        Assert.isTrue(content01.contains("\"queueType\":2"), "Core field queueType visible to 0.1.0");
        Assert.isTrue(content01.contains("\"capacity\":512"), "Core field capacity visible to 0.1.0");
        Assert.isTrue(content01.contains("\"keepAliveTime\":30"), "Core field keepAliveTime visible to 0.1.0");
        Assert.isTrue(content01.contains("\"rejectedType\":2"), "Core field rejectedType visible to 0.1.0");

        System.out.println("Core parameters test passed for version 0.1.0");
    }

    /**
     * Test field adapter compatibility (corePoolSize vs coreSize)
     */
    @Test
    public void testFieldAdapterInVersionedContent() {
        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCorePoolSize(15); // Use new field name
        param.setMaximumPoolSize(30); // Use new field name

        String content = IncrementalContentUtil.getVersionedContent(param, "1.5.0");

        // Assert: should use old field names in output (via adapter)
        Assert.isTrue(content.contains("\"coreSize\":15"), "Should use coreSize (old name) via adapter");
        Assert.isTrue(content.contains("\"maxSize\":30"), "Should use maxSize (old name) via adapter");
        Assert.isTrue(!content.contains("corePoolSize"), "Should NOT contain corePoolSize (new name)");
        Assert.isTrue(!content.contains("maximumPoolSize"), "Should NOT contain maximumPoolSize (new name)");

        System.out.println("Field adapter test passed: " + content);
    }

    /**
     * Test null and empty version handling
     */
    @Test
    public void testNullAndEmptyVersionHandling() {
        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(10);
        param.setMaxSize(20);
        param.setExecuteTimeOut(5000L);

        // Test null version (should act as UNKNOWN_VERSION = 0.0.0, cannot see 2.0.0 fields)
        String contentNull = IncrementalContentUtil.getVersionedContent(param, null);
        Assert.isTrue(contentNull.contains("\"coreSize\":10"), "Null version should see core fields");
        Assert.isTrue(!contentNull.contains("executeTimeOut"), "Null version (0.0.0) should NOT see 2.0.0 fields");

        // Test empty version (should act as UNKNOWN_VERSION = 0.0.0)
        String contentEmpty = IncrementalContentUtil.getVersionedContent(param, "");
        Assert.isTrue(contentEmpty.contains("\"coreSize\":10"), "Empty version should see core fields");
        Assert.isTrue(!contentEmpty.contains("executeTimeOut"), "Empty version (0.0.0) should NOT see 2.0.0 fields");

        // Test blank version (should act as UNKNOWN_VERSION = 0.0.0)
        String contentBlank = IncrementalContentUtil.getVersionedContent(param, "   ");
        Assert.isTrue(contentBlank.contains("\"coreSize\":10"), "Blank version should see core fields");
        Assert.isTrue(!contentBlank.contains("executeTimeOut"), "Blank version (0.0.0) should NOT see 2.0.0 fields");

        System.out.println("Null/empty/blank version test passed");
    }
}
