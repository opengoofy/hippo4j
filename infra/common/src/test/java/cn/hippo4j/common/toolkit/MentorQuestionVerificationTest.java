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
 * Verification test for mentor's two key questions:
 * Q1: How to combine multiple fields in a single version for comparison?
 * Q2: How to compare when Server is multiple versions ahead of Client?
 */
public class MentorQuestionVerificationTest {

    @Before
    public void setUp() {
        // Clear registry and set up test environment
        FieldVersionRegistry.clearForTest();

        // Simulate FieldVersionInitializer - Register fields for different versions
        // Version 1.5.0: Initial release (only core fields)

        // Version 2.0.0: Add 5 new fields (simulating single version with multiple fields)
        FieldVersionRegistry.registerField("executeTimeOut", "2.0.0");
        FieldVersionRegistry.registerField("isAlarm", "2.0.0");
        FieldVersionRegistry.registerField("capacityAlarm", "2.0.0");
        FieldVersionRegistry.registerField("livenessAlarm", "2.0.0");
        FieldVersionRegistry.registerField("allowCoreThreadTimeOut", "2.0.0");

        // Version 2.1.0: Add hypothetical fields (for demonstration)
        FieldVersionRegistry.registerField("futureField21A", "2.1.0");
        FieldVersionRegistry.registerField("futureField21B", "2.1.0");

        // Version 2.2.0: Add hypothetical fields (for demonstration)
        FieldVersionRegistry.registerField("futureField22A", "2.2.0");
        FieldVersionRegistry.registerField("futureField22B", "2.2.0");
        FieldVersionRegistry.registerField("futureField22C", "2.2.0");
    }

    /**
     * Q1 Verification: Single version (2.0.0) introduces 5 fields - how to combine for comparison?
     *
     * Expected behavior:
     * - Client 1.5.0: Cannot see any 2.0.0 fields (all 5 filtered out)
     * - Client 2.0.0: Can see all 5 fields (all 5 included)
     * - All 5 fields are treated equally (no precedence/priority among them)
     */
    @Test
    public void testQ1_MultipleFieldsInSingleVersion() {
        System.out.println("\n========== Q1 Verification: Multiple Fields in Single Version ==========");

        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        // Core fields
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(10);
        param.setMaxSize(20);
        param.setQueueType(1);
        param.setCapacity(1024);
        param.setKeepAliveTime(60L);
        param.setRejectedType(1);

        // Version 2.0.0 fields (5 fields introduced together)
        param.setExecuteTimeOut(5000L);
        param.setIsAlarm(1);
        param.setCapacityAlarm(80);
        param.setLivenessAlarm(80);
        param.setAllowCoreThreadTimeOut(0);

        // Test 1: Client 1.5.0 - should NOT see any 2.0.0 fields
        String content15 = IncrementalContentUtil.getVersionedContent(param, "1.5.0");
        System.out.println("\n[Client 1.5.0] Content:");
        System.out.println(content15);

        Assert.isTrue(!content15.contains("executeTimeOut"), "1.5.0 should NOT see executeTimeOut");
        Assert.isTrue(!content15.contains("isAlarm"), "1.5.0 should NOT see isAlarm");
        Assert.isTrue(!content15.contains("capacityAlarm"), "1.5.0 should NOT see capacityAlarm");
        Assert.isTrue(!content15.contains("livenessAlarm"), "1.5.0 should NOT see livenessAlarm");
        Assert.isTrue(!content15.contains("allowCoreThreadTimeOut"), "1.5.0 should NOT see allowCoreThreadTimeOut");

        // Test 2: Client 2.0.0 - should see ALL 5 fields
        String content20 = IncrementalContentUtil.getVersionedContent(param, "2.0.0");
        System.out.println("\n[Client 2.0.0] Content:");
        System.out.println(content20);

        Assert.isTrue(content20.contains("executeTimeOut"), "2.0.0 should see executeTimeOut");
        Assert.isTrue(content20.contains("isAlarm"), "2.0.0 should see isAlarm");
        Assert.isTrue(content20.contains("capacityAlarm"), "2.0.0 should see capacityAlarm");
        Assert.isTrue(content20.contains("livenessAlarm"), "2.0.0 should see livenessAlarm");
        Assert.isTrue(content20.contains("allowCoreThreadTimeOut"), "2.0.0 should see allowCoreThreadTimeOut");

        // Verify MD5 difference
        String md5_15 = Md5Util.md5Hex(content15, "UTF-8");
        String md5_20 = Md5Util.md5Hex(content20, "UTF-8");

        System.out.println("\n[MD5 Comparison]:");
        System.out.println("  Client 1.5.0 MD5: " + md5_15);
        System.out.println("  Client 2.0.0 MD5: " + md5_20);
        System.out.println("  MD5 Different: " + !md5_15.equals(md5_20));

        Assert.isTrue(!md5_15.equals(md5_20), "MD5 should be different for different client versions");

        System.out.println("\n✅ Q1 Answer: Multiple fields in a single version are combined by:");
        System.out.println("   1. Each field has the SAME minimum version requirement (2.0.0)");
        System.out.println("   2. Client version comparison: clientVersion >= fieldVersion");
        System.out.println("   3. ALL fields pass/fail the version check TOGETHER");
        System.out.println("   4. Filtered content generates different MD5 for different client versions");
    }

