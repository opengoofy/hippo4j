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

package cn.hippo4j.config.springboot.starter.refresher.event;

import cn.hippo4j.adapter.web.WebThreadPoolHandlerChoose;
import cn.hippo4j.adapter.web.WebThreadPoolService;
import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.model.WebIpAndPortInfo;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;

/**
 * Refresh listener abstract base class.
 */
@Slf4j
public abstract class AbstractRefreshListener<M> implements RefreshListener<Hippo4jConfigDynamicRefreshEvent, M> {

    protected static final String ALL = "*";

    protected static final String SEPARATOR = ",";

    /**
     * Application ip and  application post
     */
    protected static volatile WebIpAndPortInfo webIpAndPort;

    protected void initIpAndPort() {
        if (webIpAndPort == null) {
            synchronized (AbstractRefreshListener.class) {
                if (webIpAndPort == null) {
                    webIpAndPort = getWebIpAndPortInfo();
                }
            }
        }
    }

    private WebIpAndPortInfo getWebIpAndPortInfo() {
        InetUtils inetUtils = ApplicationContextHolder.getBean(InetUtils.class);
        InetUtils.HostInfo loopBackHostInfo = inetUtils.findFirstNonLoopBackHostInfo();
        Assert.notNull(loopBackHostInfo, "Unable to get the application IP address");
        String ip = loopBackHostInfo.getIpAddress();
        WebThreadPoolHandlerChoose webThreadPoolHandlerChoose = ApplicationContextHolder.getBean(WebThreadPoolHandlerChoose.class);
        WebThreadPoolService webThreadPoolService = webThreadPoolHandlerChoose.choose();
        // When get the port at startup, can get the message: "port xxx was already in use" or use two ports
        String port = String.valueOf(webThreadPoolService.getWebServer().getPort());
        return new WebIpAndPortInfo(ip, port);
    }

    /**
     * Matching nodes<br>
     * nodes is ip + port.Get 'nodes' in the new Properties,Compare this with the ip + port of Application.<br>
     * support prefix pattern matching. e.g: <br>
     * <ul>
     *     <li>192.168.1.5:* -- Matches all ports of 192.168.1.5</li>
     *     <li>192.168.1.*:2009 -- Matches 2009 port of 192.168.1.*</li>
     *     <li>* -- all</li>
     *     <li>empty -- all</li>
     * </ul>
     * The format of ip + port is ip : port.
     *
     * @param properties new Properties
     */
    @Override
    public boolean match(M properties) {
        if (webIpAndPort == null) {
            initIpAndPort();
        }
        String nodes = getNodes(properties);
        if (StringUtil.isEmpty(nodes) || ALL.equals(nodes)) {
            return true;
        }
        String[] splitNodes = nodes.split(SEPARATOR);
        return Arrays.stream(splitNodes)
                .distinct()
                .map(WebIpAndPortInfo::build)
                .filter(Objects::nonNull)
                .anyMatch(each -> each.check(webIpAndPort.getIpSegment(), webIpAndPort.getPort()));
    }

    /**
     * Get nodes in new properties.
     *
     * @param properties new properties
     * @return nodes in properties
     */
    protected String getNodes(M properties) {
        return ALL;
    }
}