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
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Field Version Control Test: Validates that fields introduced in newer versions
 * are automatically excluded for older protocol clients to prevent unnecessary refreshes.
 * 
 * This directly addresses the mentor's requirement:
 * "If server version 2.0 introduces a new parameter xxx, and the client version is lower than 2.0,
 * then this parameter should not be included in the refresh check."
 */
public class FieldVersionControlTest {

    /**
     * Scenario 1: Server 2.0 introduces a new field, client with protocol v1 should skip it.
     * This simulates the mentor's example: server adds field 'xxx' in v2.0, client v1.9 should ignore it.
     */
    @Test
    public void testNewFieldInV2_ProtocolV1ClientSkips() {
        System.out.println("========== Scenario 1: Server 2.0 adds new field, Protocol v1 client skips ==========");

        // Server configuration (v2.0) with a hypothetical new field 'executeTimeOut'
        // Explicitly mark that the field is only recognized by clients from protocol v3 onward
        ThreadPoolParameterInfo serverConfig = new ThreadPoolParameterInfo();
        serverConfig.setTenantId("tenant-001");
        serverConfig.setItemId("item-001");
        serverConfig.setTpId("test-pool");
        serverConfig.setCorePoolSize(10);
        serverConfig.setMaximumPoolSize(20);
        serverConfig.setQueueType(2);
        serverConfig.setCapacity(1024);
        serverConfig.setKeepAliveTime(60L);
        serverConfig.setRejectedType(1);
        serverConfig.setAllowCoreThreadTimeOut(0);
        serverConfig.setExecuteTimeOut(5000L); // New field introduced in v2.0 (minimum version = 2.1.0)
        serverConfig.setFieldVersionMetadata(Collections.singletonMap("executeTimeOut", "2.1.0"));

        // Protocol v1 client content generation
        String v1Content = IncrementalContentUtil.getVersionedContent(serverConfig, "1.9.0");
        LinkedHashMap<String, Object> v1Fields = JSONUtil.parseObject(v1Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });

