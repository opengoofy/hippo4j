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
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.common.toolkit.Singleton;
import cn.hippo4j.common.toolkit.http.HttpUtil;
import cn.hippo4j.threadpool.message.core.platform.base.AbstractRobotSendMessageHandler;
import cn.hippo4j.threadpool.message.core.platform.base.RobotMessageActualContent;
import cn.hippo4j.threadpool.message.core.platform.base.RobotMessageExecuteDTO;
import cn.hippo4j.threadpool.message.api.NotifyPlatformEnum;
import cn.hippo4j.threadpool.message.core.constant.WeChatAlarmConstants;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * WeChat send message handler.
 */
@Slf4j
public class WeChatSendMessageHandler extends AbstractRobotSendMessageHandler {

    @Override
    public String getType() {
        return NotifyPlatformEnum.WECHAT.name();
    }

    @Override
    protected RobotMessageActualContent buildMessageActualContent() {
        String weChatAlarmTxtKey = "message/robot/dynamic-thread-pool/wechat-alarm.txt";
        String weChatConfigTxtKey = "message/robot/dynamic-thread-pool/wechat-config.txt";
        return RobotMessageActualContent.builder()
                .receiveSeparator("><@")
                .changeSeparator("  ➲  ")
                .replaceTxt(WeChatAlarmConstants.WE_CHAT_ALARM_TIMOUT_REPLACE_TXT)
                .traceReplaceTxt(WeChatAlarmConstants.WE_CHAT_ALARM_TIMOUT_TRACE_REPLACE_TXT)
                .alarmMessageContent(Singleton.get(weChatAlarmTxtKey, () -> readUtf8String(weChatAlarmTxtKey)))
                .configMessageContent(Singleton.get(weChatConfigTxtKey, () -> readUtf8String(weChatConfigTxtKey)))
                .build();
    }

    @Override
    protected void execute(RobotMessageExecuteDTO robotMessageExecuteDTO) {
        String serverUrl = WeChatAlarmConstants.WE_CHAT_SERVER_URL + robotMessageExecuteDTO.getNotifyConfig().getSecretKey();
        try {
            WeChatReqDTO weChatReq = new WeChatReqDTO();
            weChatReq.setMsgtype("markdown");
            Markdown markdown = new Markdown();
            markdown.setContent(robotMessageExecuteDTO.getText());
            weChatReq.setMarkdown(markdown);
            String responseBody = HttpUtil.post(serverUrl, weChatReq);
            WeChatRobotResponse response = JSONUtil.parseObject(responseBody, WeChatRobotResponse.class);
            Assert.notNull(response, "Response is null.");
            if (response.getErrcode() != 0) {
                log.error("WeChat failed to send message, reason : {}", response.errmsg);
            }
        } catch (Exception ex) {
            log.error("WeChat failed to send message", ex);
        }
    }

    /**
     * WeChat
     */
    @Data
    @Accessors(chain = true)
    public static class WeChatReqDTO {

        private String msgtype;

        private Markdown markdown;
    }

    /**
     * Markdown
     */
    @Data
    public static class Markdown {

        private String content;
    }

    /**
     * WeChat robot response.
     */
    @Data
    static class WeChatRobotResponse {

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
