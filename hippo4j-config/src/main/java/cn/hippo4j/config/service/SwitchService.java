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

package cn.hippo4j.config.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Switch service.
 */
@Slf4j
@Service
public class SwitchService {

    public static final String FIXED_DELAY_TIME = "fixedDelayTime";

    public static final String FIXED_POLLING = "isFixedPolling";

    public static final String FIXED_POLLING_INTERVAL = "fixedPollingInertval";

    private static volatile Map<String, String> switches = new HashMap(16);

    public static int getSwitchInteger(String key, int defaultValue) {
        int rtn = defaultValue;
        try {
            String status = switches.get(key);
            rtn = status != null ? Integer.parseInt(status) : defaultValue;
        } catch (Exception e) {
            rtn = defaultValue;
            log.error("corrupt switch value {}, {}", key, switches.get(key));
        }
        return rtn;
    }

    public static boolean getSwitchBoolean(String key, boolean defaultValue) {
        boolean rtn = defaultValue;
        try {
            String value = switches.get(key);
            rtn = value != null ? Boolean.parseBoolean(value) : defaultValue;
        } catch (Exception e) {
            rtn = defaultValue;
            log.error("corrupt switch value {}, {}", key, switches.get(key));
        }
        return rtn;
    }
}
