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

package cn.hippo4j.message.platform.constant;

/**
 * Email alarm constants.
 */
public class EmailAlarmConstants {

    /**
     * Thread Pool Alert Notification Title
     */
    public static String Email_ALARM_TITLE = "【Hippo4J】${active}-${threadPoolId} 线程池 ${notifyTypeEnum} 预警";

    /**
     * Thread pool parameter change notification title
     */
    public static String Email_NOTICE_TITLE = "【Hippo4J】${active}-${threadPoolId} 线程池参数变更通知";
}