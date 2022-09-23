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

package cn.hippo4j.common.api;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * when init thread-pool dynamic refresh.
 */
public interface ThreadPoolInitRefresh extends ApplicationRunner {

    /**
     * Initializes the thread pool after system startup
     *
     * @param context new properties
     */
    void initRefresh(String context);

    /**
     * get from the Configuration center
     *
     * @return new properties
     * @throws Exception exception
     */
    String getProperties() throws Exception;

    @Override
    default void run(ApplicationArguments args) throws Exception {
        String properties = getProperties();
        if (properties == null) {
            return;
        }
        initRefresh(properties);
    }
}
