package cn.hippo4j.common.notify.platform;

import cn.hippo4j.common.notify.NotifyPlatformEnum;
import cn.hippo4j.common.notify.request.RobotAlarmNotifyRequest;
import cn.hippo4j.common.notify.request.RobotChangeParameterNotifyRequest;
import cn.hippo4j.common.notify.SendMessageHandler;
import cn.hutool.core.date.DateUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.hippo4j.common.notify.platform.DingAlarmConstants.DING_ALARM_TXT;
import static cn.hippo4j.common.notify.platform.DingAlarmConstants.DING_NOTICE_TXT;

/**
 * Send ding notification message.
 *
 * @author chen.ma
 * @date 2021/8/15 15:49
 */
@Slf4j
public class DingSendMessageHandler implements SendMessageHandler<RobotAlarmNotifyRequest, RobotChangeParameterNotifyRequest> {

    @Override
    public String getType() {
        return NotifyPlatformEnum.DING.name();
    }

    @Override
    public void sendAlarmMessage(RobotAlarmNotifyRequest alarmNotifyRequest) {
        String[] receives = alarmNotifyRequest.getReceives().split(",");
        String afterReceives = Joiner.on(", @").join(receives);

        String text = String.format(
                DING_ALARM_TXT,
                // 环境
                alarmNotifyRequest.getActive(),
                // 线程池ID
                alarmNotifyRequest.getThreadPoolId(),
                // 应用名称
                alarmNotifyRequest.getAppName(),
                // 实例信息
                alarmNotifyRequest.getIdentify(),
                // 报警类型
                alarmNotifyRequest.getNotifyTypeEnum(),
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
                alarmNotifyRequest.getClass().getSimpleName(),
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
                alarmNotifyRequest.getInterval(),
                // 当前时间
                DateUtil.now()
        );

        execute(alarmNotifyRequest.getSecretKey(), DingAlarmConstants.DING_ALARM_TITLE, text, Lists.newArrayList(receives));
    }

    @Override
    public void sendChangeMessage(RobotChangeParameterNotifyRequest changeParameterNotifyRequest) {
        String threadPoolId = changeParameterNotifyRequest.getThreadPoolId();

        String[] receives = changeParameterNotifyRequest.getReceives().split(",");
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
                changeParameterNotifyRequest.getBeforeKeepAliveTime() + "  ➲  " + changeParameterNotifyRequest.getNowAllowsCoreThreadTimeOut(),
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

        execute(changeParameterNotifyRequest.getSecretKey(), DingAlarmConstants.DING_NOTICE_TITLE, text, Lists.newArrayList(receives));
    }

    private void execute(String secretKey, String title, String text, List<String> mobiles) {
        String serverUrl = DingAlarmConstants.DING_ROBOT_SERVER_URL + secretKey;
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
