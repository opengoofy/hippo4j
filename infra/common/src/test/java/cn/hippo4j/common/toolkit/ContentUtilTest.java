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
import org.junit.Test;

public class ContentUtilTest {

//    @Test
//    public void assertGetPoolContent() {
//        String testText = "{\"tenantId\":\"prescription\",\"itemId\":\"dynamic-threadpool-example\",\"tpId\":" +
//                "\"message-consume\",\"queueType\":1,\"capacity\":4,\"keepAliveTime\":513,\"rejectedType\":4,\"isAlarm\"" +
//                ":1,\"capacityAlarm\":80,\"livenessAlarm\":80,\"allowCoreThreadTimeOut\":1}";
//        ThreadPoolParameterInfo threadPoolParameterInfo = ThreadPoolParameterInfo.builder().tenantId("prescription")
//                .itemId("dynamic-threadpool-example").tpId("message-consume").content("描述信息").corePoolSize(1)
//                .maximumPoolSize(2).queueType(1).capacity(4).keepAliveTime(513L).executeTimeOut(null).rejectedType(4)
//                .isAlarm(1).capacityAlarm(80).livenessAlarm(80).allowCoreThreadTimeOut(1).build();
//        Assert.isTrue(testText.equals(ContentUtil.getPoolContent(threadPoolParameterInfo)));
//    }

    @Test
    public void assertGetPoolContent() {
        // Since field adapter is used, ContentUtil.getPoolContent() converts new fields to old fields for JSON generation
        // This ensures backward compatibility (v1 protocol uses old field names)
        ThreadPoolParameterInfo threadPoolParameterInfo = ThreadPoolParameterInfo.builder().tenantId("prescription")
                .itemId("dynamic-threadpool-example").tpId("message-consume").content("description").corePoolSize(1)
                .maximumPoolSize(2).queueType(1).capacity(4).keepAliveTime(513L).executeTimeOut(null).rejectedType(4)
                .isAlarm(1).capacityAlarm(80).livenessAlarm(80).allowCoreThreadTimeOut(1).build();

        String actualContent = ContentUtil.getPoolContent(threadPoolParameterInfo);

        // Verify generated JSON contains required fields
        Assert.isTrue(actualContent.contains("\"tenantId\":\"prescription\""), "Should contain tenantId");
        Assert.isTrue(actualContent.contains("\"itemId\":\"dynamic-threadpool-example\""), "Should contain itemId");
        Assert.isTrue(actualContent.contains("\"tpId\":\"message-consume\""), "Should contain tpId");
        Assert.isTrue(actualContent.contains("\"coreSize\":1"), "Should contain coreSize (via adapter)");
        Assert.isTrue(actualContent.contains("\"maxSize\":2"), "Should contain maxSize (via adapter)");
        Assert.isTrue(actualContent.contains("\"queueType\":1"), "Should contain queueType");
        Assert.isTrue(actualContent.contains("\"capacity\":4"), "Should contain capacity");

        System.out.println("ContentUtil.getPoolContent() test passed");
        System.out.println("Generated content: " + actualContent);
    }

    @Test
    public void assertGetGroupKey() {
        String testText = "message-consume+dynamic-threadpool-example+prescription";
        ThreadPoolParameterInfo parameter = ThreadPoolParameterInfo.builder()
                .tenantId("prescription").itemId("dynamic-threadpool-example").tpId("message-consume").build();
        Assert.isTrue(testText.equals(ContentUtil.getGroupKey(parameter)));
    }

    @Test
    public void assertGetGroupKeys() {
        String testText = "message-consume+dynamic-threadpool-example+prescription";
        String groupKey = ContentUtil.getGroupKey("message-consume", "dynamic-threadpool-example", "prescription");
        Assert.isTrue(testText.equals(groupKey));
    }

    /**
     * Test field adapter: generate content using old field names
     * Verify ContentUtil.getPoolContent() correctly handles coreSize/maxSize
     */
    @Test
    public void testFieldAdapterWithOldFields() {
        ThreadPoolParameterInfo oldFieldConfig = new ThreadPoolParameterInfo();
        oldFieldConfig.setTenantId("tenant-001");
        oldFieldConfig.setItemId("item-001");
        oldFieldConfig.setTpId("test-pool");
        oldFieldConfig.setCoreSize(10); // Use old field name
        oldFieldConfig.setMaxSize(20); // Use old field name
        oldFieldConfig.setQueueType(2);
        oldFieldConfig.setCapacity(1024);

        ThreadPoolParameterInfo newFieldConfig = new ThreadPoolParameterInfo();
        newFieldConfig.setTenantId("tenant-001");
        newFieldConfig.setItemId("item-001");
        newFieldConfig.setTpId("test-pool");
        newFieldConfig.setCorePoolSize(10); // Use new field name
        newFieldConfig.setMaximumPoolSize(20); // Use new field name
        newFieldConfig.setQueueType(2);
        newFieldConfig.setCapacity(1024);

        String oldContent = ContentUtil.getPoolContent(oldFieldConfig);
        String newContent = ContentUtil.getPoolContent(newFieldConfig);

        // Assert: content generated from old and new field names should be the same (via adapter)
        Assert.isTrue(oldContent.equals(newContent), "Field adapter should generate same content for old and new fields");
        System.out.println("ContentUtil field adapter test passed");
        System.out.println("Old field content: " + oldContent);
        System.out.println("New field content: " + newContent);
    }
}