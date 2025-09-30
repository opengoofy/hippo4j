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
import lombok.var;
import org.junit.Assert;
import org.junit.Test;

/**
 * Extensibility issue test: verify that adding new extended parameters does not trigger invalid refresh.
 */
public class ExtensibilityTest {

    /**
     * Scenario 1: Server adds an extended parameter (executeTimeOut), Client does not have this parameter.
     * Expected result: Under v2 protocol, MD5 remains the same, no refresh triggered.
     */
    @Test
    public void testExtendedParameterAddition_ExecuteTimeOut() {
        System.out.println("========== Scenario 1: Server adds executeTimeOut extended parameter ==========");

        // Client configuration (without executeTimeOut)
        ThreadPoolParameterInfo clientConfig = new ThreadPoolParameterInfo();
        clientConfig.setTenantId("default");
        clientConfig.setItemId("item-001");
        clientConfig.setTpId("test-pool");
        clientConfig.setCorePoolSize(10);
        clientConfig.setMaximumPoolSize(20);
        clientConfig.setQueueType(2);
        clientConfig.setCapacity(1024);
        clientConfig.setKeepAliveTime(60L);
        clientConfig.setRejectedType(1);
        clientConfig.setAllowCoreThreadTimeOut(0);
        // Note: executeTimeOut not set

        // Server configuration (with executeTimeOut added)
        ThreadPoolParameterInfo serverConfig = new ThreadPoolParameterInfo();
        serverConfig.setTenantId("default");
        serverConfig.setItemId("item-001");
        serverConfig.setTpId("test-pool");
        serverConfig.setCorePoolSize(10);
        serverConfig.setMaximumPoolSize(20);
        serverConfig.setQueueType(2);
        serverConfig.setCapacity(1024);
        serverConfig.setKeepAliveTime(60L);
        serverConfig.setRejectedType(1);
        serverConfig.setAllowCoreThreadTimeOut(0);
        serverConfig.setExecuteTimeOut(5000L); // new extended parameter

        // v2 protocol: only core parameters are included in MD5 calculation
        String clientContent = IncrementalContentUtil.getCoreContent(clientConfig);
        String clientMd5 = Md5Util.md5Hex(clientContent, "UTF-8");

        String serverContent = IncrementalContentUtil.getCoreContent(serverConfig);
        String serverMd5 = Md5Util.md5Hex(serverContent, "UTF-8");

        System.out.println("Client config: executeTimeOut=" + clientConfig.getExecuteTimeOut());
        System.out.println("Server config: executeTimeOut=" + serverConfig.getExecuteTimeOut());
        System.out.println("Client incremental content: " + clientContent);
        System.out.println("Server incremental content: " + serverContent);
        System.out.println("Client MD5: " + clientMd5);
        System.out.println("Server MD5: " + serverMd5);
        System.out.println("Does server incremental content contain executeTimeOut: " + serverContent.contains("executeTimeOut"));

        Assert.assertFalse("Incremental content should not contain extended parameter executeTimeOut", serverContent.contains("executeTimeOut"));
        Assert.assertEquals("Adding extended parameter should not affect incremental MD5 and should not trigger refresh", serverMd5, clientMd5);
        System.out.println("Test passed: Adding executeTimeOut does not trigger invalid refresh");
    }

