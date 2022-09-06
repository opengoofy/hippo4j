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

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.toolkit.inet.InetUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.Objects;

/**
 * Refresh listener abstract base class.
 */
@Slf4j
public abstract class AbstractRefreshListener<M> implements RefreshListener<Hippo4jConfigDynamicRefreshEvent, M> {

    protected static final String ALL = "*";

    protected static final String SPOT = "\\.";

    protected static final String SEPARATOR = ",";

    protected static final String COLON = ":";

    /**
     * application ip
     */
    protected final String[] ipSegment;

    /**
     * application post
     */
    protected String port;

    AbstractRefreshListener() {
        InetUtils inetUtils = ApplicationContextHolder.getBean(InetUtils.class);
        InetUtils.HostInfo loopBackHostInfo = inetUtils.findFirstNonLoopBackHostInfo();
        Assert.notNull(loopBackHostInfo, "Unable to get the application IP address");
        ipSegment = loopBackHostInfo.getIpAddress().split(SPOT);
    }

    @EventListener(WebServerInitializedEvent.class)
    public void webServerInitializedListener(WebServerInitializedEvent event) {
        port = String.valueOf(event.getWebServer().getPort());
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
        String nodes = getNodes(properties);
        if (StringUtil.isEmpty(nodes) || ALL.equals(nodes)) {
            return true;
        }
        String[] splitNodes = nodes.split(SEPARATOR);
        return Arrays.stream(splitNodes)
                .distinct()
                .map(IpAndPort::build)
                .filter(Objects::nonNull)
                .anyMatch(i -> i.check(ipSegment, port));
    }

    /**
     * get nodes in new properties
     *
     * @param properties new properties
     * @return nodes in properties
     */
    protected String getNodes(M properties) {
        return ALL;
    }

    /**
     * ip + port
     */
    @Data
    protected static class IpAndPort {

        private String ip;
        private String port;
        private String[] propIpSegment;

        private IpAndPort(String ip, String port) {
            this.ip = ip;
            this.port = port;
            this.propIpSegment = ip.split(SPOT);
        }

        public static IpAndPort build(String node) {
            if (ALL.equals(node)) {
                return new IpAndPort(ALL, ALL);
            }
            String[] ipPort = node.split(COLON);
            if (ipPort.length != 2) {
                log.error("The IP address format is error : {}", node);
                return null;
            }
            return new IpAndPort(ipPort[0], ipPort[1]);
        }

        /**
         * check
         *
         * @param appIpSegment application ip segment
         * @param port         application port
         */
        public boolean check(String[] appIpSegment, String port) {
            return checkPort(port) && checkIp(appIpSegment);
        }

        /**
         * check ip
         *
         * @param appIpSegment application ip segment
         */
        protected boolean checkIp(String[] appIpSegment) {
            if (ALL.equals(this.ip)) {
                return true;
            }
            boolean flag = true;
            for (int i = 0; i < propIpSegment.length && flag; i++) {
                String propIp = propIpSegment[i];
                String appIp = appIpSegment[i];
                flag = contrastSegment(appIp, propIp);
            }
            return flag;
        }

        /**
         * check port
         *
         * @param port application port
         */
        protected boolean checkPort(String port) {
            return contrastSegment(port, this.port);
        }

        /**
         * Check whether the strings are the same
         *
         * @param appIp  appIp
         * @param propIp propIp
         */
        protected boolean contrastSegment(String appIp, String propIp) {
            return ALL.equals(propIp) || appIp.equals(propIp);
        }
    }
}