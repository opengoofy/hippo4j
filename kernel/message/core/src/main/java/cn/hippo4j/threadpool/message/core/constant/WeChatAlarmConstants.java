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

package cn.hippo4j.threadpool.message.core.constant;

/**
 * We chat alarm constants.
 */
public class WeChatAlarmConstants {

    /**
     * Enterprise Micro Robot Url
     */
    public static final String WE_CHAT_SERVER_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";

    /**
     * Trace info
     */
    public static final String WE_CHAT_ALARM_TIMOUT_TRACE_REPLACE_TXT = "\n> 链路信息：%s ";

    /**
     * Replace task timeout template
     */
    public static final String WE_CHAT_ALARM_TIMOUT_REPLACE_TXT =
            "\n> 任务执行时间：%s / ms \n"
                    + "> 超时时间：%s / ms "
                    + WE_CHAT_ALARM_TIMOUT_TRACE_REPLACE_TXT;
}
