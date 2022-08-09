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
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.enums.NotifyPlatformEnum;
import cn.hippo4j.message.enums.NotifyTypeEnum;
import cn.hippo4j.message.service.SendMessageHandler;
import cn.hippo4j.message.request.AlarmNotifyRequest;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hutool.core.date.DateUtil;
import cn.hippo4j.common.toolkit.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.hippo4j.message.platform.constant.LarkAlarmConstants.*;

/**
 * Send lark notification message.
 */
@Slf4j
@AllArgsConstructor
public class LarkSendMessageHandler implements SendMessageHandler<AlarmNotifyRequest, ChangeParameterNotifyRequest> {

    @Override
    public String getType() {
        return NotifyPlatformEnum.LARK.name();
    }

    @Override
    @SneakyThrows
    public void sendAlarmMessage(NotifyConfigDTO notifyConfig, AlarmNotifyRequest alarmNotifyRequest) {
        String afterReceives = getReceives(notifyConfig.getReceives());
        String larkAlarmTimoutReplaceTxt;
        String larkAlarmTxtKey = "message/robot/dynamic-thread-pool/lark-alarm.json";
        String larkAlarmTxt = Singleton.get(larkAlarmTxtKey, () -> FileUtil.readUtf8String(larkAlarmTxtKey));
        String larkAlarmTimoutReplaceJsonKey = "message/robot/dynamic-thread-pool/lark-alarm-timeout-replace.json";
        String larkAlarmTimoutReplaceJson = Singleton.get(larkAlarmTimoutReplaceJsonKey, () -> FileUtil.readUtf8String(larkAlarmTimoutReplaceJsonKey));
        if (Objects.equals(alarmNotifyRequest.getNotifyTypeEnum(), NotifyTypeEnum.TIMEOUT)) {
            String executeTimeoutTrace = alarmNotifyRequest.getExecuteTimeoutTrace();
            String larkAlarmTimoutTraceReplaceJsonKey = "message/robot/dynamic-thread-pool/lark-alarm-trace-replace.json";
            String larkAlarmTimoutTraceReplaceJson = Singleton.get(larkAlarmTimoutTraceReplaceJsonKey, () -> FileUtil.readUtf8String(larkAlarmTimoutTraceReplaceJsonKey));
            if (StringUtil.isNotBlank(executeTimeoutTrace)) {
                String larkAlarmTimoutTraceReplaceTxt = String.format(larkAlarmTimoutTraceReplaceJson, executeTimeoutTrace);
                larkAlarmTimoutReplaceTxt = StrUtil.replace(larkAlarmTimoutReplaceJson, larkAlarmTimoutTraceReplaceJson, larkAlarmTimoutTraceReplaceTxt);
            } else {
                larkAlarmTimoutReplaceTxt = StrUtil.replace(larkAlarmTimoutReplaceJson, larkAlarmTimoutTraceReplaceJson, "");
            }
            larkAlarmTimoutReplaceTxt = String.format(larkAlarmTimoutReplaceTxt, alarmNotifyRequest.getExecuteTime(), alarmNotifyRequest.getExecuteTimeOut());
            larkAlarmTxt = StrUtil.replace(larkAlarmTxt, larkAlarmTimoutReplaceJson, larkAlarmTimoutReplaceTxt);
        } else {
            larkAlarmTxt = StrUtil.replace(larkAlarmTxt, larkAlarmTimoutReplaceJson, "");
        }

        String text = String.format(larkAlarmTxt,
                // 环境
                alarmNotifyRequest.getActive(),
                // 报警类型
                alarmNotifyRequest.getNotifyTypeEnum(),
                // 线程池ID
                alarmNotifyRequest.getThreadPoolId(),
                // 应用名称
                alarmNotifyRequest.getAppName(),
                // 实例信息
                alarmNotifyRequest.getIdentify(),
                // 核心线程数
                alarmNotifyRequest.getCorePoolSize(),
                // 最大线程数
                alarmNotifyRequest.getMaximumPoolSize(),
                // 当前线程数
                alarmNotifyRequest.getPoolSize(),
                // 活跃线程数
                alarmNotifyRequest.getActiveCount(),
                // 最大任务数
                alarmNotifyRequest.getLargestPoolSize(),
                // 线程池任务总量
                alarmNotifyRequest.getCompletedTaskCount(),
                // 队列类型名称
                alarmNotifyRequest.getQueueName(),
                // 队列容量
                alarmNotifyRequest.getCapacity(),
                // 队列元素个数
                alarmNotifyRequest.getQueueSize(),
                // 队列剩余个数
                alarmNotifyRequest.getRemainingCapacity(),
                // 拒绝策略名称
                alarmNotifyRequest.getRejectedExecutionHandlerName(),
                // 拒绝策略次数
                alarmNotifyRequest.getRejectCountNum(),
                // 告警手机号
                afterReceives,
                // 当前时间
                DateUtil.now(),
                // 报警频率
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
                // 环境
                changeParameterNotifyRequest.getActive(),
                // 线程池名称
                threadPoolId,
                // 应用名称
                changeParameterNotifyRequest.getAppName(),
                // 实例信息
                changeParameterNotifyRequest.getIdentify(),
                // 核心线程数
                changeParameterNotifyRequest.getBeforeCorePoolSize() + "  ➲  " + changeParameterNotifyRequest.getNowCorePoolSize(),
                // 最大线程数
                changeParameterNotifyRequest.getBeforeMaximumPoolSize() + "  ➲  " + changeParameterNotifyRequest.getNowMaximumPoolSize(),
                // 核心线程超时
                changeParameterNotifyRequest.getBeforeAllowsCoreThreadTimeOut() + "  ➲  " + changeParameterNotifyRequest.getNowAllowsCoreThreadTimeOut(),
                // 线程存活时间
                changeParameterNotifyRequest.getBeforeKeepAliveTime() + "  ➲  " + changeParameterNotifyRequest.getNowKeepAliveTime(),
                // 阻塞队列
                changeParameterNotifyRequest.getBlockingQueueName(),
                // 阻塞队列容量
                changeParameterNotifyRequest.getBeforeQueueCapacity() + "  ➲  " + changeParameterNotifyRequest.getNowQueueCapacity(),
                // 执行超时时间
                changeParameterNotifyRequest.getBeforeExecuteTimeOut() + "  ➲  " + changeParameterNotifyRequest.getNowExecuteTimeOut(),
                // 拒绝策略
                changeParameterNotifyRequest.getBeforeRejectedName(),
                changeParameterNotifyRequest.getNowRejectedName(),
                // 告警手机号
                afterReceives,
                // 当前时间
                DateUtil.now());
        execute(notifyConfig.getSecretKey(), text);
    }

    private String getReceives(String receives) {
        if (StringUtil.isBlank(receives)) {
            return "";
        }
        return Arrays.stream(receives.split(","))
                .map(receive -> StrUtil.startWith(receive, LARK_OPENID_PREFIX) ? String.format(LARK_AT_FORMAT_OPENID, receive) : String.format(LARK_AT_FORMAT_USERNAME, receive))
                .collect(Collectors.joining(" "));
    }

    private void execute(String secretKey, String text) {
        String serverUrl = LARK_BOT_URL + secretKey;
        try {
            HttpRequest.post(serverUrl).body(text).execute();
        } catch (Exception ex) {
            log.error("Lark failed to send message", ex);
        }
    }
}
