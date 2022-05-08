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

package cn.hippo4j.tools.logrecord.service.impl;

import cn.hippo4j.tools.logrecord.service.ParseFunction;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 函数解析工厂.
 *
 * @author chen.ma
 * @date 2021/10/23 22:39
 */
public class ParseFunctionFactory {

    private Map<String, ParseFunction> allFunctionMap;

    public ParseFunctionFactory(List<ParseFunction> parseFunctions) {
        if (CollectionUtils.isEmpty(parseFunctions)) {
            return;
        }

        allFunctionMap = Maps.newHashMap();
        for (ParseFunction parseFunction : parseFunctions) {
            if (StringUtils.isEmpty(parseFunction.functionName())) {
                continue;
            }

            allFunctionMap.put(parseFunction.functionName(), parseFunction);
        }
    }

    /**
     * 获取函数实例.
     *
     * @param functionName
     * @return
     */
    public ParseFunction getFunction(String functionName) {
        return allFunctionMap.get(functionName);
    }

    /**
     * 是否提前执行.
     *
     * @param functionName
     * @return
     */
    public boolean isBeforeFunction(String functionName) {
        return allFunctionMap.get(functionName) != null && allFunctionMap.get(functionName).executeBefore();
    }

}
