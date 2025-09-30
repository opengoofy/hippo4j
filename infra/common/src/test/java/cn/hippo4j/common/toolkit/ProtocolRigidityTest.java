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

/**
 * Protocol Rigidity Test: Verifies that field renaming does not trigger unnecessary refreshes.
 */
public class ProtocolRigidityTest {

    /**
     * Scenario 1: Client uses old field names (coreSize/maxSize), Server uses new field names (corePoolSize/maximumPoolSize).
     * Expected Result: MD5 should be the same, no refresh triggered.
     */
    @Test
    public void testFieldRenamingCompatibility_OldClientNewServer() {
        System.out.println("========== Scenario 1: Field Renaming Compatibility (Old Client vs New Server) ==========");

        // Simulate Client configuration (using old field names)
        ThreadPoolParameterInfo clientConfig = new ThreadPoolParameterInfo();
        clientConfig.setTenantId("default");
        clientConfig.setItemId("item-001");
        clientConfig.setTpId("test-pool");
        clientConfig.setCoreSize(10); // old field
        clientConfig.setMaxSize(20);  // old field
        clientConfig.setQueueType(2);
        clientConfig.setCapacity(1024);
        clientConfig.setKeepAliveTime(60L);
        clientConfig.setRejectedType(1);
        clientConfig.setAllowCoreThreadTimeOut(0);

        // Simulate Server configuration (using new field names)
        ThreadPoolParameterInfo serverConfig = new ThreadPoolParameterInfo();
        serverConfig.setTenantId("default");
        serverConfig.setItemId("item-001");
        serverConfig.setTpId("test-pool");
        serverConfig.setCorePoolSize(10);     // new field
        serverConfig.setMaximumPoolSize(20);  // new field
        serverConfig.setQueueType(2);
        serverConfig.setCapacity(1024);
        serverConfig.setKeepAliveTime(60L);
        serverConfig.setRejectedType(1);
        serverConfig.setAllowCoreThreadTimeOut(0);

        // Client-side (v2 protocol) incremental MD5
        String clientContent = IncrementalContentUtil.getCoreContent(clientConfig);
        String clientMd5 = Md5Util.md5Hex(clientContent, "UTF-8");

        // Server-side (v2 protocol) incremental MD5
        String serverContent = IncrementalContentUtil.getCoreContent(serverConfig);
        String serverMd5 = Md5Util.md5Hex(serverContent, "UTF-8");

        System.out.println("Client config (old fields): coreSize=" + clientConfig.getCoreSize() + ", maxSize=" + clientConfig.getMaxSize());
        System.out.println("Server config (new fields): corePoolSize=" + serverConfig.getCorePoolSize() + ", maximumPoolSize=" + serverConfig.getMaximumPoolSize());
        System.out.println("Client incremental content: " + clientContent);
        System.out.println("Server incremental content: " + serverContent);
        System.out.println("Client MD5: " + clientMd5);
        System.out.println("Server MD5: " + serverMd5);

        // Assertion: Even with different field names, MD5 should be the same (adapter unifies fields)
        Assert.assertEquals("MD5 should be the same after field renaming, no refresh triggered", serverMd5, clientMd5);
        System.out.println("Test passed: Field renaming does not trigger unnecessary refresh");
    }

    /**
     * Scenario 2: Client and Server both use new field names.
     * Expected Result: MD5 should be the same.
     */
    @Test
    public void testNewFieldNamesConsistency() {
        System.out.println("\n========== Scenario 2: New Field Name Consistency ==========");

        // Both Client and Server use new field names
        ThreadPoolParameterInfo clientConfig = new ThreadPoolParameterInfo();
        clientConfig.setTenantId("default");
        clientConfig.setItemId("item-001");
        clientConfig.setTpId("test-pool");
        clientConfig.setCorePoolSize(15);
        clientConfig.setMaximumPoolSize(30);
        clientConfig.setQueueType(3);
        clientConfig.setCapacity(2048);

        ThreadPoolParameterInfo serverConfig = new ThreadPoolParameterInfo();
        serverConfig.setTenantId("default");
        serverConfig.setItemId("item-001");
        serverConfig.setTpId("test-pool");
        serverConfig.setCorePoolSize(15);
        serverConfig.setMaximumPoolSize(30);
        serverConfig.setQueueType(3);
        serverConfig.setCapacity(2048);

        String clientMd5 = Md5Util.md5Hex(IncrementalContentUtil.getCoreContent(clientConfig), "UTF-8");
        String serverMd5 = Md5Util.md5Hex(IncrementalContentUtil.getCoreContent(serverConfig), "UTF-8");

        System.out.println("Client MD5: " + clientMd5);
        System.out.println("Server MD5: " + serverMd5);

        Assert.assertEquals("MD5 should be the same when new field names are consistent", serverMd5, clientMd5);
        System.out.println("Test passed: New field names produce consistent MD5");
    }