    /**
     * Q2 Verification: Server is multiple versions ahead (Server 2.2.0, Client 1.5.0)
     *
     * Expected behavior:
     * - Client 1.5.0 skips 2.0.0, 2.1.0, 2.2.0 fields (跨3个版本)
     * - Client 2.0.0 sees 2.0.0 fields but skips 2.1.0, 2.2.0 fields (跨2个版本)
     * - Client 2.1.0 sees 2.0.0 + 2.1.0 fields but skips 2.2.0 fields (跨1个版本)
     * - Client 2.2.0 sees all fields (同版本)
     */
    @Test
    public void testQ2_ServerMultipleVersionsAhead() {
        System.out.println("\n========== Q2 Verification: Server Multiple Versions Ahead ==========");

        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        // Core fields
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(10);
        param.setMaxSize(20);

        // Version 2.0.0 fields (5 fields)
        param.setExecuteTimeOut(5000L);
        param.setIsAlarm(1);
        param.setCapacityAlarm(80);
        param.setLivenessAlarm(80);
        param.setAllowCoreThreadTimeOut(0);

        // Note: Version 2.1.0 and 2.2.0 fields are hypothetical (registered but not on param object)
        // The test demonstrates version filtering logic without actual field values

        System.out.println("\n[Scenario] Server Version: 2.2.0 (has 2.0.0 + 2.1.0 + 2.2.0 fields)");
        System.out.println("           Testing clients: 1.5.0, 2.0.0, 2.1.0, 2.2.0\n");

        // Test 1: Client 1.5.0 (跨3个版本 - skips 2.0.0, 2.1.0, 2.2.0)
        String content15 = IncrementalContentUtil.getVersionedContent(param, "1.5.0");
        System.out.println("[Client 1.5.0] (3 versions behind)");
        System.out.println("  Content: " + content15);
        System.out.println("  Field Count: " + countFields(content15));

        Assert.isTrue(!content15.contains("executeTimeOut"), "1.5.0 should NOT see 2.0.0 fields");

        // Test 2: Client 2.0.0 (跨2个版本 - sees 2.0.0, skips hypothetical 2.1.0, 2.2.0)
        String content20 = IncrementalContentUtil.getVersionedContent(param, "2.0.0");
        System.out.println("\n[Client 2.0.0] (2 versions behind)");
        System.out.println("  Content: " + content20);
        System.out.println("  Field Count: " + countFields(content20));

        Assert.isTrue(content20.contains("executeTimeOut"), "2.0.0 should see 2.0.0 fields");

        // Test 3: Client 2.1.0 (跨1个版本 - sees 2.0.0, skips hypothetical 2.2.0)
        String content21 = IncrementalContentUtil.getVersionedContent(param, "2.1.0");
        System.out.println("\n[Client 2.1.0] (1 version behind)");
        System.out.println("  Content: " + content21);
        System.out.println("  Field Count: " + countFields(content21));

        Assert.isTrue(content21.contains("executeTimeOut"), "2.1.0 should see 2.0.0 fields");

        // Test 4: Client 2.2.0 (同版本 - sees all actual fields)
        String content22 = IncrementalContentUtil.getVersionedContent(param, "2.2.0");
        System.out.println("\n[Client 2.2.0] (same version)");
        System.out.println("  Content: " + content22);
        System.out.println("  Field Count: " + countFields(content22));

        Assert.isTrue(content22.contains("executeTimeOut"), "2.2.0 should see 2.0.0 fields");

        // Verify MD5 progression
        String md5_15 = Md5Util.md5Hex(content15, "UTF-8");
        String md5_20 = Md5Util.md5Hex(content20, "UTF-8");
        String md5_21 = Md5Util.md5Hex(content21, "UTF-8");
        String md5_22 = Md5Util.md5Hex(content22, "UTF-8");

        System.out.println("\n[MD5 Comparison Across Versions]:");
        System.out.println("  Client 1.5.0 MD5: " + md5_15);
        System.out.println("  Client 2.0.0 MD5: " + md5_20 + " (different: " + !md5_15.equals(md5_20) + ")");
        System.out.println("  Client 2.1.0 MD5: " + md5_21 + " (different: " + !md5_20.equals(md5_21) + ")");
        System.out.println("  Client 2.2.0 MD5: " + md5_22 + " (different: " + !md5_21.equals(md5_22) + ")");

        Assert.isTrue(!md5_15.equals(md5_20), "1.5.0 and 2.0.0 should have different MD5");
        // Note: md5_20, md5_21, md5_22 are same because we don't have actual 2.1.0/2.2.0 fields in param
        // In production with real fields, each version would have different MD5

        System.out.println("\n✅ Q2 Answer: When Server is multiple versions ahead:");
        System.out.println("   1. Each field independently checks: clientVersion >= fieldIntroducedVersion");
        System.out.println("   2. Incremental visibility: Client sees all fields from its version and below");
        System.out.println("   3. Transitive compatibility: 1.5.0 → 2.0.0 → 2.1.0 → 2.2.0 forms a version chain");
        System.out.println("   4. Each client gets a stable, version-appropriate MD5");
        System.out.println("   5. No 'skip version' issue - comparison is per-field, not per-version");
    }

