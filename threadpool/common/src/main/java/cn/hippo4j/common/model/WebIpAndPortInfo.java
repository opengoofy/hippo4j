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

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Web ip and port info
 */
@Data
@Slf4j
public class WebIpAndPortInfo {

    protected static final String ALL = "*";

    protected static final String SPOT = "\\.";

    protected static final String COLON = ":";

    private String ip;

    private String port;

    private String[] ipSegment;

    public WebIpAndPortInfo(String ip, String port) {
        this.ip = ip;
        this.port = port;
        this.ipSegment = ip.split(SPOT);
    }

    public static WebIpAndPortInfo build(String node) {
        if (ALL.equals(node)) {
            return new WebIpAndPortInfo(ALL, ALL);
        }
        String[] ipPort = node.split(COLON);
        if (ipPort.length != 2) {
            log.error("The IP address format is error : {}", node);
            return null;
        }
        return new WebIpAndPortInfo(ipPort[0], ipPort[1]);
    }

    /**
     * Check.
     *
     * @param appIpSegment application ip segment
     * @param port         application port
     */
    public boolean check(String[] appIpSegment, String port) {
        return checkPort(port) && checkIp(appIpSegment);
    }

    /**
     * Check ip.
     *
     * @param appIpSegment application ip segment
     */
    protected boolean checkIp(String[] appIpSegment) {
        if (ALL.equals(this.ip)) {
            return true;
        }
        boolean flag = true;
        for (int i = 0; i < ipSegment.length && flag; i++) {
            String propIp = ipSegment[i];
            String appIp = appIpSegment[i];
            flag = contrastSegment(appIp, propIp);
        }
        return flag;
    }

    /**
     * Check port.
     *
     * @param port application port
     */
    protected boolean checkPort(String port) {
        return contrastSegment(port, this.port);
    }

    /**
     * Check whether the strings are the same.
     *
     * @param appIp  appIp
     * @param propIp propIp
     */
    protected boolean contrastSegment(String appIp, String propIp) {
        return ALL.equals(propIp) || appIp.equals(propIp);
    }
}
