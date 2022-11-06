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

package cn.hippo4j.common.toolkit.http;

import cn.hippo4j.common.toolkit.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpUtilsTest {

    /**
     * test post url
     */
    static String postUrl = "http://console.hippo4j.cn/hippo4j/v1/cs/";

    /**
     * test get url
     */
    static String getUrl = "https://hippo4j.cn/";

    @Test
    public void get() {
        String s = HttpUtil.get(getUrl);
        Assert.assertNotNull(s);
    }

    @Test
    public void restApiPost() {
        String loginUrl = postUrl + "auth/login";
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setPassword("hippo4j");
        loginInfo.setUsername("hippo4j");
        loginInfo.setRememberMe(1);
        String s = HttpUtil.post(loginUrl, loginInfo);
        Result result = JSONUtil.parseObject(s, Result.class);
        Assert.assertNotNull(result);
        String data = result.getData().getData();
        Assert.assertNotNull(data);
    }

    @Test
    public void testRestApiPost() {
        String loginUrl = postUrl + "auth/login";
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setPassword("hippo4j");
        loginInfo.setUsername("hippo4j");
        loginInfo.setRememberMe(1);
        Result result = HttpUtil.post(loginUrl, loginInfo, Result.class);
        Assert.assertNotNull(result);
        String data = result.getData().getData();
        Assert.assertNotNull(data);
    }

    // @Test(expected = SocketTimeoutException.class)
    public void testRestApiPostTimeout() {
        String loginUrl = postUrl + "auth/login";
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setPassword("hippo4j");
        loginInfo.setUsername("hippo4j");
        loginInfo.setRememberMe(1);
        HttpUtil.post(loginUrl, loginInfo, 1, Result.class);
    }

    @Test
    public void buildUrl() {
        Map<String, String> map = new HashMap<>();
        map.put("password", "hippo4j");
        map.put("username", "hippo4j");
        String s = HttpUtil.buildUrl(getUrl, map);
        Assert.assertEquals(getUrl + "?password=hippo4j&username=hippo4j", s);
    }

    @Getter
    @Setter
    private static class LoginInfo {

        private String username;

        private String password;

        private Integer rememberMe;
    }

    @Getter
    @Setter
    private static class Result {

        private String code;

        private ResultData data;
    }

    @Getter
    @Setter
    private static class ResultData {

        private String data;

        private String[] roles;
    }
}