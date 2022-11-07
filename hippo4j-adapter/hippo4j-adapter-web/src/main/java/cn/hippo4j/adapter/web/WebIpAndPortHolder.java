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

package cn.hippo4j.adapter.web;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.WebIpAndPortInfo;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import lombok.NoArgsConstructor;
import org.springframework.boot.web.server.WebServer;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Ip and port holder.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class WebIpAndPortHolder {

    private static boolean supportVersion = false;

    static {
        try {
            Class.forName("org.springframework.boot.web.server.WebServer");
            supportVersion = true;
        } catch (Exception ignored) {
        }
    }

    /**
     * Application ip and  application post
     */
    protected static AtomicReference<WebIpAndPortInfo> webIpAndPort = new AtomicReference<>();

    public static final String ALL = "*";

    protected static final String SEPARATOR = ",";

    protected static void initIpAndPort() {
        if (!supportVersion) {
            return;
        }
        webIpAndPort.compareAndSet(null, getWebIpAndPortInfo());
    }

    private static WebIpAndPortInfo getWebIpAndPortInfo() {
        InetUtils inetUtils = ApplicationContextHolder.getBean(InetUtils.class);
        InetUtils.HostInfo loopBackHostInfo = inetUtils.findFirstNonLoopBackHostInfo();
        Assert.notNull(loopBackHostInfo, "Unable to get the application IP address");
        String ip = loopBackHostInfo.getIpAddress();
        WebThreadPoolHandlerChoose webThreadPoolHandlerChoose = ApplicationContextHolder.getBean(WebThreadPoolHandlerChoose.class);
        WebThreadPoolService webThreadPoolService = webThreadPoolHandlerChoose.choose();
        // When get the port at startup, can get the message: "port xxx was already in use" or use two ports
        WebServer webServer = webThreadPoolService.getWebServer();
        String port = String.valueOf(webServer.getPort());
        return new WebIpAndPortInfo(ip, port);
    }

    /**
     * get WebIpAndPortInfo, If it is null, initialize it.
     *
     * @return Web ip and port info
     */
    public static WebIpAndPortInfo getWebIpAndPort() {
        if (webIpAndPort.get() == null) {
            initIpAndPort();
        }
        return WebIpAndPortHolder.webIpAndPort.get();
    }

    /**
     * Check the new properties and instance IP and port.
     *
     * @param nodes nodes in properties
     * @return Whether it meets the conditions
     */
    public static boolean check(String nodes) {
        WebIpAndPortInfo webIpAndPort = WebIpAndPortHolder.getWebIpAndPort();
        if (StringUtil.isEmpty(nodes) || ALL.equals(nodes) || webIpAndPort == null) {
            return true;
        }
        String[] splitNodes = nodes.split(SEPARATOR);
        return Arrays.stream(splitNodes)
                .distinct()
                .map(WebIpAndPortInfo::build)
                .filter(Objects::nonNull)
                .anyMatch(each -> each.check(webIpAndPort.getIpSegment(), webIpAndPort.getPort()));
    }
}
