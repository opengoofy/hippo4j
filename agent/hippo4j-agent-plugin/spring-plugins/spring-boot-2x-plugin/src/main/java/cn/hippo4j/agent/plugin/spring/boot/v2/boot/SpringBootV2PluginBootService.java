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

package cn.hippo4j.agent.plugin.spring.boot.v2.boot;

import cn.hippo4j.agent.core.boot.BootService;
import cn.hippo4j.agent.core.boot.DefaultImplementor;
import cn.hippo4j.agent.core.logging.api.ILog;
import cn.hippo4j.agent.core.logging.api.LogManager;

/**
 * SpringBoot v1 plugin boot service
 */
@DefaultImplementor
public class SpringBootV2PluginBootService implements BootService {

    private static final ILog LOGGER = LogManager.getLogger(SpringBootV2PluginBootService.class);

    @Override
    public void prepare() throws Throwable {

    }

    @Override
    public void boot() throws Throwable {
        LOGGER.info("Loader SpringBootV2PluginBootService...");
    }

    @Override
    public void onComplete() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {

    }
}
