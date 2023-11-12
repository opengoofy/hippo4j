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

package cn.hippo4j.common.logging.core;

import cn.hippo4j.common.boot.AgentPackageNotFoundException;
import cn.hippo4j.common.boot.AgentPackagePath;
import cn.hippo4j.common.conf.Config;
import cn.hippo4j.common.conf.Constants;
import cn.hippo4j.common.toolkit.StringUtil;

public class WriterFactory {

    private static IWriter WRITER;

    public static IWriter getLogWriter() {

        switch (Config.Logging.OUTPUT) {
            case FILE:
                if (WRITER != null) {
                    return WRITER;
                }
                if (Constants.IS_INIT_COMPLETED
                        && AgentPackagePath.isPathFound()) {
                    if (StringUtil.isEmpty(Config.Logging.DIR)) {
                        try {
                            Config.Logging.DIR = AgentPackagePath.getPath() + "/logs";
                        } catch (AgentPackageNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    WRITER = FileWriter.get();
                } else {
                    return SystemOutWriter.INSTANCE;
                }
                break;
            default:
                return SystemOutWriter.INSTANCE;

        }
        return WRITER;
    }
}
