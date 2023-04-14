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

import cn.hippo4j.common.toolkit.*;
import cn.hippo4j.common.toolkit.http.HttpUtil;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.enums.NotifyPlatformEnum;
import cn.hippo4j.message.enums.NotifyTypeEnum;
import cn.hippo4j.message.service.SendMessageHandler;
import cn.hippo4j.message.request.AlarmNotifyRequest;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hippo4j.message.platform.constant.LarkAlarmConstants.*;

/**
 * Send lark notification message.
 */
@Slf4j
@RequiredArgsConstructor
public class LarkSendMessageHandler implements SendMessageHandler {

    @Override
    public String getType() {
        return NotifyPlatformEnum.LARK.name();
    }

    @Override
    @SneakyThrows
    public void sendAlarmMessage(NotifyConfigDTO notifyConfig, AlarmNotifyRequest alarmNotifyRequest) {
        String afterReceives = getReceives(notifyConfig.getReceives());
        String larkAlarmTimeoutReplaceTxt;
        String larkAlarmTxtKey = "message/robot/dynamic-thread-pool/lark-alarm.json";
        String larkAlarmTxt = Singleton.get(larkAlarmTxtKey, () -> FileUtil.readUtf8String(larkAlarmTxtKey));
        String larkAlarmTimeoutReplaceJsonKey = "message/robot/dynamic-thread-pool/lark-alarm-timeout-replace.json";
        String larkAlarmTimeoutReplaceJson = Singleton.get(larkAlarmTimeoutReplaceJsonKey, () -> FileUtil.readUtf8String(larkAlarmTimeoutReplaceJsonKey));
        if (Objects.equals(alarmNotifyRequest.getNotifyTypeEnum(), NotifyTypeEnum.TIMEOUT)) {
            String executeTimeoutTrace = alarmNotifyRequest.getExecuteTimeoutTrace();
            String larkAlarmTimoutTraceReplaceJsonKey = "message/robot/dynamic-thread-pool/lark-alarm-trace-replace.json";
            String larkAlarmTimoutTraceReplaceJson = Singleton.get(larkAlarmTimoutTraceReplaceJsonKey, () -> FileUtil.readUtf8String(larkAlarmTimoutTraceReplaceJsonKey));
            if (StringUtil.isNotBlank(executeTimeoutTrace)) {
                String larkAlarmTimoutTraceReplaceTxt = String.format(larkAlarmTimoutTraceReplaceJson, executeTimeoutTrace);
                larkAlarmTimeoutReplaceTxt = StringUtil.replace(larkAlarmTimeoutReplaceJson, larkAlarmTimoutTraceReplaceJson, larkAlarmTimoutTraceReplaceTxt);
            } else {
                larkAlarmTimeoutReplaceTxt = StringUtil.replace(larkAlarmTimeoutReplaceJson, larkAlarmTimoutTraceReplaceJson, "");
            }
            larkAlarmTimeoutReplaceTxt = String.format(larkAlarmTimeoutReplaceTxt, alarmNotifyRequest.getExecuteTime(), alarmNotifyRequest.getExecuteTimeOut());
            larkAlarmTxt = StringUtil.replace(larkAlarmTxt, larkAlarmTimeoutReplaceJson, larkAlarmTimeoutReplaceTxt);
        } else {
            larkAlarmTxt = StringUtil.replace(larkAlarmTxt, larkAlarmTimeoutReplaceJson, "");
        }
        String text = String.format(larkAlarmTxt,
                alarmNotifyRequest.getActive(),
                alarmNotifyRequest.getNotifyTypeEnum(),
                alarmNotifyRequest.getThreadPoolId(),
                alarmNotifyRequest.getAppName(),
                alarmNotifyRequest.getIdentify(),
                alarmNotifyRequest.getCorePoolSize(),
                alarmNotifyRequest.getMaximumPoolSize(),
                alarmNotifyRequest.getPoolSize(),
                alarmNotifyRequest.getActiveCount(),
                alarmNotifyRequest.getLargestPoolSize(),
                alarmNotifyRequest.getCompletedTaskCount(),
                alarmNotifyRequest.getQueueName(),
                alarmNotifyRequest.getCapacity(),
                alarmNotifyRequest.getQueueSize(),
                alarmNotifyRequest.getRemainingCapacity(),
                alarmNotifyRequest.getRejectedExecutionHandlerName(),
                alarmNotifyRequest.getRejectCountNum(),
                afterReceives,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                notifyConfig.getInterval());
        execute(notifyConfig.getSecretKey(), text);
    }

