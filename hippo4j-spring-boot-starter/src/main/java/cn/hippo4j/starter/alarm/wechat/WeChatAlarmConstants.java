package cn.hippo4j.starter.alarm.wechat;

/**
 * Ding alarm constants.
 *
 * @author chen.ma
 * @date 2021/11/26 20:03
 */
public class WeChatAlarmConstants {

    /**
     * 企微机器人 Url
     */
    public static final String WE_CHAT_SERVER_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=";

    /**
     * 线程池报警通知文本
     */
    public static final String WE_CHAT_ALARM_TXT =
            "### <font color='#FF0000'>[警报] </font>%s - 动态线程池运行告警 \n" +
                    "> 线程池ID：<font color=\"warning\">%s</font> \n" +
                    "> 应用名称：<font color=\"warning\">%s</font> \n" +
                    "> 应用实例：%s \n" +
                    "> 报警类型：%s \n" +
                    "> 核心线程数：%s \n" +
                    "> 最大线程数：%s \n" +
                    "> 当前线程数：%s \n" +
                    "> 活跃线程数：%s \n" +
                    "> 最大任务数：%s \n" +
                    "> 线程池任务总量：%s \n" +
                    "> 队列类型：%s \n" +
                    "> 队列容量：%s \n" +
                    "> 队列元素个数：%s \n" +
                    "> 队列剩余个数：%s \n" +
                    "> 拒绝策略：%s \n" +
                    "> 拒绝策略执行次数：%s \n" +
                    "> OWNER：@%s \n" +
                    "> 提示：%d 分钟内此线程池不会重复告警（可配置） \n\n" +
                    "**播报时间：%s**";

    /**
     * 线程池参数变更通知文本
     */
    public static final String WE_CHAT_NOTICE_TXT =
            "### <font color=\"info\">[通知] </font>%s - 动态线程池参数变更 \n" +
                    "> 线程池ID：<font color=\"warning\">%s</font> \n" +
                    "> 应用名称：<font color=\"warning\">%s</font> \n" +
                    "> 应用实例：%s \n" +
                    "> 核心线程数：%s \n" +
                    "> 最大线程数：%s \n" +
                    "> 核心线程超时：%s \n" +
                    "> 线程存活时间：%s / SECONDS \n" +
                    "> 队列类型：%s \n" +
                    "> 队列容量：%s \n" +
                    "> AGO 拒绝策略：%s \n" +
                    "> NOW 拒绝策略：%s \n" +
                    "> OWNER：<@%s> \n" +
                    "> 提示：动态线程池配置变更实时通知（无限制） \n\n" +
                    "**播报时间：%s**";

}
