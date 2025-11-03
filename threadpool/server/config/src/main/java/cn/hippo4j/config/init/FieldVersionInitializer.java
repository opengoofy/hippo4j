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

package cn.hippo4j.config.init;

import cn.hippo4j.common.toolkit.FieldVersionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Initialize field version registry at server startup.
 * Pre-registers known fields with their introduction versions.
 */
@Slf4j
@Component
@Order(Integer.MIN_VALUE)
public class FieldVersionInitializer implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing field version registry...");

        // Register fields introduced in version 2.0.0
        FieldVersionRegistry.registerField("executeTimeOut", "2.0.0");
        FieldVersionRegistry.registerField("isAlarm", "2.0.0");
        FieldVersionRegistry.registerField("capacityAlarm", "2.0.0");
        FieldVersionRegistry.registerField("livenessAlarm", "2.0.0");
        FieldVersionRegistry.registerField("allowCoreThreadTimeOut", "2.0.0");

        // Register fields for future versions here:
        // FieldVersionRegistry.registerField("newField", "2.1.0");

        log.info("Field version registry initialized with {} fields",
                FieldVersionRegistry.getAllFieldVersions().size());
    }
}