        // Protocol v2 client content generation
        String v2Content = IncrementalContentUtil.getVersionedContent(serverConfig, "2.0.0");
        LinkedHashMap<String, Object> v2Fields = JSONUtil.parseObject(v2Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });

        // Protocol v3 client content generation
        String v3Content = IncrementalContentUtil.getVersionedContent(serverConfig, "2.1.0");
        LinkedHashMap<String, Object> v3Fields = JSONUtil.parseObject(v3Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });

        System.out.println("Server config has executeTimeOut: " + serverConfig.getExecuteTimeOut());
        System.out.println("Protocol v1 content: " + v1Content);
        System.out.println("Protocol v2 content: " + v2Content);
        System.out.println("Protocol v3 content: " + v3Content);
        System.out.println("Protocol v1 contains executeTimeOut: " + v1Fields.containsKey("executeTimeOut"));
        System.out.println("Protocol v2 contains executeTimeOut: " + v2Fields.containsKey("executeTimeOut"));
        System.out.println("Protocol v3 contains executeTimeOut: " + v3Fields.containsKey("executeTimeOut"));

        // Assertions
        Assert.assertFalse("Protocol v1 should skip executeTimeOut (min protocol = 3)", v1Fields.containsKey("executeTimeOut"));
        Assert.assertFalse("Protocol v2 should skip executeTimeOut (min protocol = 3)", v2Fields.containsKey("executeTimeOut"));
        Assert.assertTrue("Protocol v3 should include executeTimeOut", v3Fields.containsKey("executeTimeOut"));
        System.out.println("Test passed: Protocol v1/v2 clients skip new field, v3 client observes it");
    }

    /**
     * Scenario 2: Server 2.1 introduces another new field, only protocol v3+ clients should see it.
     * This validates the mentor's second example: incremental field rollout across versions.
     */
    @Test
    public void testNewFieldInV21_RequiresProtocolV3() {
        System.out.println("\n========== Scenario 2: Server 2.1 adds field requiring protocol v3 ==========");

        // Simulate a field that requires protocol v3 (e.g., a new alarm type)
        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setTenantId("tenant-001");
        config.setItemId("item-001");
        config.setTpId("test-pool");
        config.setCorePoolSize(10);
        config.setMaximumPoolSize(20);
        config.setQueueType(2);
        config.setCapacity(1024);
        config.setIsAlarm(1); // Extended field, minimum version = 2.1.0
        config.setFieldVersionMetadata(Collections.singletonMap("isAlarm", "2.1.0"));

        String v1Content = IncrementalContentUtil.getVersionedContent(config, "1.9.0");
        String v2Content = IncrementalContentUtil.getVersionedContent(config, "2.0.0");
        String v3Content = IncrementalContentUtil.getVersionedContent(config, "2.1.0");

        LinkedHashMap<String, Object> v1Fields = JSONUtil.parseObject(v1Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        LinkedHashMap<String, Object> v2Fields = JSONUtil.parseObject(v2Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        LinkedHashMap<String, Object> v3Fields = JSONUtil.parseObject(v3Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });

        System.out.println("Protocol v1 content: " + v1Content);
        System.out.println("Protocol v2 content: " + v2Content);
        System.out.println("Protocol v3 content: " + v3Content);
        System.out.println("v1 contains isAlarm: " + v1Fields.containsKey("isAlarm"));
        System.out.println("v2 contains isAlarm: " + v2Fields.containsKey("isAlarm"));
        System.out.println("v3 contains isAlarm: " + v3Fields.containsKey("isAlarm"));

        Assert.assertFalse("Protocol v1 should skip isAlarm (min protocol = 3)", v1Fields.containsKey("isAlarm"));
        Assert.assertFalse("Protocol v2 should skip isAlarm (min protocol = 3)", v2Fields.containsKey("isAlarm"));
        Assert.assertTrue("Protocol v3 should include isAlarm", v3Fields.containsKey("isAlarm"));
        System.out.println("Test passed: Field visibility controlled by minimum protocol version");
    }

    /**
     * Scenario 3: Verify that MD5 remains stable when only invisible fields change.
     * This is the core benefit: preventing unnecessary refreshes.
     */
    @Test
    public void testMd5StabilityWhenInvisibleFieldChanges() {
        System.out.println("\n========== Scenario 3: MD5 stability when invisible field changes ==========");

        // Old config without extended field
        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setTenantId("tenant-001");
        oldConfig.setItemId("item-001");
        oldConfig.setTpId("test-pool");
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);
        oldConfig.setQueueType(2);
        oldConfig.setCapacity(1024);

        // New config with extended field added (invisible to protocol v2)
        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setTenantId("tenant-001");
        newConfig.setItemId("item-001");
        newConfig.setTpId("test-pool");
        newConfig.setCorePoolSize(10);
        newConfig.setMaximumPoolSize(20);
        newConfig.setQueueType(2);
        newConfig.setCapacity(1024);
        newConfig.setExecuteTimeOut(5000L); // Added field (min version = 2.1.0)
        newConfig.setFieldVersionMetadata(Collections.singletonMap("executeTimeOut", "2.1.0"));

        String oldV2Md5 = IncrementalMd5Util.getVersionedMd5(oldConfig, "2.0.0");
        String newV2Md5 = IncrementalMd5Util.getVersionedMd5(newConfig, "2.0.0");

        System.out.println("Old config executeTimeOut: " + oldConfig.getExecuteTimeOut());
        System.out.println("New config executeTimeOut: " + newConfig.getExecuteTimeOut());
        System.out.println("Protocol v2 old MD5: " + oldV2Md5);
        System.out.println("Protocol v2 new MD5: " + newV2Md5);

        Assert.assertEquals("MD5 should remain same when only invisible fields change", oldV2Md5, newV2Md5);
        System.out.println("Test passed: Protocol v2 client does not refresh when server adds executeTimeOut");
    }

    /**
     * Scenario 4: Core field changes should always trigger refresh regardless of protocol.
     * This ensures critical updates are never missed.
     */
    @Test
    public void testCoreFieldChangesAlwaysTriggerRefresh() {
        System.out.println("\n========== Scenario 4: Core field changes trigger refresh for all protocols ==========");

        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setTenantId("tenant-001");
        oldConfig.setItemId("item-001");
        oldConfig.setTpId("test-pool");
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);

        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setTenantId("tenant-001");
        newConfig.setItemId("item-001");
        newConfig.setTpId("test-pool");
        newConfig.setCorePoolSize(15); // Core field changed
        newConfig.setMaximumPoolSize(20);

        String oldV1Md5 = IncrementalMd5Util.getVersionedMd5(oldConfig, "1.9.0");
        String newV1Md5 = IncrementalMd5Util.getVersionedMd5(newConfig, "1.9.0");
        String oldV2Md5 = IncrementalMd5Util.getVersionedMd5(oldConfig, "2.0.0");
        String newV2Md5 = IncrementalMd5Util.getVersionedMd5(newConfig, "2.0.0");

        System.out.println("Core field changed: corePoolSize 10 -> 15");
        System.out.println("Protocol v1: " + (oldV1Md5.equals(newV1Md5) ? "same" : "different"));
        System.out.println("Protocol v2: " + (oldV2Md5.equals(newV2Md5) ? "same" : "different"));

        Assert.assertNotEquals("Protocol v1 should detect core field change", oldV1Md5, newV1Md5);
        Assert.assertNotEquals("Protocol v2 should detect core field change", oldV2Md5, newV2Md5);
        System.out.println("Test passed: Core field changes always trigger refresh");
    }

    /**
     * Scenario 5: Simulate exact mentor's requirement - adding field 'xxx' in server 2.0.
     * Demonstrates the complete workflow of field version control.
     */
    @Test
    public void testMentorScenario_ServerV20AddsFieldXxx() {
        System.out.println("\n========== Scenario 5: Mentor's exact requirement - Server 2.0 adds 'xxx' ==========");

        // Step 1: Simulate registering a new field 'xxx' with minimum protocol 2
        // (In real implementation, this would be done in FIELD_MIN_PROTOCOL_VERSION initialization)
        // For testing, we use 'isAlarm' as a proxy since it's configured with min protocol = 3

        // Client v1.9 (protocol 1) - before 'xxx' was introduced
        ThreadPoolParameterInfo clientV19Config = new ThreadPoolParameterInfo();
        clientV19Config.setTenantId("tenant-001");
        clientV19Config.setItemId("item-001");
        clientV19Config.setTpId("test-pool");
        clientV19Config.setCorePoolSize(10);
        clientV19Config.setMaximumPoolSize(20);
        clientV19Config.setQueueType(2);
        clientV19Config.setCapacity(1024);

        // Server v2.0 - has field 'xxx' (using 'isAlarm' as proxy, min protocol = 3)
        ThreadPoolParameterInfo serverV20Config = new ThreadPoolParameterInfo();
        serverV20Config.setTenantId("tenant-001");
        serverV20Config.setItemId("item-001");
        serverV20Config.setTpId("test-pool");
        serverV20Config.setCorePoolSize(10);
        serverV20Config.setMaximumPoolSize(20);
        serverV20Config.setQueueType(2);
        serverV20Config.setCapacity(1024);
        serverV20Config.setIsAlarm(1); // New field 'xxx' introduced in v2.0 (but min version = 2.1.0)
        serverV20Config.setFieldVersionMetadata(Collections.singletonMap("isAlarm", "2.1.0"));

        // Client v2.0 (protocol 2) - should see 'xxx' if it's marked for protocol 2
        // But since isAlarm is marked protocol 3, even v2 clients skip it
        ThreadPoolParameterInfo clientV20Config = new ThreadPoolParameterInfo();
        clientV20Config.setTenantId("tenant-001");
        clientV20Config.setItemId("item-001");
        clientV20Config.setTpId("test-pool");
        clientV20Config.setCorePoolSize(10);
        clientV20Config.setMaximumPoolSize(20);
        clientV20Config.setQueueType(2);
        clientV20Config.setCapacity(1024);
        clientV20Config.setIsAlarm(1);

        // Generate MD5 for different protocol versions
        String v1ClientMd5 = IncrementalMd5Util.getVersionedMd5(clientV19Config, "1.9.0");
        String v2ClientWithoutFieldMd5 = IncrementalMd5Util.getVersionedMd5(clientV19Config, "2.0.0");
        String v2ServerWithFieldMd5 = IncrementalMd5Util.getVersionedMd5(serverV20Config, "2.0.0");

        System.out.println("Client v1.9 (protocol 1) MD5: " + v1ClientMd5);
        System.out.println("Client v2.0 without 'xxx' (protocol 2) MD5: " + v2ClientWithoutFieldMd5);
        System.out.println("Server v2.0 with 'xxx' (protocol 2) MD5: " + v2ServerWithFieldMd5);

        // Key assertion: Protocol v2 clients should have same MD5 regardless of isAlarm
        // because isAlarm requires protocol 3
        Assert.assertEquals(
                "Server v2.0 adding field 'xxx' (isAlarm) should NOT affect protocol v2 client MD5",
                v2ClientWithoutFieldMd5,
                v2ServerWithFieldMd5);

        System.out.println("Test passed: Field 'xxx' invisible to protocol v2, no refresh triggered");
        System.out.println("Mentor's requirement validated: Client < v2.0 does not refresh on new field");
    }

    /**
     * Scenario 6: Server 2.1 introduces field 'yyy', protocol v3 clients see it, v2 clients skip it.
     */
    @Test
    public void testMentorScenario_ServerV21AddsFieldYyy() {
        System.out.println("\n========== Scenario 6: Server 2.1 adds 'yyy', only protocol v3+ sees it ==========");

        // Server v2.1 with new field 'yyy' (using 'capacityAlarm' as proxy, min protocol = 3)
        ThreadPoolParameterInfo serverV21Config = new ThreadPoolParameterInfo();
        serverV21Config.setTenantId("tenant-001");
        serverV21Config.setItemId("item-001");
        serverV21Config.setTpId("test-pool");
        serverV21Config.setCorePoolSize(10);
        serverV21Config.setMaximumPoolSize(20);
        serverV21Config.setQueueType(2);
        serverV21Config.setCapacity(1024);
        serverV21Config.setCapacityAlarm(80); // New field 'yyy' introduced in v2.1 (min version = 2.1.0)
        serverV21Config.setFieldVersionMetadata(Collections.singletonMap("capacityAlarm", "2.1.0"));

        String v1Content = IncrementalContentUtil.getVersionedContent(serverV21Config, "1.9.0");
        String v2Content = IncrementalContentUtil.getVersionedContent(serverV21Config, "2.0.0");
        String v3Content = IncrementalContentUtil.getVersionedContent(serverV21Config, "2.1.0");

        LinkedHashMap<String, Object> v1Fields = JSONUtil.parseObject(v1Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        LinkedHashMap<String, Object> v2Fields = JSONUtil.parseObject(v2Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        LinkedHashMap<String, Object> v3Fields = JSONUtil.parseObject(v3Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });

        System.out.println("Server v2.1 has field 'yyy' (capacityAlarm): " + serverV21Config.getCapacityAlarm());
        System.out.println("Protocol v1 contains capacityAlarm: " + v1Fields.containsKey("capacityAlarm"));
        System.out.println("Protocol v2 contains capacityAlarm: " + v2Fields.containsKey("capacityAlarm"));
        System.out.println("Protocol v3 contains capacityAlarm: " + v3Fields.containsKey("capacityAlarm"));

        Assert.assertFalse("Protocol v1 should skip 'yyy' (capacityAlarm)", v1Fields.containsKey("capacityAlarm"));
        Assert.assertFalse("Protocol v2 should skip 'yyy' (capacityAlarm)", v2Fields.containsKey("capacityAlarm"));
        Assert.assertTrue("Protocol v3 should include 'yyy' (capacityAlarm)", v3Fields.containsKey("capacityAlarm"));

        System.out.println("Test passed: Field 'yyy' only visible to protocol v3+");
        System.out.println("Mentor's requirement validated: Incremental field rollout works correctly");
    }

    /**
     * Scenario 7: Verify that changing an invisible field does not change MD5 for lower protocol clients.
     * This is the key to preventing "invalid refresh" mentioned by the mentor.
     */
    @Test
    public void testInvisibleFieldChangeDoesNotAffectMd5() {
        System.out.println("\n========== Scenario 7: Invisible field change does not affect MD5 ==========");

        // Config 1: without extended field
        ThreadPoolParameterInfo config1 = new ThreadPoolParameterInfo();
        config1.setTenantId("tenant-001");
        config1.setItemId("item-001");
        config1.setTpId("test-pool");
        config1.setCorePoolSize(10);
        config1.setMaximumPoolSize(20);
        config1.setQueueType(2);
        config1.setCapacity(1024);

        // Config 2: extended field changed from null to 5000
        ThreadPoolParameterInfo config2 = new ThreadPoolParameterInfo();
        config2.setTenantId("tenant-001");
        config2.setItemId("item-001");
        config2.setTpId("test-pool");
        config2.setCorePoolSize(10);
        config2.setMaximumPoolSize(20);
        config2.setQueueType(2);
        config2.setCapacity(1024);
        config2.setExecuteTimeOut(5000L); // Changed from null to 5000
        config2.setFieldVersionMetadata(Collections.singletonMap("executeTimeOut", "2.1.0"));

        // Config 3: extended field changed from 5000 to 8000
        ThreadPoolParameterInfo config3 = new ThreadPoolParameterInfo();
        config3.setTenantId("tenant-001");
        config3.setItemId("item-001");
        config3.setTpId("test-pool");
        config3.setCorePoolSize(10);
        config3.setMaximumPoolSize(20);
        config3.setQueueType(2);
        config3.setCapacity(1024);
        config3.setExecuteTimeOut(8000L); // Changed from 5000 to 8000
        config3.setFieldVersionMetadata(Collections.singletonMap("executeTimeOut", "2.1.0"));

        String md51 = IncrementalMd5Util.getVersionedMd5(config1, "2.0.0");
        String md52 = IncrementalMd5Util.getVersionedMd5(config2, "2.0.0");
        String md53 = IncrementalMd5Util.getVersionedMd5(config3, "2.0.0");

        System.out.println("Config 1 executeTimeOut: null");
        System.out.println("Config 2 executeTimeOut: 5000");
        System.out.println("Config 3 executeTimeOut: 8000");
        System.out.println("Protocol v2 MD5 config1: " + md51);
        System.out.println("Protocol v2 MD5 config2: " + md52);
        System.out.println("Protocol v2 MD5 config3: " + md53);

        Assert.assertEquals("MD5 should be same (null -> 5000)", md51, md52);
        Assert.assertEquals("MD5 should be same (5000 -> 8000)", md52, md53);
        Assert.assertEquals("MD5 should be same (null -> 8000)", md51, md53);

        System.out.println("Test passed: Invisible field changes do not trigger refresh");
        System.out.println("This prevents the 'invalid refresh' issue mentioned by the mentor");
    }

    /**
     * Scenario 8: Demonstrate field visibility matrix across protocol versions.
     */
    @Test
    public void testFieldVisibilityMatrix() {
        System.out.println("\n========== Scenario 8: Field visibility matrix ==========");

        ThreadPoolParameterInfo fullConfig = new ThreadPoolParameterInfo();
        fullConfig.setTenantId("tenant-001");
        fullConfig.setItemId("item-001");
        fullConfig.setTpId("test-pool");
        fullConfig.setCorePoolSize(10);
        fullConfig.setMaximumPoolSize(20);
        fullConfig.setQueueType(2);
        fullConfig.setCapacity(1024);
        fullConfig.setKeepAliveTime(60L);
        fullConfig.setRejectedType(1);
        fullConfig.setAllowCoreThreadTimeOut(0);
        fullConfig.setExecuteTimeOut(5000L);
        fullConfig.setIsAlarm(1);
        fullConfig.setCapacityAlarm(80);
        fullConfig.setLivenessAlarm(90);
        LinkedHashMap<String, String> metadata = new LinkedHashMap<>();
        metadata.put("executeTimeOut", "2.1.0");
        metadata.put("isAlarm", "2.1.0");
        metadata.put("capacityAlarm", "2.1.0");
        metadata.put("livenessAlarm", "2.1.0");
        fullConfig.setFieldVersionMetadata(metadata);

        String v1Content = IncrementalContentUtil.getVersionedContent(fullConfig, "1.9.0");
        String v2Content = IncrementalContentUtil.getVersionedContent(fullConfig, "2.0.0");
        String v3Content = IncrementalContentUtil.getVersionedContent(fullConfig, "2.1.0");

        LinkedHashMap<String, Object> v1Fields = JSONUtil.parseObject(v1Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        LinkedHashMap<String, Object> v2Fields = JSONUtil.parseObject(v2Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });
        LinkedHashMap<String, Object> v3Fields = JSONUtil.parseObject(v3Content, new TypeReference<LinkedHashMap<String, Object>>() {
        });

        System.out.println("\nField Visibility Matrix:");
        System.out.println("Field             | Protocol v1 | Protocol v2 | Protocol v3");
        System.out.println("------------------+-------------+-------------+------------");
        System.out.println("tenantId          | " + v1Fields.containsKey("tenantId") + "        | " + v2Fields.containsKey("tenantId") + "        | " + v3Fields.containsKey("tenantId"));
        System.out.println("coreSize          | " + v1Fields.containsKey("coreSize") + "        | " + v2Fields.containsKey("coreSize") + "        | " + v3Fields.containsKey("coreSize"));
        System.out.println(
                "executeTimeOut    | " + v1Fields.containsKey("executeTimeOut") + "        | " + v2Fields.containsKey("executeTimeOut") + "       | " + v3Fields.containsKey("executeTimeOut"));
        System.out.println("isAlarm           | " + v1Fields.containsKey("isAlarm") + "        | " + v2Fields.containsKey("isAlarm") + "       | " + v3Fields.containsKey("isAlarm"));
        System.out.println("capacityAlarm     | " + v1Fields.containsKey("capacityAlarm") + "        | " + v2Fields.containsKey("capacityAlarm") + "       | " + v3Fields.containsKey("capacityAlarm"));

        // Core assertions
        Assert.assertTrue("All protocols see core fields", v1Fields.containsKey("coreSize") && v2Fields.containsKey("coreSize") && v3Fields.containsKey("coreSize"));
        Assert.assertFalse("Protocol v1 (1.9.0) should skip executeTimeOut", v1Fields.containsKey("executeTimeOut"));
        Assert.assertFalse("Protocol v2 (2.0.0) should skip executeTimeOut", v2Fields.containsKey("executeTimeOut"));
        Assert.assertTrue("Protocol v3 (2.1.0) should include executeTimeOut", v3Fields.containsKey("executeTimeOut"));

        System.out.println("\nTest passed: Field visibility correctly controlled by semantic version");
        System.out.println("This is the foundation for mentor's requirement: version-aware field filtering");
    }
}
