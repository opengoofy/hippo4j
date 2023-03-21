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

package cn.hippo4j.message.service;

import cn.hippo4j.common.api.ThreadPoolConfigChange;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.core.toolkit.IdentifyUtil;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hippo4j.message.request.WebChangeParameterNotifyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * Web thread-pool config change handler.
 */
@RequiredArgsConstructor
@Slf4j
public class WebThreadPoolConfigChangeHandler implements ThreadPoolConfigChange<WebChangeParameterNotifyRequest> {

    @Value("${spring.profiles.active:UNKNOWN}")
    private String active;

    @Value("${spring.dynamic.thread-pool.item-id:}")
    private String itemId;

    @Value("${spring.application.name:UNKNOWN}")
    private String applicationName;

    private final Hippo4jSendMessageService hippo4jSendMessageService;

    /**
     * Send pool config change message for web.
     *
     * @param requestParam change parameter notify request
     */
    @Override
    public void sendPoolConfigChange(WebChangeParameterNotifyRequest requestParam) {
        try {
            requestParam.setActive(active.toUpperCase());
            String appName = StringUtil.isBlank(itemId) ? applicationName : itemId;
            requestParam.setAppName(appName);
            requestParam.setIdentify(IdentifyUtil.getIdentify());
            hippo4jSendMessageService.sendChangeMessage(requestParam);
        } catch (Throwable th) {
            log.error("send web thread pool config change message failed.", th);
        }
    }
}
