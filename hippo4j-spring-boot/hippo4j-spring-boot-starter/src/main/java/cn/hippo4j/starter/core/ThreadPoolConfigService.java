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

package cn.hippo4j.starter.core;

import cn.hippo4j.starter.event.ApplicationCompleteEvent;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hippo4j.starter.remote.ServerHealthCheck;
import org.springframework.context.ApplicationListener;

import java.util.Arrays;

/**
 * Thread-pool config service.
 *
 * @author chen.ma
 * @date 2021/6/21 21:50
 */
public class ThreadPoolConfigService implements ConfigService, ApplicationListener<ApplicationCompleteEvent> {

    private final ClientWorker clientWorker;

    private final ServerHealthCheck serverHealthCheck;

    public ThreadPoolConfigService(HttpAgent httpAgent, String identification, ServerHealthCheck serverHealthCheck) {
        this.serverHealthCheck = serverHealthCheck;
        this.clientWorker = new ClientWorker(httpAgent, identification, serverHealthCheck);
    }

    @Override
    public void addListener(String tenantId, String itemId, String tpId, Listener listener) {
        clientWorker.addTenantListeners(tenantId, itemId, tpId, Arrays.asList(listener));
    }

    @Override
    public String getServerStatus() {
        if (serverHealthCheck.isHealthStatus()) {
            return "UP";
        } else {
            return "DOWN";
        }
    }

    @Override
    public void onApplicationEvent(ApplicationCompleteEvent event) {
        clientWorker.notifyApplicationComplete();
    }
}
