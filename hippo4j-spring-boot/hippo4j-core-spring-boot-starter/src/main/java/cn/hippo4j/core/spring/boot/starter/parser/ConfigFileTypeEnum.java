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

package cn.hippo4j.core.spring.boot.starter.parser;

import lombok.Getter;

/**
 * @author : wh
 * @date : 2022/3/1 07:47
 * @description:
 */
@Getter
public enum ConfigFileTypeEnum {

    /**
     * properties
     */
    PROPERTIES("properties"),

    /**
     * xml
     */
    XML("xml"),

    /**
     * json
     */
    JSON("json"),

    /**
     * yml
     */
    YML("yml"),

    /**
     * yaml
     */
    YAML("yaml"),

    /**
     * txt
     */
    TXT("txt");

    private final String value;

    ConfigFileTypeEnum(String value) {
        this.value = value;
    }

    public static ConfigFileTypeEnum of(String value) {
        for (ConfigFileTypeEnum typeEnum : ConfigFileTypeEnum.values()) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return PROPERTIES;
    }

}
