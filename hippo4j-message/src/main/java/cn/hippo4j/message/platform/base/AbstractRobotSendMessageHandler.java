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

package cn.hippo4j.message.platform.base;

import cn.hippo4j.common.toolkit.StringUtil;
import cn.hippo4j.message.dto.NotifyConfigDTO;
import cn.hippo4j.message.enums.NotifyTypeEnum;
import cn.hippo4j.message.request.AlarmNotifyRequest;
import cn.hippo4j.message.request.ChangeParameterNotifyRequest;
import cn.hippo4j.message.service.SendMessageHandler;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;

import java.util.Objects;

/**
 * Abstract robot send message handler.
 */
public abstract class AbstractRobotSendMessageHandler implements SendMessageHandler<AlarmNotifyRequest, ChangeParameterNotifyRequest> {

    /**
     * Build message actual content.
     *
     * @return
     */
    protected abstract RobotMessageActualContent buildMessageActualContent();

    /**
     * Execute.
     *
     * @param robotMessageExecuteDTO
     */
    protected abstract void execute(RobotMessageExecuteDTO robotMessageExecuteDTO);

    /**
     * Send alarm message.
     *
     * @param notifyConfig
     * @param alarmNotifyRequest
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
                alarmTimoutReplaceTxt = StrUtil.replace(replaceTxt, traceReplaceTxt, alarmTimoutTraceReplaceTxt);
            } else {
                alarmTimoutReplaceTxt = StrUtil.replace(replaceTxt, traceReplaceTxt, "");
            }
            replaceTxt = String.format(alarmTimoutReplaceTxt, alarmNotifyRequest.getExecuteTime(), alarmNotifyRequest.getExecuteTimeOut());
        } else {
            replaceTxt = "";
        }
        alarmContentTxt = StrUtil.replace(alarmContentTxt, "${timout-content}", replaceTxt);
        String text = String.format(
                alarmContentTxt,
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
                Joiner.on(robotMessageActualContent.getReceiveSeparator()).join(notifyConfig.getReceives().split(",")),
                // 报警频率
                notifyConfig.getInterval(),
                // 当前时间
                DateUtil.now());
        execute(RobotMessageExecuteDTO.builder().text(text).notifyConfig(notifyConfig).build());
    }

    /**
     * Send change message.
     *
     * @param notifyConfig
     * @param changeParameterNotifyRequest
     */
    public void sendChangeMessage(NotifyConfigDTO notifyConfig, ChangeParameterNotifyRequest changeParameterNotifyRequest) {
        RobotMessageActualContent robotMessageActualContent = buildMessageActualContent();
        String threadPoolId = changeParameterNotifyRequest.getThreadPoolId();
        String changeSeparator = robotMessageActualContent.getChangeSeparator();
        String text = String.format(
                robotMessageActualContent.getConfigMessageContent(),
                // 环境
                changeParameterNotifyRequest.getActive(),
                // 线程池名称
                threadPoolId,
                // 应用名称
                changeParameterNotifyRequest.getAppName(),
                // 实例信息
                changeParameterNotifyRequest.getIdentify(),
                // 核心线程数
                changeParameterNotifyRequest.getBeforeCorePoolSize() + changeSeparator + changeParameterNotifyRequest.getNowCorePoolSize(),
                // 最大线程数
                changeParameterNotifyRequest.getBeforeMaximumPoolSize() + changeSeparator + changeParameterNotifyRequest.getNowMaximumPoolSize(),
                // 核心线程超时
                changeParameterNotifyRequest.getBeforeAllowsCoreThreadTimeOut() + changeSeparator + changeParameterNotifyRequest.getNowAllowsCoreThreadTimeOut(),
                // 线程存活时间
                changeParameterNotifyRequest.getBeforeKeepAliveTime() + changeSeparator + changeParameterNotifyRequest.getNowKeepAliveTime(),
                // 执行超时时间
                changeParameterNotifyRequest.getBeforeExecuteTimeOut() + changeSeparator + changeParameterNotifyRequest.getNowExecuteTimeOut(),
                // 阻塞队列
                changeParameterNotifyRequest.getBlockingQueueName(),
                // 阻塞队列容量
                changeParameterNotifyRequest.getBeforeQueueCapacity() + changeSeparator + changeParameterNotifyRequest.getNowQueueCapacity(),
                // 拒绝策略
                changeParameterNotifyRequest.getBeforeRejectedName(),
                changeParameterNotifyRequest.getNowRejectedName(),
                // 告警手机号
                Joiner.on(robotMessageActualContent.getReceiveSeparator()).join(notifyConfig.getReceives().split(",")),
                // 当前时间
                DateUtil.now());
        execute(RobotMessageExecuteDTO.builder().text(text).notifyConfig(notifyConfig).build());
    }
}
