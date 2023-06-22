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

package cn.hippo4j.monitor.elasticsearch;

import cn.hippo4j.common.model.ThreadPoolRunStateInfo;
import cn.hippo4j.core.executor.state.ThreadPoolRunStateHandler;
import cn.hippo4j.monitor.base.AbstractDynamicThreadPoolMonitor;
import cn.hippo4j.threadpool.monitor.support.MonitorTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * Web thread-pool elastic-search monitor handler.
 */
@Slf4j
public class WebThreadPoolElasticSearchMonitorHandler extends AbstractDynamicThreadPoolMonitor {

    public WebThreadPoolElasticSearchMonitorHandler(ThreadPoolRunStateHandler handler) {
        super(handler);
    }

    @Override
    protected void execute(ThreadPoolRunStateInfo poolRunStateInfo) {
        // TODO
    }

    @Override
    public String getType() {
        return MonitorTypeEnum.ELASTICSEARCH.name().toLowerCase();
    }
}
