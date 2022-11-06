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
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Objects;

import static cn.hippo4j.common.constant.Constants.LONG_PULLING_CLIENT_IDENTIFICATION;

/**
 * RequestUtil Test
 */
public class RequestUtilTest {

    @Test
    public void existClientIdentifyTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(LONG_PULLING_CLIENT_IDENTIFICATION, "test");
        String clientIdentify = RequestUtil.getClientIdentify(request);
        Assert.isTrue(Objects.equals(clientIdentify, "test"));
    }

    @Test
    public void notExistClientIdentifyTest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String clientIdentify = RequestUtil.getClientIdentify(request);
        Assert.isTrue(Objects.equals(clientIdentify, ""));
    }

}
