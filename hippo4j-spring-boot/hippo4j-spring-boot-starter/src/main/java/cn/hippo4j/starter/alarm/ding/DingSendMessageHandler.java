package cn.hippo4j.starter.alarm.ding;

import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.starter.alarm.NotifyDTO;
import cn.hippo4j.starter.alarm.NotifyPlatformEnum;
import cn.hippo4j.starter.alarm.SendMessageHandler;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.toolkit.thread.QueueTypeEnum;
import cn.hippo4j.starter.toolkit.thread.RejectedTypeEnum;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import cn.hutool.core.date.DateUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.taobao.api.ApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.starter.alarm.ding.DingAlarmConstants.DING_ALARM_TXT;
import static cn.hippo4j.starter.alarm.ding.DingAlarmConstants.DING_NOTICE_TXT;

/**
 * Send ding notification message.
 *
 * @author chen.ma
 * @date 2021/8/15 15:49
 */
@Slf4j
@AllArgsConstructor
public class DingSendMessageHandler implements SendMessageHandler {

    private final String active;

    private final InstanceInfo instanceInfo;

    @Override
    public String getType() {
        return NotifyPlatformEnum.DING.name();
    }

    @Override
    public void sendAlarmMessage(NotifyDTO notifyConfig, DynamicThreadPoolExecutor pool) {
        dingAlarmSendMessage(notifyConfig, pool);
    }

    @Override
    public void sendChangeMessage(NotifyDTO notifyConfig, PoolParameterInfo parameter) {
        dingChangeSendMessage(notifyConfig, parameter);
    }

    private void dingAlarmSendMessage(NotifyDTO notifyConfig, DynamicThreadPoolExecutor pool) {
        String[] receives = notifyConfig.getReceives().split(",");
        String afterReceives = Joiner.on(", @").join(receives);

        BlockingQueue<Runnable> queue = pool.getQueue();
        String text = String.format(
                DING_ALARM_TXT,
                // 环境
                active.toUpperCase(),
                // 线程池ID
                pool.getThreadPoolId(),
                // 应用名称
                instanceInfo.getAppName(),
                // 实例信息
                instanceInfo.getIdentify(),
                // 报警类型
                notifyConfig.getTypeEnum(),
                // 核心线程数
                pool.getCorePoolSize(),
                // 最大线程数
                pool.getMaximumPoolSize(),
                // 当前线程数
                pool.getPoolSize(),
                // 活跃线程数
                pool.getActiveCount(),
                // 最大任务数
                pool.getLargestPoolSize(),
                // 线程池任务总量
                pool.getCompletedTaskCount(),
                // 队列类型名称
                queue.getClass().getSimpleName(),
                // 队列容量
                queue.size() + queue.remainingCapacity(),
                // 队列元素个数
                queue.size(),
                // 队列剩余个数
                queue.remainingCapacity(),
                // 拒绝策略名称
                pool.getRejectedExecutionHandler().getClass().getSimpleName(),
                // 拒绝策略次数
                pool.getRejectCount(),
                // 告警手机号
                afterReceives,
                // 报警频率
                notifyConfig.getInterval(),
                // 当前时间
                DateUtil.now()
        );

        execute(notifyConfig, DingAlarmConstants.DING_ALARM_TITLE, text, Lists.newArrayList(receives));
    }

    private void dingChangeSendMessage(NotifyDTO notifyConfig, PoolParameterInfo parameter) {
        String threadPoolId = parameter.getTpId();
        DynamicThreadPoolWrapper poolWrap = GlobalThreadPoolManage.getExecutorService(threadPoolId);
        if (poolWrap == null) {
            log.warn("Thread pool is empty when sending change notification, threadPoolId :: {}", threadPoolId);
            return;
        }

        String[] receives = notifyConfig.getReceives().split(",");
        String afterReceives = Joiner.on(", @").join(receives);

        ThreadPoolExecutor customPool = poolWrap.getExecutor();
        /**
         * hesitant e.g. ➲  ➜  ⇨  ➪
         */
        String text = String.format(
                DING_NOTICE_TXT,
                // 环境
                active.toUpperCase(),
                // 线程池名称
                threadPoolId,
                // 应用名称
                instanceInfo.getAppName(),
                // 实例信息
                instanceInfo.getIdentify(),
                // 核心线程数
                customPool.getCorePoolSize() + "  ➲  " + parameter.getCoreSize(),
                // 最大线程数
                customPool.getMaximumPoolSize() + "  ➲  " + parameter.getMaxSize(),
                // 核心线程超时
                customPool.allowsCoreThreadTimeOut() + "  ➲  " + EnableEnum.getBool(parameter.getAllowCoreThreadTimeOut()),
                // 线程存活时间
                customPool.getKeepAliveTime(TimeUnit.SECONDS) + "  ➲  " + parameter.getKeepAliveTime(),
                // 阻塞队列
                QueueTypeEnum.getBlockingQueueNameByType(parameter.getQueueType()),
                // 阻塞队列容量
                (customPool.getQueue().size() + customPool.getQueue().remainingCapacity()) + "  ➲  " + parameter.getCapacity(),
                // 拒绝策略
                customPool.getRejectedExecutionHandler().getClass().getSimpleName(),
                RejectedTypeEnum.getRejectedNameByType(parameter.getRejectedType()),
                // 告警手机号
                afterReceives,
                // 当前时间
                DateUtil.now()
        );

        execute(notifyConfig, DingAlarmConstants.DING_NOTICE_TITLE, text, Lists.newArrayList(receives));
    }

    private void execute(NotifyDTO notifyConfig, String title, String text, List<String> mobiles) {
        String serverUrl = DingAlarmConstants.DING_ROBOT_SERVER_URL + notifyConfig.getSecretKey();
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