    /**
     * Edge case: Client version between two server versions (e.g., Client 2.0.5)
     */
    @Test
    public void testEdgeCase_ClientBetweenServerVersions() {
        System.out.println("\n========== Edge Case: Client Between Server Versions ==========");

        ThreadPoolParameterInfo param = new ThreadPoolParameterInfo();
        param.setTenantId("tenant-001");
        param.setItemId("item-001");
        param.setTpId("test-pool");
        param.setCoreSize(10);

        param.setExecuteTimeOut(5000L); // 2.0.0

        // Client 2.0.5 (between 2.0.0 and 2.1.0)
        String content205 = IncrementalContentUtil.getVersionedContent(param, "2.0.5");
        System.out.println("\n[Client 2.0.5] (between 2.0.0 and 2.1.0)");
        System.out.println("  Content: " + content205);

        // 2.0.5 >= 2.0.0 → should see executeTimeOut
        Assert.isTrue(content205.contains("executeTimeOut"), "2.0.5 >= 2.0.0, should see 2.0.0 fields");

        System.out.println("\n✅ Edge Case Handled: Semantic version comparison ensures correct filtering");
    }

    /**
     * Real-world scenario: Configuration update from old client
     */
    @Test
    public void testRealWorld_OldClientUpdatesConfig() {
        System.out.println("\n========== Real-World Scenario: Old Client Updates Config ==========");

        // Scenario: Server 2.2.0, Client 1.5.0 calls save_or_update
        ThreadPoolParameterInfo paramFromClient = new ThreadPoolParameterInfo();
        paramFromClient.setTenantId("tenant-001");
        paramFromClient.setItemId("item-001");
        paramFromClient.setTpId("test-pool");
        paramFromClient.setCoreSize(15); // Client only knows about core fields
        paramFromClient.setMaxSize(30);

        // Server has additional fields from newer versions (which client doesn't send)
        ThreadPoolParameterInfo paramOnServer = new ThreadPoolParameterInfo();
        paramOnServer.setTenantId("tenant-001");
        paramOnServer.setItemId("item-001");
        paramOnServer.setTpId("test-pool");
        paramOnServer.setCoreSize(15);
        paramOnServer.setMaxSize(30);
        paramOnServer.setExecuteTimeOut(5000L); // Server 2.0.0 field
        // Note: Hypothetical 2.1.0 and 2.2.0 fields are registered but not on param object

        // Generate MD5 for Client 1.5.0 view
        String clientContent = IncrementalContentUtil.getVersionedContent(paramFromClient, "1.5.0");
        String serverContentForClient15 = IncrementalContentUtil.getVersionedContent(paramOnServer, "1.5.0");

        String clientMd5 = Md5Util.md5Hex(clientContent, "UTF-8");
        String serverMd5 = Md5Util.md5Hex(serverContentForClient15, "UTF-8");

        System.out.println("\n[Client 1.5.0 View]:");
        System.out.println("  Client sent content: " + clientContent);
        System.out.println("  Server content (filtered for 1.5.0): " + serverContentForClient15);
        System.out.println("  Client MD5: " + clientMd5);
        System.out.println("  Server MD5 (for 1.5.0): " + serverMd5);
        System.out.println("  MD5 Match: " + clientMd5.equals(serverMd5));

        Assert.isTrue(clientMd5.equals(serverMd5), "Client and Server MD5 should match when viewed through same version lens");

        System.out.println("\n✅ Real-World Scenario Verified:");
        System.out.println("   - Old client updates config → no invalid refresh triggered");
        System.out.println("   - Server's newer fields are invisible to old client");
        System.out.println("   - MD5 comparison is version-aware and stable");
    }

    private int countFields(String jsonContent) {
        int count = 0;
        for (char c : jsonContent.toCharArray()) {
            if (c == ':') {
                count++;
            }
        }
        return count;
    }
}
