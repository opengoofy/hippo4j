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

public class Constants {

    public static String PATH_SEPARATOR = System.getProperty("file.separator", "/");

    public static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    public static String EMPTY_STRING = "";

    public static char SERVICE_NAME_PART_CONNECTOR = '|';

    // The name of the layer that represents agent-installed services,
    // which is defined at
    // https://github.com/apache/skywalking/blob/85ce1645be53e46286f36c0ea206c60db2d1a716/oap-server/server-core/src/main/java/org/apache/skywalking/oap/server/core/analysis/Layer.java#L30
    public static String EVENT_LAYER_NAME = "GENERAL";

    public static int NULL_VALUE = 0;

    public static String SPRING_BOOT_CONFIG_PREFIX = "spring.dynamic.thread-pool";

}
