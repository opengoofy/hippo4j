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

package cn.hippo4j.springboot.starter.remote;

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Server list manager.
 */
@Slf4j
public class ServerListManager {

    private static final String HTTPS = "https://";

    private static final String HTTP = "http://";

    private String serverAddrsStr;

    private String nettyServerPort;

    @Getter
    volatile List<String> serverUrls = new ArrayList();

    private volatile String currentServerAddr;

    private Iterator<String> iterator;

    private final BootstrapProperties properties;

    public ServerListManager(BootstrapProperties dynamicThreadPoolProperties) {
        this.properties = dynamicThreadPoolProperties;
        serverAddrsStr = properties.getServerAddr();
        nettyServerPort = properties.getNettyServerPort();
        if (!StringUtils.isEmpty(serverAddrsStr)) {
            List<String> serverAddrList = new ArrayList();
            String[] serverAddrListArr = this.serverAddrsStr.split(",");
            for (String serverAddr : serverAddrListArr) {
                boolean whetherJoint = StringUtil.isNotBlank(serverAddr)
                        && !serverAddr.startsWith(HTTPS) && !serverAddr.startsWith(HTTP);
                if (whetherJoint) {
                    serverAddr = HTTP + serverAddr;
                }
                currentServerAddr = serverAddr;
                serverAddrList.add(serverAddr);
            }
            this.serverUrls = serverAddrList;
        }
    }

    public String getCurrentServerAddr() {
        if (StringUtils.isEmpty(currentServerAddr)) {
            iterator = iterator();
            currentServerAddr = iterator.next();
        }
        return currentServerAddr;
    }

    public String getNettyServerPort() {
        return nettyServerPort;
    }

    Iterator<String> iterator() {
        return new ServerAddressIterator(serverUrls);
    }

    /**
     * Server Address Iterator
     */
    private static class ServerAddressIterator implements Iterator<String> {

        final List<RandomizedServerAddress> sorted;

        final Iterator<RandomizedServerAddress> iter;

        ServerAddressIterator(List<String> source) {
            sorted = new ArrayList();
            for (String address : source) {
                sorted.add(new RandomizedServerAddress(address));
            }
            Collections.sort(sorted);
            iter = sorted.iterator();
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public String next() {
            return null;
        }

        /**
         * Randomized Server Address
         */
        static class RandomizedServerAddress implements Comparable<RandomizedServerAddress> {

            static Random random = new Random();

            String serverIp;

            int priority = 0;

            int seed;

            RandomizedServerAddress(String ip) {
                try {
                    this.serverIp = ip;
                    /*
                     * change random scope from 32 to Integer.MAX_VALUE to fix load balance issue
                     */
                    this.seed = random.nextInt(Integer.MAX_VALUE);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public int compareTo(RandomizedServerAddress other) {
                if (this.priority != other.priority) {
                    return other.priority - this.priority;
                } else {
                    return other.seed - this.seed;
                }
            }
        }
    }
}
