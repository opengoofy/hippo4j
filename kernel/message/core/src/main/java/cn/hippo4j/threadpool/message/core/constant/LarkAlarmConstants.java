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
 * Lark alarm constants.
 */
public class LarkAlarmConstants {

    /**
     * Lark bot url
     */
    public static final String LARK_BOT_URL = "https://open.feishu.cn/open-apis/bot/v2/hook/";

    /**
     * Lark at format. openid
     * When openid is configured, the bot can @person
     */
    public static final String LARK_AT_FORMAT_OPENID = "<at id='%s'></at>";

    /**
     * Lark at format. username
     * When configuring username, only @username can be displayed in blue font, and it is reminded by @people without @
     */
    public static final String LARK_AT_FORMAT_USERNAME = "<at id=''>%s</at>";

    /**
     * Lark openid prefix
     */
    public static final String LARK_OPENID_PREFIX = "ou_";
}
