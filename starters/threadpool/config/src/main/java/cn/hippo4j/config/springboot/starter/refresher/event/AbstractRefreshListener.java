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

import cn.hippo4j.adapter.web.WebIpAndPortHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * Refresh listener abstract base class.
 */
@Slf4j
public abstract class AbstractRefreshListener<M> implements RefreshListener<ThreadPoolConfigDynamicRefreshEvent, M> {

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
        return WebIpAndPortHolder.check(nodes);
    }

    /**
     * Get nodes in new properties.
     *
     * @param properties new properties
     * @return nodes in properties
     */
    protected String getNodes(M properties) {
        return WebIpAndPortHolder.ALL;
    }
}
