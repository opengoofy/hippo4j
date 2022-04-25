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

package cn.hippo4j.common.notify;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Thread pool notify alarm.
 *
 * @author chen.ma
 * @date 2021/8/15 19:13
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ThreadPoolNotifyAlarm {

    /**
     * isAlarm
     */
    @NonNull
    private Boolean isAlarm;

    /**
     * activeAlarm
     */
    @NonNull
    private Integer activeAlarm;

    /**
     * capacityAlarm
     */
    @NonNull
    private Integer capacityAlarm;

    /**
     * interval
     */
    private Integer interval;

    /**
     * receive
     */
    private String receive;

    /**
     * receives
     * ps：暂不启用该配置，后续如果开发邮箱时或许有用
     */
    @Deprecated
    private Map<String, String> receives;

}
