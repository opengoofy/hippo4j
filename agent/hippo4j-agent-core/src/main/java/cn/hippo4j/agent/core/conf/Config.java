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

package cn.hippo4j.agent.core.conf;

import cn.hippo4j.agent.core.logging.core.LogLevel;
import cn.hippo4j.agent.core.logging.core.LogOutput;
import cn.hippo4j.agent.core.logging.core.ResolverType;
import cn.hippo4j.agent.core.logging.core.WriterFactory;
import cn.hippo4j.agent.core.plugin.bytebuddy.ClassCacheMode;
import cn.hippo4j.agent.core.util.Length;

import java.util.Arrays;
import java.util.List;

/**
 * This is the core config in hippo4j agent.
 */
public class Config {

    public static class Agent {

        /**
         * Namespace represents a subnet, such as kubernetes namespace, or 172.10.*.*.
         *
         * @since 8.10.0 namespace would be added as {@link #SERVICE_NAME} suffix.
         *
         * Removed namespace isolating headers in cross process propagation. The HEADER name was
         * `HeaderName:Namespace`.
         */
        @Length(20)
        public static String NAMESPACE = "";

        /**
         * Service name is showed on the UI. Suggestion: set a unique name for each service, service instance nodes
         * share the same code
         *
         * @since 8.10.0 ${service name} = [${group name}::]${logic name}|${NAMESPACE}|${CLUSTER}
         *
         * The group name, namespace and cluster are optional. Once they are all blank, service name would be the final
         * name.
         */
        @Length(50)
        public static String SERVICE_NAME = "";

        /**
         * Cluster defines the physical cluster in a data center or same network segment. In one cluster, IP address
         * should be unique identify.
         *
         * The cluster name would be
         *
         * 1. Add as {@link #SERVICE_NAME} suffix.
         *
         * 2. Add as exit span's peer, ${CLUSTER} / original peer
         *
         * 3. Cross Process Propagation Header's value addressUsedAtClient[index=8] (Target address of this request used
         * on the client end).
         *
         * @since 8.10.0
         */
        @Length(20)
        public static String CLUSTER = "";

        /**
         * If true, Hippo4j agent will save all instrumented classes files in `/debugging` folder. Hippo4j team
         * may ask for these files in order to resolve compatible problem.
         */
        public static boolean IS_OPEN_DEBUGGING_CLASS = false;

        /**
         * If true, Hippo4j agent will cache all instrumented classes to memory or disk files (decided by class cache
         * mode), allow other javaagent to enhance those classes that enhanced by Hippo4j agent.
         */
        public static boolean IS_CACHE_ENHANCED_CLASS = false;

        /**
         * The instrumented classes cache mode: MEMORY or FILE MEMORY: cache class bytes to memory, if instrumented
         * classes is too many or too large, it may take up more memory FILE: cache class bytes in `/class-cache`
         * folder, automatically clean up cached class files when the application exits
         */
        public static ClassCacheMode CLASS_CACHE_MODE = ClassCacheMode.MEMORY;
    }

    public static class Logging {

        /**
         * Log file name.
         */
        public static String FILE_NAME = "hippo4j-api.log";

        /**
         * Log files directory. Default is blank string, means, use "{theHippo4jAgentJarDir}/logs  " to output logs.
         * {theHippo4jAgentJarDir} is the directory where the hippo4j agent jar file is located.
         * <p>
         * Ref to {@link WriterFactory#getLogWriter()}
         */
        public static String DIR = "";

        /**
         * The max size of log file. If the size is bigger than this, archive the current file, and write into a new
         * file.
         */
        public static int MAX_FILE_SIZE = 300 * 1024 * 1024;

        /**
         * The max history log files. When rollover happened, if log files exceed this number, then the oldest file will
         * be delete. Negative or zero means off, by default.
         */
        public static int MAX_HISTORY_FILES = -1;

        /**
         * The log level. Default is debug.
         */
        public static LogLevel LEVEL = LogLevel.DEBUG;

        /**
         * The log output. Default is FILE.
         */
        public static LogOutput OUTPUT = LogOutput.FILE;

        /**
         * The log resolver type. Default is PATTERN which will create PatternLogResolver later.
         */
        public static ResolverType RESOLVER = ResolverType.PATTERN;

        /**
         * The log patten. Default is "%level %timestamp %thread %class : %msg %throwable". Each conversion specifiers
         * starts with a percent sign '%' and fis followed by conversion word. There are some default conversion
         * specifiers: %thread = ThreadName %level = LogLevel  {@link LogLevel} %timestamp = The now() who format is
         * 'yyyy-MM-dd HH:mm:ss:SSS' %class = SimpleName of TargetClass %msg = Message of user input %throwable =
         * Throwable of user input %agent_name = ServiceName of Agent {@link Agent#SERVICE_NAME}
         *
         * @see cn.hippo4j.agent.core.logging.core.PatternLogger#DEFAULT_CONVERTER_MAP
         */
        public static String PATTERN = "%level %timestamp %thread %class : %msg %throwable";
    }

    public static class Plugin {

        /**
         * Control the length of the peer field.
         */
        public static int PEER_MAX_LENGTH = 200;

        /**
         * Exclude activated plugins
         */
        public static String EXCLUDE_PLUGINS = "";

        /**
         * Mount the folders of the plugins. The folder path is relative to agent.jar.
         */
        public static List<String> MOUNT = Arrays.asList("plugins", "activations");

        public static class ThreadPool {

            public static List<String> EXCLUDE_PACKAGE_PREFIX = Arrays.asList(
                    "java", "sun", "okhttp3", "retrofit2", "reactor",
                    "org.apache", "io.netty", "org.springframework", "com.ctrip", "com.google",
                    "io.undertow", "org.xnio", "org.jboss", "com.zaxxer", "org.redisson", "com.alibaba",
                    "com.netflix", "com.mysql", "rx.internal", "io.shardingjdbc", "org.drools", "org.elasticsearch",
                    "ch.qos.logback", "net.sf.ehcache");
        }

        public static class Apollo {

            public static class App {

                public static String ID;
            }
            public static String META;

            public static class BootStrap {

                public static boolean ENABLED = false;

                public static List<String> NAMESPACES;
            }
        }
    }
}
