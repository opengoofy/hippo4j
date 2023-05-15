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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * his HttpServlet represents the login request
 */
public class LoginServlet extends HttpServlet {

    String passwordAttr = "password";
    String usernameAttr = "username";
    String status = "200";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String password = (String) req.getAttribute(passwordAttr);
        String username = (String) req.getAttribute(usernameAttr);
        HttpUtilsTest.ResultData resultData = new HttpUtilsTest.ResultData();
        resultData.setData(username + password);
        HttpUtilsTest.Result result = new HttpUtilsTest.Result();
        result.setCode(status);
        result.setData(resultData);
        String s = JSONUtil.toJSONString(result);
        PrintWriter writer = resp.getWriter();
        writer.println(s);
    }
}
