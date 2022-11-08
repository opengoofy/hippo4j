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

package cn.hippo4j.common.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * test for {@link PluginRuntimeInfo}
 */
public class PluginRuntimeInfoTest {

    @Test
    public void test() {
        PluginRuntimeInfo runtime = new PluginRuntimeInfo("test")
                .setDescription("test");
        Assert.assertEquals("test", runtime.getPluginId());
        Assert.assertTrue(runtime.getInfoList().isEmpty());
        Assert.assertEquals("test", runtime.getDescription());

        runtime.addInfo("item", "item");
        PluginRuntimeInfo.Info info = runtime.getInfoList().get(0);
        Assert.assertEquals("item", info.getName());
        Assert.assertEquals("item", info.getValue());
    }

}
