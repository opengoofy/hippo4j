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

package cn.hippo4j.config.verify;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.common.constant.ConfigModifyTypeConstants;
import cn.hippo4j.config.service.biz.ConfigModifyVerifyService;
import com.google.common.collect.Maps;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * config change verify service choose
 */
@Component
public class ConfigModifyVerifyServiceChoose implements CommandLineRunner {

    /**
     * Storage config change verify service container.
     */
    private Map<Integer, ConfigModifyVerifyService> configChangeVerifyServiceChooseMap = Maps.newHashMap();

    /**
     * Choose by type.
     *
     * @param type {@link ConfigModifyTypeConstants}
     * @return
     */
    public ConfigModifyVerifyService choose(Integer type) {
        ConfigModifyVerifyService verifyService = configChangeVerifyServiceChooseMap.get(type);
        return verifyService;
    }

    @Override
    public void run(String... args) throws Exception {
        Map<String, ConfigModifyVerifyService> configChangeVerifyServiceMap =
                ApplicationContextHolder.getBeansOfType(ConfigModifyVerifyService.class);
        configChangeVerifyServiceMap.values().forEach(each -> configChangeVerifyServiceChooseMap.put(each.type(), each));
    }
}