    /**
     * Scenario 2: Server adds multiple extended parameters (executeTimeOut, isAlarm, capacityAlarm).
     * Expected result: Under v2 protocol, MD5 remains the same, no refresh triggered.
     */
    @Test
    public void testMultipleExtendedParametersAddition() {
        System.out.println("\n========== Scenario 2: Server adds multiple extended parameters ==========");

        // Client configuration (without extended parameters)
        ThreadPoolParameterInfo clientConfig = new ThreadPoolParameterInfo();
        clientConfig.setTenantId("default");
        clientConfig.setItemId("item-001");
        clientConfig.setTpId("test-pool");
        clientConfig.setCorePoolSize(10);
        clientConfig.setMaximumPoolSize(20);
        clientConfig.setQueueType(2);
        clientConfig.setCapacity(1024);

        // Server configuration (with multiple extended parameters)
        ThreadPoolParameterInfo serverConfig = new ThreadPoolParameterInfo();
        serverConfig.setTenantId("default");
        serverConfig.setItemId("item-001");
        serverConfig.setTpId("test-pool");
        serverConfig.setCorePoolSize(10);
        serverConfig.setMaximumPoolSize(20);
        serverConfig.setQueueType(2);
        serverConfig.setCapacity(1024);
        serverConfig.setExecuteTimeOut(5000L); // extended parameter 1
        serverConfig.setIsAlarm(1); // extended parameter 2
        serverConfig.setCapacityAlarm(80); // extended parameter 3
        serverConfig.setLivenessAlarm(90); // extended parameter 4

        String clientMd5 = Md5Util.md5Hex(IncrementalContentUtil.getCoreContent(clientConfig), "UTF-8");
        String serverMd5 = Md5Util.md5Hex(IncrementalContentUtil.getCoreContent(serverConfig), "UTF-8");

        System.out.println("Server added extended parameters: executeTimeOut=" + serverConfig.getExecuteTimeOut()
                + ", isAlarm=" + serverConfig.getIsAlarm()
                + ", capacityAlarm=" + serverConfig.getCapacityAlarm()
                + ", livenessAlarm=" + serverConfig.getLivenessAlarm());
        System.out.println("Client MD5: " + clientMd5);
        System.out.println("Server MD5: " + serverMd5);

        Assert.assertEquals("Adding multiple extended parameters should not trigger refresh", serverMd5, clientMd5);
        System.out.println("Test passed: Adding multiple extended parameters does not trigger invalid refresh");
    }

    /**
     * Scenario 3: Only extended parameters changed, core parameters remain unchanged.
     * Expected result: hasCoreChanges returns false, hasExtendedChanges returns true.
     */
    @Test
    public void testOnlyExtendedParametersChanged() {
        System.out.println("\n========== Scenario 3: Only extended parameters changed ==========");

        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setTenantId("default");
        oldConfig.setItemId("item-001");
        oldConfig.setTpId("test-pool");
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);
        oldConfig.setQueueType(2);
        oldConfig.setCapacity(1024);
        oldConfig.setExecuteTimeOut(3000L);
        oldConfig.setIsAlarm(0);

        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setTenantId("default");
        newConfig.setItemId("item-001");
        newConfig.setTpId("test-pool");
        newConfig.setCorePoolSize(10);
        newConfig.setMaximumPoolSize(20);
        newConfig.setQueueType(2);
        newConfig.setCapacity(1024);
        newConfig.setExecuteTimeOut(5000L);
        newConfig.setIsAlarm(1);

        boolean hasCoreChanges = IncrementalContentUtil.hasCoreChanges(oldConfig, newConfig);
        boolean hasExtendedChanges = IncrementalContentUtil.hasExtendedChanges(oldConfig, newConfig);

        System.out.println("Core parameter changes: " + hasCoreChanges);
        System.out.println("Extended parameter changes: " + hasExtendedChanges);

