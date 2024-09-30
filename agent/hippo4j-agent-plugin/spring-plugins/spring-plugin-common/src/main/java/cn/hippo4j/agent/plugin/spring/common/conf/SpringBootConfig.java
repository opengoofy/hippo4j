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

package cn.hippo4j.agent.plugin.spring.common.conf;

import cn.hippo4j.agent.core.boot.SpringBootConfigNode;

import java.util.Arrays;
import java.util.List;

/**
 * Spring boot config
 */
public class SpringBootConfig {

    /**
     *  Spring
     */
    @SpringBootConfigNode(root = SpringBootConfig.class)
    public static class Spring {

        /**
         * Dynamic
         */
        @SpringBootConfigNode(root = SpringBootConfig.class)
        public static class Dynamic {

            /**
             * ThreadPool
             */
            @SpringBootConfigNode(root = SpringBootConfig.class)
            public static class Thread_Pool {

                /**
                 * Apollo
                 */
                @SpringBootConfigNode(root = SpringBootConfig.class)
                public static class Apollo {

                    public static List<String> NAMESPACE = Arrays.asList("application");
                }

                @SpringBootConfigNode(root = SpringBootConfig.class)
                public static class Nacos {

                    public static String SERVER_ADDR = "localhost";

                    public static List<String> NAMESPACE = Arrays.asList("");

                    public static String DATA_ID = "";

                    public static String GROUP = "DEFAULT_GROUP";
                }

                /**
                 * Monitor
                 */
                @SpringBootConfigNode(root = SpringBootConfig.class)
                public static class Monitor {

                    public static Boolean enable = Boolean.TRUE;

                    public static String collectTypes = "micrometer";

                    public static String threadPoolTypes = "dynamic";

                    public static Long initialDelay = 10000L;

                    public static Long collectInterval = 5000L;

                    public static Integer AGENT_MICROMETER_PORT;

                }

                public static String CONFIG_FILE_TYPE;
            }
        }

        @SpringBootConfigNode(root = SpringBootConfig.class)
        public static class Application {

            public static String name = "";

        }

        @SpringBootConfigNode(root = SpringBootConfig.class)
        public static class Profiles {

            public static String active = "";

        }
    }
}
