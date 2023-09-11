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

package cn.hippo4j.threadpool.message.core.platform;

import cn.hippo4j.common.toolkit.Assert;
import cn.hippo4j.common.toolkit.CollectionUtil;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.Singleton;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.common.toolkit.http.HttpUtil;
import cn.hippo4j.threadpool.message.core.platform.base.AbstractRobotSendMessageHandler;
import cn.hippo4j.threadpool.message.core.platform.base.RobotMessageActualContent;
import cn.hippo4j.threadpool.message.core.platform.base.RobotMessageExecuteDTO;
import cn.hippo4j.threadpool.message.core.constant.DingAlarmConstants;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.api.NotifyPlatformEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Send ding notification message.
 */
@Slf4j
public class DingSendMessageHandler extends AbstractRobotSendMessageHandler {

    @Override
    public String getType() {
        return NotifyPlatformEnum.DING.name();
    }

    @Override
    protected RobotMessageActualContent buildMessageActualContent() {
        String dingAlarmTxtKey = "message/robot/dynamic-thread-pool/ding-alarm.txt";
        String dingConfigTxtKey = "message/robot/dynamic-thread-pool/ding-config.txt";
        RobotMessageActualContent robotMessageActualContent = RobotMessageActualContent.builder()
                .receiveSeparator(", @")
                .changeSeparator(" -> ")
                .replaceTxt(DingAlarmConstants.DING_ALARM_TIMEOUT_REPLACE_TXT)
                .traceReplaceTxt(DingAlarmConstants.DING_ALARM_TIMEOUT_TRACE_REPLACE_TXT)
                .alarmMessageContent(Singleton.get(dingAlarmTxtKey, () -> readUtf8String(dingAlarmTxtKey)))
                .configMessageContent(Singleton.get(dingConfigTxtKey, () -> readUtf8String(dingConfigTxtKey)))
                .build();
        return robotMessageActualContent;
    }

    @Override
    protected void execute(RobotMessageExecuteDTO robotMessageExecuteDTO) {
        NotifyConfigDTO notifyConfig = robotMessageExecuteDTO.getNotifyConfig();
        String serverUrl = DingAlarmConstants.DING_ROBOT_SERVER_URL + notifyConfig.getSecretKey();
        String secret = notifyConfig.getSecret();
        if (StringUtil.isNotBlank(secret)) {
            long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
                byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
                String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), StandardCharsets.UTF_8.name());
                serverUrl = serverUrl + "&timestamp=" + timestamp + "&sign=" + sign;
            } catch (Exception ex) {
                log.error("Failed to sign the message sent by nailing.", ex);
            }
        }
        String title = Objects.equals(notifyConfig.getType(), "CONFIG") ? DingAlarmConstants.DING_NOTICE_TITLE : DingAlarmConstants.DING_ALARM_TITLE;
        String text = robotMessageExecuteDTO.getText();
        ArrayList<String> atMobiles = CollectionUtil.newArrayList(notifyConfig.getReceives().split(","));
        HashMap<String, Object> markdown = new HashMap<>();
        markdown.put("title", title);
        markdown.put("text", text);
        HashMap<String, Object> at = new HashMap<>();
        at.put("atMobiles", atMobiles);
        HashMap<String, Object> markdownJson = new HashMap<>();
        markdownJson.put("msgtype", "markdown");
        markdownJson.put("markdown", markdown);
        markdownJson.put("at", at);
        try {
            String responseBody = HttpUtil.post(serverUrl, markdownJson);
            DingRobotResponse response = JSONUtil.parseObject(responseBody, DingRobotResponse.class);
            Assert.isTrue(response != null, "Response is null.");
            if (response.getErrcode() != 0) {
                log.error("Ding failed to send message, reason : {}", response.errmsg);
            }
        } catch (Exception ex) {
            log.error("Ding failed to send message.", ex);
        }
    }

    /**
     * Ding robot response.
     */
    @Data
    static class DingRobotResponse {

        /**
         * Error code
         */
        private Long errcode;

        /**
         * Error message
         */
        private String errmsg;
    }
}