        Assert.assertFalse("Core parameters should not change", hasCoreChanges);
        Assert.assertTrue("Extended parameters should change", hasExtendedChanges);
        System.out.println("Test passed: Correctly identifies only extended parameters changed");
    }

    /**
     * Scenario 4: Both core and extended parameters changed.
     * Expected result: hasCoreChanges returns true, hasExtendedChanges returns true.
     */
    @Test
    public void testBothCoreAndExtendedParametersChanged() {
        System.out.println("\n========== Scenario 4: Both core and extended parameters changed ==========");

        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);
        oldConfig.setQueueType(2);
        oldConfig.setExecuteTimeOut(3000L);

        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setCorePoolSize(15);
        newConfig.setMaximumPoolSize(30);
        newConfig.setQueueType(2);
        newConfig.setExecuteTimeOut(5000L);

        boolean hasCoreChanges = IncrementalContentUtil.hasCoreChanges(oldConfig, newConfig);
        boolean hasExtendedChanges = IncrementalContentUtil.hasExtendedChanges(oldConfig, newConfig);

        System.out.println("Core parameter changes: " + hasCoreChanges);
        System.out.println("Extended parameter changes: " + hasExtendedChanges);

        Assert.assertTrue("Core parameters should change", hasCoreChanges);
        Assert.assertTrue("Extended parameters should change", hasExtendedChanges);
        System.out.println("Test passed: Correctly identifies both core and extended parameters changed");
    }

    /**
     * Scenario 5: v1 client refreshes due to extended parameter changes, v2 client does not.
     * Expected result: v1 MD5 differs, v2 MD5 remains the same.
     */
    @Test
    public void testProtocolVersionBehaviorDifference() {
        System.out.println("\n========== Scenario 5: v1 and v2 protocol differences in handling extended parameters ==========");

        ThreadPoolParameterInfo oldConfig = new ThreadPoolParameterInfo();
        oldConfig.setTenantId("default");
        oldConfig.setItemId("item-001");
        oldConfig.setTpId("test-pool");
        oldConfig.setCorePoolSize(10);
        oldConfig.setMaximumPoolSize(20);
        oldConfig.setQueueType(2);
        oldConfig.setExecuteTimeOut(3000L);

        ThreadPoolParameterInfo newConfig = new ThreadPoolParameterInfo();
        newConfig.setTenantId("default");
        newConfig.setItemId("item-001");
        newConfig.setTpId("test-pool");
        newConfig.setCorePoolSize(10);
        newConfig.setMaximumPoolSize(20);
        newConfig.setQueueType(2);
        newConfig.setExecuteTimeOut(5000L);

        // v1 protocol: full MD5
        String oldV1Md5 = Md5Util.md5Hex(IncrementalContentUtil.getFullContent(oldConfig), "UTF-8");
        String newV1Md5 = Md5Util.md5Hex(IncrementalContentUtil.getFullContent(newConfig), "UTF-8");

        // v2 protocol: incremental MD5 (only core parameters)
        String oldV2Md5 = Md5Util.md5Hex(IncrementalContentUtil.getCoreContent(oldConfig), "UTF-8");
        String newV2Md5 = Md5Util.md5Hex(IncrementalContentUtil.getCoreContent(newConfig), "UTF-8");

        System.out.println("Extended parameter changed: executeTimeOut " + oldConfig.getExecuteTimeOut() + " -> " + newConfig.getExecuteTimeOut());
        System.out.println("v1 protocol: oldMd5=" + oldV1Md5 + ", newMd5=" + newV1Md5 + ", equal=" + oldV1Md5.equals(newV1Md5));
        System.out.println("v2 protocol: oldMd5=" + oldV2Md5 + ", newMd5=" + newV2Md5 + ", equal=" + oldV2Md5.equals(newV2Md5));

        Assert.assertNotEquals("v1 protocol: extended parameter changes should trigger refresh (MD5 differs)", oldV1Md5, newV1Md5);
        Assert.assertEquals("v2 protocol: extended parameter changes should not trigger refresh (MD5 same)", oldV2Md5, newV2Md5);
        System.out.println("Test passed: v2 protocol correctly isolates extended parameter changes");
    }

    /**
     * Scenario 6: getChangesSummary correctly identifies change types.
     */
    @Test
    public void testChangesSummary() {
        System.out.println("\n========== Scenario 6: Change summary identification ==========");

        ThreadPoolParameterInfo baseConfig = new ThreadPoolParameterInfo();
        baseConfig.setCorePoolSize(10);
        baseConfig.setMaximumPoolSize(20);
        baseConfig.setQueueType(2);
        baseConfig.setExecuteTimeOut(3000L);

        // Case 1: Only extended parameter changed
        ThreadPoolParameterInfo extendedOnlyConfig = new ThreadPoolParameterInfo();
        extendedOnlyConfig.setCorePoolSize(10);
        extendedOnlyConfig.setMaximumPoolSize(20);
        extendedOnlyConfig.setQueueType(2);
        extendedOnlyConfig.setExecuteTimeOut(5000L);

        var extendedOnlySummary = IncrementalContentUtil.getChangesSummary(baseConfig, extendedOnlyConfig);
        System.out.println("Only extended parameter change summary: " + extendedOnlySummary);
        Assert.assertEquals("extended", extendedOnlySummary.get("type"));

        // Case 2: Core parameters changed
        ThreadPoolParameterInfo coreChangedConfig = new ThreadPoolParameterInfo();
        coreChangedConfig.setCorePoolSize(15);
        coreChangedConfig.setMaximumPoolSize(20);
        coreChangedConfig.setQueueType(2);
        coreChangedConfig.setExecuteTimeOut(3000L);

        var coreChangedSummary = IncrementalContentUtil.getChangesSummary(baseConfig, coreChangedConfig);
        System.out.println("Core parameter change summary: " + coreChangedSummary);
        Assert.assertEquals("core", coreChangedSummary.get("type"));

        System.out.println("Test passed: Change summary correctly identified");
    }
}