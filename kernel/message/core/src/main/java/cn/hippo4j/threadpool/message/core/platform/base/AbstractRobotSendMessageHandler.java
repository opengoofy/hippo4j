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

package cn.hippo4j.threadpool.message.core.platform.base;

import cn.hippo4j.common.toolkit.Joiner;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.threadpool.message.api.NotifyConfigDTO;
import cn.hippo4j.threadpool.message.api.NotifyTypeEnum;
import cn.hippo4j.threadpool.message.core.request.AlarmNotifyRequest;
import cn.hippo4j.threadpool.message.core.request.ChangeParameterNotifyRequest;
import cn.hippo4j.threadpool.message.core.service.SendMessageHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Abstract robot send message handler.
 */
public abstract class AbstractRobotSendMessageHandler implements SendMessageHandler {

    /**
     * Build message actual content.
     *
     * @return robot message actual content
     */
    protected abstract RobotMessageActualContent buildMessageActualContent();

    /**
     * Execute.
     *
     * @param robotMessageExecuteDTO robot message execute dto
     */
    protected abstract void execute(RobotMessageExecuteDTO robotMessageExecuteDTO);

    /**
     * Send alarm message.
     *
     * @param notifyConfig       notify config
     * @param alarmNotifyRequest alarm notify request
     */
    public void sendAlarmMessage(NotifyConfigDTO notifyConfig, AlarmNotifyRequest alarmNotifyRequest) {
        RobotMessageActualContent robotMessageActualContent = buildMessageActualContent();
        String replaceTxt = robotMessageActualContent.getReplaceTxt();
        String traceReplaceTxt = robotMessageActualContent.getTraceReplaceTxt();
        String alarmContentTxt = robotMessageActualContent.getAlarmMessageContent();
        String alarmTimoutReplaceTxt;
        if (Objects.equals(alarmNotifyRequest.getNotifyTypeEnum(), NotifyTypeEnum.TIMEOUT)) {
            String executeTimeoutTrace = alarmNotifyRequest.getExecuteTimeoutTrace();
            if (StringUtil.isNotBlank(executeTimeoutTrace)) {
                String alarmTimoutTraceReplaceTxt = String.format(traceReplaceTxt, executeTimeoutTrace);
                alarmTimoutReplaceTxt = StringUtil.replace(replaceTxt, traceReplaceTxt, alarmTimoutTraceReplaceTxt);
            } else {
                alarmTimoutReplaceTxt = StringUtil.replace(replaceTxt, traceReplaceTxt, "");
            }
            replaceTxt = String.format(alarmTimoutReplaceTxt, alarmNotifyRequest.getExecuteTime(), alarmNotifyRequest.getExecuteTimeOut());
        } else {
            replaceTxt = "";
        }
        alarmContentTxt = StringUtil.replace(alarmContentTxt, "${timeout-content}", replaceTxt);
        String text = String.format(
                alarmContentTxt,
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
                Joiner.on(robotMessageActualContent.getReceiveSeparator()).join(notifyConfig.getReceives().split(",")),
                notifyConfig.getInterval(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        execute(RobotMessageExecuteDTO.builder().text(text).notifyConfig(notifyConfig).build());
    }

    /**
     * Send change message.
     *
     * @param notifyConfig                 notify config
     * @param changeParameterNotifyRequest change parameter notify request
     */
    public void sendChangeMessage(NotifyConfigDTO notifyConfig, ChangeParameterNotifyRequest changeParameterNotifyRequest) {
        RobotMessageActualContent robotMessageActualContent = buildMessageActualContent();
        String threadPoolId = changeParameterNotifyRequest.getThreadPoolId();
        String changeSeparator = robotMessageActualContent.getChangeSeparator();
        String text = String.format(
                robotMessageActualContent.getConfigMessageContent(),
                changeParameterNotifyRequest.getActive(),
                threadPoolId,
                changeParameterNotifyRequest.getAppName(),
                changeParameterNotifyRequest.getIdentify(),
                changeParameterNotifyRequest.getBeforeCorePoolSize() + changeSeparator + changeParameterNotifyRequest.getNowCorePoolSize(),
                changeParameterNotifyRequest.getBeforeMaximumPoolSize() + changeSeparator + changeParameterNotifyRequest.getNowMaximumPoolSize(),
                changeParameterNotifyRequest.getBeforeAllowsCoreThreadTimeOut() + changeSeparator + changeParameterNotifyRequest.getNowAllowsCoreThreadTimeOut(),
                changeParameterNotifyRequest.getBeforeKeepAliveTime() + changeSeparator + changeParameterNotifyRequest.getNowKeepAliveTime(),
                changeParameterNotifyRequest.getBeforeExecuteTimeOut() + changeSeparator + changeParameterNotifyRequest.getNowExecuteTimeOut(),
                changeParameterNotifyRequest.getBlockingQueueName(),
                changeParameterNotifyRequest.getBeforeQueueCapacity() + changeSeparator + changeParameterNotifyRequest.getNowQueueCapacity(),
                changeParameterNotifyRequest.getBeforeRejectedName(),
                changeParameterNotifyRequest.getNowRejectedName(),
                Joiner.on(robotMessageActualContent.getReceiveSeparator()).join(notifyConfig.getReceives().split(",")),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        execute(RobotMessageExecuteDTO.builder().text(text).notifyConfig(notifyConfig).build());
    }
}