    /**
     * Scenario 3: Both old and new fields exist, new fields should take priority.
     * Expected Result: Adapter returns new field values.
     */
    @Test
    public void testFieldAdapterPriority() {
        System.out.println("\n========== Scenario 3: Field Adapter Priority ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setCoreSize(10);      // old field
        config.setMaxSize(20);       // old field
        config.setCorePoolSize(15);  // new field (should take priority)
        config.setMaximumPoolSize(30); // new field (should take priority)

        Integer adaptedCore = config.corePoolSizeAdapt();
        Integer adaptedMax = config.maximumPoolSizeAdapt();

        System.out.println("Old field values: coreSize=" + config.getCoreSize() + ", maxSize=" + config.getMaxSize());
        System.out.println("New field values: corePoolSize=" + config.getCorePoolSize() + ", maximumPoolSize=" + config.getMaximumPoolSize());
        System.out.println("Adapter returned values: core=" + adaptedCore + ", max=" + adaptedMax);

        Assert.assertEquals("Adapter should return new field value first", Integer.valueOf(15), adaptedCore);
        Assert.assertEquals("Adapter should return new field value first", Integer.valueOf(30), adaptedMax);
        System.out.println("Test passed: Adapter correctly prioritizes new fields");
    }

    /**
     * Scenario 4: Only old fields exist, adapter should correctly return old values.
     */
    @Test
    public void testFieldAdapterFallback() {
        System.out.println("\n========== Scenario 4: Field Adapter Fallback ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setCoreSize(10); // only old fields
        config.setMaxSize(20);

        Integer adaptedCore = config.corePoolSizeAdapt();
        Integer adaptedMax = config.maximumPoolSizeAdapt();

        System.out.println("Old field values: coreSize=" + config.getCoreSize() + ", maxSize=" + config.getMaxSize());
        System.out.println("New field values: corePoolSize=" + config.getCorePoolSize() + ", maximumPoolSize=" + config.getMaximumPoolSize());
        System.out.println("Adapter returned values: core=" + adaptedCore + ", max=" + adaptedMax);

        Assert.assertEquals("Adapter should fall back to old field value", Integer.valueOf(10), adaptedCore);
        Assert.assertEquals("Adapter should fall back to old field value", Integer.valueOf(20), adaptedMax);
        System.out.println("Test passed: Adapter correctly falls back to old fields");
    }

    /**
     * Scenario 5: v1 client (full MD5) vs v2 client (incremental MD5).
     * Expected Result: v1 and v2 use different comparison strategies.
     */
    @Test
    public void testProtocolVersionDifference() {
        System.out.println("\n========== Scenario 5: Protocol Version Difference ==========");

        ThreadPoolParameterInfo config = new ThreadPoolParameterInfo();
        config.setTenantId("default");
        config.setItemId("item-001");
        config.setTpId("test-pool");
        config.setCorePoolSize(10);
        config.setMaximumPoolSize(20);
        config.setQueueType(2);
        config.setCapacity(1024);
        config.setExecuteTimeOut(5000L); // extended parameter
        config.setIsAlarm(1); // extended parameter

        // v1 protocol: full MD5
        String v1Content = IncrementalContentUtil.getFullContent(config);
        String v1Md5 = Md5Util.md5Hex(v1Content, "UTF-8");

        // v2 protocol: incremental MD5 (core parameters only)
        String v2Content = IncrementalContentUtil.getCoreContent(config);
        String v2Md5 = Md5Util.md5Hex(v2Content, "UTF-8");

        System.out.println("v1 protocol (full) content length: " + v1Content.length());
        System.out.println("v2 protocol (incremental) content length: " + v2Content.length());
        System.out.println("v1 MD5: " + v1Md5);
        System.out.println("v2 MD5: " + v2Md5);
        System.out.println("Does v2 content contain executeTimeOut: " + v2Content.contains("executeTimeOut"));

        Assert.assertNotEquals("MD5 should differ between v1 and v2 protocols", v1Md5, v2Md5);
        Assert.assertFalse("v2 incremental content should not include extended parameters", v2Content.contains("executeTimeOut"));
        System.out.println("Test passed: v1 and v2 protocols use different strategies");
    }
}