    @Override
    @SneakyThrows
    public void sendChangeMessage(NotifyConfigDTO notifyConfig, ChangeParameterNotifyRequest changeParameterNotifyRequest) {
        String threadPoolId = changeParameterNotifyRequest.getThreadPoolId();
        String afterReceives = getReceives(notifyConfig.getReceives());
        String larkNoticeJsonKey = "message/robot/dynamic-thread-pool/lark-config.json";
        String larkNoticeJson = Singleton.get(larkNoticeJsonKey, () -> FileUtil.readUtf8String(larkNoticeJsonKey));
        String text = String.format(larkNoticeJson,
                changeParameterNotifyRequest.getActive(),
                threadPoolId,
                changeParameterNotifyRequest.getAppName(),
                changeParameterNotifyRequest.getIdentify(),
                changeParameterNotifyRequest.getBeforeCorePoolSize() + "  ➲  " + changeParameterNotifyRequest.getNowCorePoolSize(),
                changeParameterNotifyRequest.getBeforeMaximumPoolSize() + "  ➲  " + changeParameterNotifyRequest.getNowMaximumPoolSize(),
                changeParameterNotifyRequest.getBeforeAllowsCoreThreadTimeOut() + "  ➲  " + changeParameterNotifyRequest.getNowAllowsCoreThreadTimeOut(),
                changeParameterNotifyRequest.getBeforeKeepAliveTime() + "  ➲  " + changeParameterNotifyRequest.getNowKeepAliveTime(),
                changeParameterNotifyRequest.getBlockingQueueName(),
                changeParameterNotifyRequest.getBeforeQueueCapacity() + "  ➲  " + changeParameterNotifyRequest.getNowQueueCapacity(),
                changeParameterNotifyRequest.getBeforeExecuteTimeOut() + "  ➲  " + changeParameterNotifyRequest.getNowExecuteTimeOut(),
                changeParameterNotifyRequest.getBeforeRejectedName(),
                changeParameterNotifyRequest.getNowRejectedName(),
                afterReceives,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        execute(notifyConfig.getSecretKey(), text);
    }

    private String getReceives(String receives) {
        if (StringUtil.isBlank(receives)) {
            return "";
        }
        return Arrays.stream(receives.split(","))
                .map(receive -> StringUtil.startWith(receive, LARK_OPENID_PREFIX) ? String.format(LARK_AT_FORMAT_OPENID, receive) : String.format(LARK_AT_FORMAT_USERNAME, receive))
                .collect(Collectors.joining(" "));
    }

    private void execute(String secretKey, String text) {
        String serverUrl = LARK_BOT_URL + secretKey;
        try {
            String responseBody = HttpUtil.postJson(serverUrl, text);
            LarkRobotResponse response = JSONUtil.parseObject(responseBody, LarkRobotResponse.class);
            Assert.isTrue(response != null, "Response is null.");
            if (response.getCode() != 0) {
                log.error("Lark failed to send message, reason : {}", response.msg);
            }
        } catch (Exception ex) {
            log.error("Lark failed to send message", ex);
        }
    }

    /**
     * Lark robot response.
     */
    @Data
    static class LarkRobotResponse {

        /**
         * code
         */
        private Long code;

        /**
         * message
         */
        private String msg;
    }
}
