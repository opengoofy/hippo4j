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
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpUtilsTest {

    static int PORT = 8080;
    static Tomcat tomcat;
    static final String PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
    static final String HOME_PAGE_URL = "/home";
    static final String HOME_PAGE_NAME = "homeServlet";
    static final String LOGIN_URL = "/login";
    static final String LOGIN_NAME = "loginServlet";
    static final String CONTEXT_PATH = "/";
    static final String USER_DIR = "user.dir";
    static final String BASE_DIR = System.getProperty(USER_DIR) + "/target/tomcat";

    @BeforeClass
    public static void startWeb() throws IOException, LifecycleException {
        tomcat = new Tomcat();
        // clear historical files that may be left behind
        deleteBaseDir();
        // set base dir
        tomcat.setBaseDir(BASE_DIR);
        // get a random port
        ServerSocket socket = new ServerSocket(0);
        PORT = socket.getLocalPort();
        socket.close();
        tomcat.setPort(PORT);
        // set a connector
        Connector connector = new Connector(PROTOCOL);
        connector.setThrowOnFailure(true);
        connector.setPort(PORT);
        tomcat.setConnector(connector);
        // set a context
        Context context = tomcat.addContext(CONTEXT_PATH, BASE_DIR);
        Tomcat.addServlet(context, HOME_PAGE_NAME, new HomeServlet()).setAsyncSupported(true);
        context.addServletMappingDecoded(HOME_PAGE_URL, HOME_PAGE_NAME);
        Tomcat.addServlet(context, LOGIN_NAME, new LoginServlet()).setAsyncSupported(true);
        context.addServletMappingDecoded(LOGIN_URL, LOGIN_NAME);
        // start tomcat
        tomcat.start();
    }

    @AfterClass
    public static void stopWeb() throws LifecycleException, IOException {
        // stop tomcat
        tomcat.stop();
        // del dir
        deleteBaseDir();
    }

    /**
     * forcibly delete the tomcat's base dir and its sub files
     */
    private static void deleteBaseDir() throws IOException {
        File file = new File(BASE_DIR);
        // fail fast
        if (!file.exists()) {
            return;
        }
        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * test url
     */
    String url = "http://localhost:";

    String passwordValue = "hippo4jtest";
    String usernameValue = "hippo4j";
    String password = "password";
    String username = "username";
    String suffix = "?password=hippo4jtest&username=hippo4j";

    @Test
    public void get() {
        String s = HttpUtil.get(url + PORT + HOME_PAGE_URL);
        Assert.assertNotNull(s);
    }

    @Test
    public void restApiPost() {
        String loginUrl = url + PORT + LOGIN_URL;
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setPassword(passwordValue);
        loginInfo.setUsername(usernameValue);
        loginInfo.setRememberMe(1);
        String s = HttpUtil.post(loginUrl, loginInfo);
        Result result = JSONUtil.parseObject(s, Result.class);
        Assert.assertNotNull(result);
        String data = result.getData().getData();
        Assert.assertNotNull(data);
    }

    @Test
    public void testRestApiPost() {
        String loginUrl = url + PORT + LOGIN_URL;
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setPassword(passwordValue);
        loginInfo.setUsername(usernameValue);
        loginInfo.setRememberMe(1);
        Result result = HttpUtil.post(loginUrl, loginInfo, Result.class);
        Assert.assertNotNull(result);
        String data = result.getData().getData();
        Assert.assertNotNull(data);
    }

    // @Test(expected = SocketTimeoutException.class)
    public void testRestApiPostTimeout() {
        String loginUrl = url + PORT + LOGIN_URL;
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setPassword(passwordValue);
        loginInfo.setUsername(usernameValue);
        loginInfo.setRememberMe(1);
        HttpUtil.post(loginUrl, loginInfo, 1, Result.class);
    }

    @Test
    public void buildUrl() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(password, passwordValue);
        map.put(username, usernameValue);
        String s = HttpUtil.buildUrl(url + PORT, map);
        Assert.assertEquals(url + PORT + suffix, s);
    }

    @Getter
    @Setter
    protected static class LoginInfo {

        private String username;

        private String password;

        private Integer rememberMe;
    }

    @Getter
    @Setter
    protected static class Result {

        private String code;

        private ResultData data;
    }

    @Getter
    @Setter
    protected static class ResultData {

        private String data;

        private String[] roles;
    }
}