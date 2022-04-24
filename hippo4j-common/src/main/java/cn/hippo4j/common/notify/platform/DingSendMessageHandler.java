package cn.hippo4j.common.notify.platform;

import cn.hippo4j.common.notify.NotifyConfigDTO;
import cn.hippo4j.common.notify.NotifyPlatformEnum;
import cn.hippo4j.common.notify.NotifyTypeEnum;
import cn.hippo4j.common.notify.SendMessageHandler;
import cn.hippo4j.common.notify.request.AlarmNotifyRequest;
import cn.hippo4j.common.notify.request.ChangeParameterNotifyRequest;
import cn.hippo4j.common.toolkit.StringUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static cn.hippo4j.common.notify.platform.DingAlarmConstants.*;

/**
 * Send ding notification message.
 *
 * @author chen.ma
 * @date 2021/8/15 15:49
 */
@Slf4j
public class DingSendMessageHandler implements SendMessageHandler<AlarmNotifyRequest, ChangeParameterNotifyRequest> {

    @Override
    public String getType() {
        return NotifyPlatformEnum.DING.name();
    }

    @Override
    public void sendAlarmMessage(NotifyConfigDTO notifyConfig, AlarmNotifyRequest alarmNotifyRequest) {
        String[] receives = notifyConfig.getReceives().split(",");
        String afterReceives = Joiner.on(", @").join(receives);

        String dingAlarmTxt;
        String dingAlarmTimoutReplaceTxt;
        if (Objects.equals(alarmNotifyRequest.getNotifyTypeEnum(), NotifyTypeEnum.TIMEOUT)) {
            String executeTimeoutTrace = alarmNotifyRequest.getExecuteTimeoutTrace();
            if (StringUtil.isNotBlank(executeTimeoutTrace)) {
                String dingAlarmTimoutTraceReplaceTxt = String.format(DING_ALARM_TIMOUT_TRACE_REPLACE_TXT, executeTimeoutTrace);
                dingAlarmTimoutReplaceTxt = StrUtil.replace(DING_ALARM_TIMOUT_REPLACE_TXT, DING_ALARM_TIMOUT_TRACE_REPLACE_TXT, dingAlarmTimoutTraceReplaceTxt);
            } else {
                dingAlarmTimoutReplaceTxt = StrUtil.replace(DING_ALARM_TIMOUT_REPLACE_TXT, DING_ALARM_TIMOUT_TRACE_REPLACE_TXT, "");
            }

            dingAlarmTimoutReplaceTxt = String.format(dingAlarmTimoutReplaceTxt, alarmNotifyRequest.getExecuteTime(), alarmNotifyRequest.getExecuteTimeOut());
            dingAlarmTxt = StrUtil.replace(DING_ALARM_TXT, DING_ALARM_TIMOUT_REPLACE_TXT, dingAlarmTimoutReplaceTxt);
        } else {
            dingAlarmTxt = StrUtil.replace(DING_ALARM_TXT, DING_ALARM_TIMOUT_REPLACE_TXT, "");
        }

        String[] strings = alarmNotifyRequest.getIdentify().split("_");
        String text = String.format(
                dingAlarmTxt,
                // 环境
                alarmNotifyRequest.getActive(),
                // 报警类型
                alarmNotifyRequest.getNotifyTypeEnum(),
                // 线程池ID
                alarmNotifyRequest.getThreadPoolId(),
                // 应用名称
                alarmNotifyRequest.getAppName(),
                // Host
                strings[0],
                // 实例ID
                strings[1],
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
                // 报警频率
                notifyConfig.getInterval(),
                // 当前时间
                DateUtil.now()
        );

        execute(notifyConfig, DingAlarmConstants.DING_ALARM_TITLE, text, Lists.newArrayList(receives));
    }

    @Override
    public void sendChangeMessage(NotifyConfigDTO notifyConfig, ChangeParameterNotifyRequest changeParameterNotifyRequest) {
        String threadPoolId = changeParameterNotifyRequest.getThreadPoolId();

        String[] receives = notifyConfig.getReceives().split(",");
        String afterReceives = Joiner.on(", @").join(receives);

        /**
         * hesitant e.g. ➲  ➜  ⇨  ➪
         */
        String text = String.format(
                DING_NOTICE_TXT,
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
                // 执行超时时间
                changeParameterNotifyRequest.getBeforeExecuteTimeOut() + "  ➲  " + changeParameterNotifyRequest.getNowExecuteTimeOut(),
                // 阻塞队列
                changeParameterNotifyRequest.getBlockingQueueName(),
                // 阻塞队列容量
                changeParameterNotifyRequest.getBeforeQueueCapacity() + "  ➲  " + changeParameterNotifyRequest.getNowQueueCapacity(),
                // 拒绝策略
                changeParameterNotifyRequest.getBeforeRejectedName(),
                changeParameterNotifyRequest.getNowRejectedName(),
                // 告警手机号
                afterReceives,
                // 当前时间
                DateUtil.now()
        );

        execute(notifyConfig, DingAlarmConstants.DING_NOTICE_TITLE, text, Lists.newArrayList(receives));
    }

    private void execute(NotifyConfigDTO notifyConfig, String title, String text, List<String> mobiles) {
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
        markdown.setTitle(title);
        markdown.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(mobiles);
        request.setAt(at);
        request.setMarkdown(markdown);
        try {
            dingTalkClient.execute(request);
        } catch (ApiException ex) {
            log.error("Ding failed to send message", ex);
        }
    }

}
