package com.github.dynamic.threadpool.starter.alarm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.github.dynamic.threadpool.common.model.InstanceInfo;
import com.github.dynamic.threadpool.starter.toolkit.thread.CustomThreadPoolExecutor;
import com.taobao.api.ApiException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

/**
 * Ding Send.
 *
 * @author chen.ma
 * @date 2021/8/15 15:49
 */
@Slf4j
@RequiredArgsConstructor
public class DingSendMessageHandler implements SendMessageHandler {

    @NonNull
    private String active;

    @NonNull
    private InstanceInfo instanceInfo;

    @Override
    public String getType() {
        return SendMessageEnum.DING.name();
    }

    @Override
    public void sendMessage(List<AlarmConfig> alarmConfigs, CustomThreadPoolExecutor pool) {
        Optional<AlarmConfig> alarmConfigOptional = alarmConfigs.stream()
                .filter(each -> Objects.equals(each.getType(), getType()))
                .findFirst();
        alarmConfigOptional.ifPresent(each -> dingSendMessage(each, pool));
    }

    public void dingSendMessage(AlarmConfig alarmConfig, CustomThreadPoolExecutor pool) {
        String serverUrl = alarmConfig.getUrl() + alarmConfig.getToken();

        BlockingQueue<Runnable> queue = pool.getQueue();
        String text = String.format(
                "<font color='#FF0000'>[警报] </font>%s - 动态线程池运行告警 \n\n" +
                        " --- \n\n " +
                        "<font color='#778899' size=2>应用实例：%s</font> \n\n " +
                        "<font color='#708090' size=2>线程池名称：%s</font> \n\n " +
                        " --- \n\n  " +
                        "<font color='#708090' size=2>核心线程数：%d</font> \n\n " +
                        "<font color='#708090' size=2>最大线程数：%d</font> \n\n " +
                        "<font color='#708090' size=2>当前线程数：%d</font> \n\n " +
                        "<font color='#708090' size=2>活跃线程数：%d</font> \n\n " +
                        "<font color='#708090' size=2>最大线程数：%d</font> \n\n " +
                        "<font color='#708090' size=2>线程池任务总量：%d</font> \n\n " +
                        " --- \n\n  " +
                        "<font color='#708090' size=2>队列类型：%s</font> \n\n " +
                        "<font color='#708090' size=2>队列总容量：%d</font> \n\n " +
                        "<font color='#708090' size=2>队列元素个数：%d</font> \n\n " +
                        "<font color='#708090' size=2>队列剩余个数：%d</font> \n\n " +
                        " --- \n\n  " +
                        "<font color='#708090' size=2>拒绝策略次数：</font><font color='#FF0000' size=2>%d</font> \n\n " +
                        "<font color='#708090' size=2>OWNER：@%s</font> \n\n" +
                        "<font color='#708090' size=2>提示：5 分钟内此线程池不会重复告警（可配置）</font> \n\n" +
                        " --- \n\n  " +
                        "**播报时间：%s**",

                // 环境
                active.toUpperCase(),
                // 节点信息
                instanceInfo.getIpApplicationName(),
                // 线程池ID
                pool.getThreadPoolId(),
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
                // 拒绝策略次数
                pool.getRejectCount(),
                // 告警手机号
                "15601166691",
                // 当前时间
                DateUtil.now()

        );

        DingTalkClient dingTalkClient = new DefaultDingTalkClient(serverUrl);
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");

        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("动态线程池告警");
        markdown.setText(text);

        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(CollUtil.newArrayList("15601166691"));

        request.setAt(at);
        request.setMarkdown(markdown);

        try {
            dingTalkClient.execute(request);
        } catch (ApiException ex) {
            log.error("Ding failed to send message", ex.getMessage());
        }
    }

}
