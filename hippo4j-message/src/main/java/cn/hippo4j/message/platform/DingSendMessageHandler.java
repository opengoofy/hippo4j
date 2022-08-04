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

package cn.hippo4j.message.platform;

import cn.hippo4j.common.toolkit.Singleton;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.enums.NotifyPlatformEnum;
import cn.hippo4j.message.platform.base.AbstractRobotSendMessageHandler;
import cn.hippo4j.message.platform.base.RobotMessageActualContent;
import cn.hippo4j.message.platform.base.RobotMessageExecuteDTO;
import cn.hippo4j.message.platform.constant.DingAlarmConstants;
import cn.hippo4j.common.toolkit.FileUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static cn.hippo4j.message.platform.constant.DingAlarmConstants.*;

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
                .replaceTxt(DING_ALARM_TIMOUT_REPLACE_TXT)
                .traceReplaceTxt(DING_ALARM_TIMOUT_TRACE_REPLACE_TXT)
                .alarmMessageContent(Singleton.get(dingAlarmTxtKey, () -> FileUtil.readUtf8String(dingAlarmTxtKey)))
                .configMessageContent(Singleton.get(dingConfigTxtKey, () -> FileUtil.readUtf8String(dingConfigTxtKey)))
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
        DingTalkClient dingTalkClient = new DefaultDingTalkClient(serverUrl);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(Objects.equals(notifyConfig.getType(), "CONFIG") ? DING_NOTICE_TITLE : DING_ALARM_TITLE);
        markdown.setText(robotMessageExecuteDTO.getText());
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(Lists.newArrayList(notifyConfig.getReceives().split(",")));
        request.setAt(at);
        request.setMarkdown(markdown);
        try {
            dingTalkClient.execute(request);
        } catch (ApiException ex) {
            log.error("Ding failed to send message", ex);
        }
    }
}
