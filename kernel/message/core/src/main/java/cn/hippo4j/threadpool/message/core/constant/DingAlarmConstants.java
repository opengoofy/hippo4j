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
 * Ding alarm constants.
 */
public class DingAlarmConstants {

    /**
     * DingTalk Robot Url
     */
    public static final String DING_ROBOT_SERVER_URL = "https://oapi.dingtalk.com/robot/send?access_token=";

    /**
     * Thread Pool Alert Notification Title
     */
    public static final String DING_ALARM_TITLE = "动态线程池告警";

    /**
     * Thread pool parameter change notification title
     */
    public static final String DING_NOTICE_TITLE = "动态线程池通知";

    /**
     * Trace info
     */
    public static final String DING_ALARM_TIMEOUT_TRACE_REPLACE_TXT = "<font color=#708090 size=2>链路信息：%s</font> \n\n ";

    /**
     * Replace task timeout template
     */
    public static final String DING_ALARM_TIMEOUT_REPLACE_TXT =
            "<font color=#708090 size=2>任务执行时间：%d / ms </font> \n\n "
                    + "<font color=#708090 size=2>超时时间：%d / ms</font> \n\n "
                    + DING_ALARM_TIMEOUT_TRACE_REPLACE_TXT
                    + " --- \n\n ";
}
