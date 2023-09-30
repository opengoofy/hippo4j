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

public class SingletonTest {

    @Test
    public void assertSingletonGet() {
        Assert.assertNull(Singleton.get("userName"));
        Singleton.put("userName", "hippo4j");
        Assert.assertEquals("hippo4j", Singleton.get("userName"));
        ThreadPoolParameterInfo threadPoolParameterInfo = ThreadPoolParameterInfo.builder().tenantId("prescription")
                .itemId("dynamic-threadpool-example").tpId("message-consume").content("描述信息").corePoolSize(1)
                .maximumPoolSize(2).queueType(1).capacity(4).keepAliveTime(513L).executeTimeOut(null).rejectedType(4)
                .isAlarm(1).capacityAlarm(80).livenessAlarm(80).allowCoreThreadTimeOut(1).build();
        Singleton.put(threadPoolParameterInfo);
        Assert.assertEquals(threadPoolParameterInfo, Singleton.get(ThreadPoolParameterInfo.class.getName()));
    }

    @Test
    public void assertSingletonGet2() {
        Assert.assertNull(Singleton.get("userName1", () -> null));
        Assert.assertEquals("hippo4j", Singleton.get("userName1", () -> "hippo4j"));
        Assert.assertEquals("123456", Singleton.get("pw", () -> "123456") + "");
        Assert.assertNotEquals("135790", Singleton.get("pw", () -> "135790") + "");
    }
}
