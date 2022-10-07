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

package cn.hippo4j.config.toolkit;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.config.model.ConfigAllInfo;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Md5ConfigUtil Test
 */
public class Md5ConfigUtilTest {

    @Test
    public void getKeyTest() {
        String key = Md5ConfigUtil.getKey("DataId", "Group");
        Assert.isTrue(Objects.equals("DataId+Group", key));
    }

    @Test
    public void getKeySpecialTest() {
        String key = Md5ConfigUtil.getKey("DataId+", "Group");
        Assert.isTrue(Objects.equals("DataId%2B+Group", key));

        String key1 = Md5ConfigUtil.getKey("DataId%", "Group");
        Assert.isTrue(Objects.equals("DataId%25+Group", key1));
    }

    @Test
    public void getKeyTenantIdentifyTest() {
        String key = Md5ConfigUtil.getKey("DataId", "Group", "Tenant", "Identify");
        Assert.isTrue(Objects.equals("DataId+Group+Tenant+Identify", key));
    }

    @Test
    public void getKeyTenantIdentifySpecialTest() {
        String key = Md5ConfigUtil.getKey("DataId+", "Group+", "Tenant+", "Identify");
        Assert.isTrue(Objects.equals("DataId%2B+Group%2B+Tenant%2B+Identify", key));
    }

    @Test
    public void compareMd5ResultStringEmptyTest() {
        String key = null;
        try {
            key = Md5ConfigUtil.compareMd5ResultString(new ArrayList<>());
        } catch (IOException ignored) {

        }
        Assert.isTrue(Objects.equals(StringUtil.EMPTY, key));
    }

    @Test
    public void compareMd5ResultStringTest() {
        String key = null;
        try {
            key = Md5ConfigUtil.compareMd5ResultString(Lists.newArrayList("DataId+Group"));
        } catch (IOException ignored) {

        }
        Assert.isTrue(Objects.equals("DataId%02Group%01", key));
    }

    @Test
    public void getClientMd5MapTest() {
        ConfigAllInfo configAllInfo = new ConfigAllInfo();
        configAllInfo.setDesc("hippo4j config");
        String tpContentMd5 = Md5ConfigUtil.getTpContentMd5(configAllInfo);
        Assert.isTrue(StringUtil.isNotEmpty(tpContentMd5));
    }

}
