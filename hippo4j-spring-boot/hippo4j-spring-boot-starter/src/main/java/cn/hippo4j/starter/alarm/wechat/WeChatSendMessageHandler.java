package cn.hippo4j.starter.alarm.wechat;

import cn.hippo4j.common.enums.EnableEnum;
import cn.hippo4j.common.model.InstanceInfo;
import cn.hippo4j.common.model.PoolParameterInfo;
import cn.hippo4j.common.toolkit.JSONUtil;
import cn.hippo4j.starter.alarm.NotifyDTO;
import cn.hippo4j.starter.alarm.NotifyPlatformEnum;
import cn.hippo4j.starter.alarm.SendMessageHandler;
import cn.hippo4j.starter.core.DynamicThreadPoolExecutor;
import cn.hippo4j.starter.core.GlobalThreadPoolManage;
import cn.hippo4j.starter.toolkit.thread.QueueTypeEnum;
import cn.hippo4j.starter.toolkit.thread.RejectedTypeEnum;
import cn.hippo4j.starter.wrapper.DynamicThreadPoolWrapper;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import com.google.common.base.Joiner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.hippo4j.starter.alarm.wechat.WeChatAlarmConstants.*;

/**
 * WeChat send message handler.
 *
 * @author chen.ma
 * @date 2021/11/26 20:06
 */
@Slf4j
@AllArgsConstructor
public class WeChatSendMessageHandler implements SendMessageHandler {

    private final String active;

    private final InstanceInfo instanceInfo;

    @Override
    public String getType() {
        return NotifyPlatformEnum.WECHAT.name();
    }

    @Override
    public void sendAlarmMessage(NotifyDTO notifyConfig, DynamicThreadPoolExecutor pool) {
        String[] receives = notifyConfig.getReceives().split(",");
        String afterReceives = Joiner.on(", @").join(receives);

        BlockingQueue<Runnable> queue = pool.getQueue();
        String text = String.format(
                WE_CHAT_ALARM_TXT,
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

        execute(notifyConfig, text);
    }

    @Override
    public void sendChangeMessage(NotifyDTO notifyConfig, PoolParameterInfo parameter) {
        String threadPoolId = parameter.getTpId();
        DynamicThreadPoolWrapper poolWrap = GlobalThreadPoolManage.getExecutorService(threadPoolId);
        if (poolWrap == null) {
            log.warn("Thread pool is empty when sending change notification, threadPoolId :: {}", threadPoolId);
            return;
        }

        String[] receives = notifyConfig.getReceives().split(",");
        String afterReceives = Joiner.on("><@").join(receives);

        ThreadPoolExecutor customPool = poolWrap.getExecutor();
        String text = String.format(
                WE_CHAT_NOTICE_TXT,
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

        execute(notifyConfig, text);
    }

    private void execute(NotifyDTO notifyConfig, String text) {
        String serverUrl = WE_CHAT_SERVER_URL + notifyConfig.getSecretKey();

        try {
            WeChatReqDTO weChatReq = new WeChatReqDTO();
            weChatReq.setMsgtype("markdown");

            Markdown markdown = new Markdown();
            markdown.setContent(text);
            weChatReq.setMarkdown(markdown);

            HttpRequest.post(serverUrl).body(JSONUtil.toJSONString(weChatReq)).execute();
        } catch (Exception ex) {
            log.error("WeChat failed to send message", ex);
        }
    }

    @Data
    @Accessors(chain = true)
    public static class WeChatReqDTO {

        /**
         * msgType
         */
        private String msgtype;

        /**
         * markdown
         */
        private Markdown markdown;

    }

    @Data
    public static class Markdown {

        /**
         * content
         */
        private String content;

    }

}
