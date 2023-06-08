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

package cn.hippo4j.springboot.starter.notify;

import cn.hippo4j.common.executor.ThreadPoolExecutorRegistry;
import cn.hippo4j.common.model.Result;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.GroupKey;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.springboot.starter.config.BootstrapProperties;
import cn.hippo4j.springboot.starter.remote.HttpAgent;
import cn.hippo4j.threadpool.message.api.NotifyConfigBuilder;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.api.ThreadPoolNotifyDTO;
import cn.hippo4j.threadpool.message.core.request.ThreadPoolNotifyRequest;
import cn.hippo4j.threadpool.message.core.service.AlarmControlHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static cn.hippo4j.common.constant.Constants.BASE_PATH;

/**
 * Server mode notify config builder.
 */
@Slf4j
@AllArgsConstructor
public class ServerModeNotifyConfigBuilder implements NotifyConfigBuilder {

    private final HttpAgent httpAgent;

    private final BootstrapProperties properties;

    private final AlarmControlHandler alarmControlHandler;

    @Override
    public Map<String, List<NotifyConfigDTO>> buildNotify() {
        List<String> threadPoolIds = ThreadPoolExecutorRegistry.listThreadPoolExecutorId();
        if (CollectionUtil.isEmpty(threadPoolIds)) {
            log.warn("The client does not have a dynamic thread pool instance configured.");
            return new HashMap<>();
        }
        return getAndInitNotify(threadPoolIds);
    }

    public Map<String, List<NotifyConfigDTO>> getAndInitNotify(List<String> threadPoolIds) {
        Map<String, List<NotifyConfigDTO>> resultMap = new HashMap<>();
        List<String> groupKeys = new ArrayList<>();
        threadPoolIds.forEach(each -> {
            String groupKey = GroupKey.getKeyTenant(each, properties.getItemId(), properties.getNamespace());
            groupKeys.add(groupKey);
        });
        Result result = null;
        try {
            result = httpAgent.httpPostByDiscovery(BASE_PATH + "/notify/list/config", new ThreadPoolNotifyRequest(groupKeys));
        } catch (Throwable ex) {
            log.error("Get dynamic thread pool notify configuration error. message: {}", ex.getMessage());
        }
        if (result != null && result.isSuccess() && result.getData() != null) {
            String resultDataStr = JSONUtil.toJSONString(result.getData());
            List<ThreadPoolNotifyDTO> resultData = JSONUtil.parseArray(resultDataStr, ThreadPoolNotifyDTO.class);
            resultData.forEach(each -> resultMap.put(each.getNotifyKey(), each.getNotifyList()));
            resultMap.forEach((key, val) -> val.stream().filter(each -> Objects.equals("ALARM", each.getType()))
                    .forEach(each -> alarmControlHandler.initCacheAndLock(each.getTpId(), each.getPlatform(), each.getInterval())));
        }
        return resultMap;
    }
}
